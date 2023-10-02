/*
 * Copyright 2023 Adrian Herscu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.aherscu.qa.jgiven.rabbitmq.utils;

import static com.google.common.base.Suppliers.*;
import static java.lang.Runtime.*;
import static java.util.Collections.*;
import static java.util.Objects.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.jooq.lambda.*;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.*;

import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Retrieves RabbitMQ messages from specified queue via provided channel.
 * Messages are converted by specified value function and indexed by specified
 * key function.
 *
 * <p>
 * Messages are consumed in background threads and made available for retrieval
 * by their key according to a specified retry policy. If a retry policy is not
 * specified, then an internal default will be used.
 * </p>
 *
 * <p>
 * Typical workflow:
 * </p>
 * <ol>
 * <li>build it</li>
 * <li>start consumption -- this will run in background until closing</li>
 * <li>retrieve messages</li>
 * </ol>
 *
 *
 * @param <K>
 *            type of message-key; it should have a proper hash function in
 *            order to get O(1) access time, otherwise it may degrade to O(n)
 * @param <V>
 *            type of message-value
 */
@SuperBuilder
@Slf4j
public class QueueHandler<K, V> implements AutoCloseable {
    /**
     * Utility for building a singleton QueueHandler.
     *
     * <p>
     * Build it:
     * 
     * <pre>
     * private final Supplier<QueueHandler<YourKeyType, YourContentType>> queueHandlerSupplier =
     *     QueueHandler.Singleton.<UUID, RetrieveTransactionResponse> builder()
     *         .queue(queueName())
     *         .connectionFactory(amqpConnectionFactory())
     *         .indexingBy(message -> message.content.yourKey)
     *         .consumingBy(
     *             Unchecked.function(YourContentType::deserializeFromBytes))
     *         .publishingBy(YourContentType::serializeToBytes)
     *         .build()
     *         .supplier();
     * </pre>
     *
     * and, later:
     *
     * <pre>
     * public QueueHandler<YourKeyType, YourContentType> queueHandler() {
     *     return queueHandlerSupplier.get();
     * }
     * </pre>
     *
     * It is guaranteed that calling {@code queueHandler()} <strong>from any
     * thread</strong>, will supply the same QueueHandler instance.
     * </p>
     * 
     * <p>
     * The connection is internally managed and closed via a runtime shutdown
     * hook.
     * </p>
     * 
     * @param <K>
     *            type of message-key; it should have a proper hash function in
     *            order to get O(1) access time, otherwise it may degrade to
     *            O(n)
     * @param <V>
     *            type of message-value
     */
    @Builder
    public static final class Singleton<K, V> {
        public final ConnectionFactory       connectionFactory;
        public final String                  queue;
        public final Function<Message<V>, K> indexingBy;
        public final Function<byte[], V>     consumingBy;
        public final Function<V, byte[]>     publishingBy;

        /**
         * @return a memoized supplier of QueueHandler
         */
        public Supplier<QueueHandler<K, V>> supplier() {
            return memoize(() -> {
                final Connection connection;
                final QueueHandler<K, V> queueHandler;
                try {
                    log.debug("creating connection");
                    connection = connectionFactory.newConnection();
                    log.debug("setting-up queue handler");
                    queueHandler = QueueHandler.<K, V> builder()
                        .channel(connection.createChannel())
                        .queue(queue)
                        .indexingBy(indexingBy)
                        .consumingBy(consumingBy)
                        .publishingBy(publishingBy)
                        .build();
                } catch (final IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
                getRuntime().addShutdownHook(new Thread(() -> {
                    // NOTE: apparently the only way to purge unacked messages
                    // is
                    // via management interface
                    // see
                    // https://www.rabbitmq.com/amqp-0-9-1-reference.html#queue.purge
                    try {
                        log.debug("closing connection");
                        connection.close();
                    } catch (final IOException ioe) {
                        log.warn("connection already closed {}",
                            ioe.toString());
                    }
                }));
                return queueHandler;
            });
        }
    }

    public final Channel                       channel;
    public final String                        queue;
    public final Function<Message<V>, K>       indexingBy;
    public final Function<byte[], V>           consumingBy;
    public final Function<V, byte[]>           publishingBy;

    // TODO allow subclasses to provide their own data-structure, e.g. a cache
    private final ConcurrentMap<K, Message<V>> recievedMessages =
        new ConcurrentHashMap<>();
    private String                             consumerTag;

    /**
     * @param uri
     *            an amqp or amqps uri; e.g.,
     *            amqps://username:password@host/virtual-host
     * @return a connection factory
     * @throws RuntimeException
     *             upon malformed uri
     */
    @SneakyThrows
    public static ConnectionFactory connectionFactoryFrom(final String uri) {
        val connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(uri);
        connectionFactory.setExceptionHandler(new ForgivingExceptionHandler());
        return connectionFactory;
    }

    /**
     * Cancels the consumption, previously started by {@link #consume()}. Must
     * be called on same thread as last {@link #consume()} call.
     *
     * @throws IOException
     *             upon connection failures, or if consumer was not started
     * @return the consumer tag
     */
    public String cancel() throws IOException {
        if (isNull(consumerTag)) {
            throw new IOException("consumer not started");
        }
        log.debug("cancelling consumer by tag {}", consumerTag);
        channel.basicCancel(consumerTag);
        return consumerTag;
    }

    @Override
    public void close() throws IOException {
        try {
            cancel();
        } catch (final Exception e) {
            log.error("while closing got {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Starts the message consumption process and returns immediately.
     *
     * @return consumer tag
     * @throws RuntimeException
     *             upon connection failures
     * @see #cancel() for canceling the consumption
     */
    @SneakyThrows
    public String consume() {
        channel.basicQos(16);
        consumerTag = channel.basicConsume(queue,
            new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                    @SuppressWarnings("hiding") final String consumerTag,
                    final Envelope envelope,
                    final AMQP.BasicProperties properties, final byte[] body)
                    throws IOException {
                    final K key;
                    final Message<V> message;
                    try {
                        message = Message.<V> builder()
                            .content(consumingBy.apply(body))
                            .properties(properties)
                            .build();
                        key = indexingBy.apply(message);
                        log.trace("received {}", key);
                    } catch (final Exception e) {
                        log.warn("skipping unknown type {}", e.getMessage());
                        channel.basicReject(envelope.getDeliveryTag(), true);
                        return;
                    }
                    recievedMessages.put(key, message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });

        log.debug("set-up consumer with tag {}", consumerTag);
        return consumerTag;
    }

    public void publish(final Stream<Message<V>> messages) {
        messages.parallel()
            .peek(message -> log.trace("publishing {}", message))
            .forEach(Unchecked.consumer(
                message -> channel.basicPublish(StringUtils.EMPTY, queue,
                    message.properties, publishingBy.apply(message.content))));
    }

    public void publishValues(final Stream<V> values) {
        publish(values
            .map(value -> Message.<V> builder().content(value).build()));
    }

    /**
     * Unmodifiable view of retrieved messages.
     *
     * @return the messages
     */
    public Map<K, Message<V>> recievedMessages() {
        return unmodifiableMap(recievedMessages);
    }
}
