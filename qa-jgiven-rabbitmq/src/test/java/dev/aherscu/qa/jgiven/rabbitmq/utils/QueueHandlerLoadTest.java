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

import static java.lang.Integer.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.IntStream.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import com.rabbitmq.client.*;

import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import lombok.*;
import net.jodah.failsafe.*;

public class QueueHandlerLoadTest extends AbstractQueueHandlerTest {

    @Test
    @Parameters("message-quantity")
    @SneakyThrows
    public void shouldRetrieveManyObjectsFromRabbit(
        @Optional("1000") final int messageQuantity) {
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel();
            // NOTE lombok.val with Eclipse Java Compiler does not work here
            final QueueHandler<String, String> queueHandler =
                QueueHandler.<String, String> builder()
                    .channel(channel)
                    .queue(channel.queueDeclare().getQueue())
                    .indexingBy(message -> message.properties.getMessageId())
                    .consumingBy(bytes -> new String(bytes, UTF_8))
                    .publishingBy(String::getBytes)
                    .build()) {

            queueHandler.consume();

            queueHandler.publish(range(0, messageQuantity)
                .mapToObj(String::valueOf)
                .map(id -> Message.<String> builder()
                    .content("any-content-" + id)
                    .properties(new AMQP.BasicProperties().builder()
                        .messageId(id)
                        .build())
                    .build()));

            range(0, messageQuantity)
                .parallel()
                .mapToObj(String::valueOf)
                .forEach(id -> Failsafe.with(retryPolicy)
                    .run(() -> assertThat(queueHandler.recievedMessages()
                        .get(id).content,
                        is("any-content-" + id))));
        }
    }

    @Test
    @Parameters("message-quantity")
    @SneakyThrows
    public void shouldRetrieveManyObjectsFromRabbitMqSkippingNoise(
        @Optional("1000") final int messageQuantity) {
        val noise = "noise-" + RandomStringUtils.random(10);
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel();
            // NOTE lombok.val with Eclipse Java Compiler does not work here
            final QueueHandler<Integer, String> queueHandler =
                QueueHandler.<Integer, String> builder()
                    .channel(channel)
                    .queue(channel.queueDeclare().getQueue())
                    // NOTE should fail parsing noise
                    .indexingBy(message -> new Integer(message.content))
                    .consumingBy(bytes -> new String(bytes, UTF_8))
                    .publishingBy(String::getBytes)
                    .build()) {

            queueHandler.consume();

            queueHandler.publishValues(Stream.of(noise));

            allOf(
                runAsync(() -> queueHandler
                    .publishValues(range(0, messageQuantity)
                        .mapToObj(String::valueOf))),
                runAsync(() -> range(0, messageQuantity)
                    .mapToObj(String::valueOf)
                    .parallel()
                    .forEach(id -> Failsafe.with(retryPolicy)
                        .run(() -> assertThat(
                            queueHandler.recievedMessages()
                                .get(parseInt(id)).content,
                            is(id))))))
                .get();
        }
    }
}
