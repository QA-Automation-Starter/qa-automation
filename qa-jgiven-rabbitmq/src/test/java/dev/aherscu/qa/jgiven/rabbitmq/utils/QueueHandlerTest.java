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

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import lombok.*;
import net.jodah.failsafe.*;

public class QueueHandlerTest extends AbstractQueueHandlerTest {

    @Test
    @SneakyThrows
    public void shouldHaveAnWorkingRabbitMq() {
        val randomString = RandomStringUtils.random(10);
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel()) {
            val queueName = channel.queueDeclare().getQueue();
            channel.basicPublish(EMPTY, queueName, null,
                randomString.getBytes(UTF_8));
            assertThat(
                new String(channel.basicGet(queueName, true).getBody(), UTF_8),
                is(randomString));
        }
    }

    @Test
    @SneakyThrows
    public void shouldRetrieveOneMessage() {
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel();
            // NOTE lombok.val with Eclipse Java Compiler does not work here
            final QueueHandler<Integer, String> queueHandler =
                QueueHandler.<Integer, String> builder()
                    .channel(channel)
                    .queue(channel.queueDeclare().getQueue())
                    .indexingBy(message -> message.content.hashCode())
                    .consumingBy(bytes -> new String(bytes, UTF_8))
                    .publishingBy(string -> string.getBytes(UTF_8))
                    .build()) {

            queueHandler.publishValues(Stream.of(new String[] { "a", "b", "c" }));

            queueHandler.consume();

            Failsafe.with(retryPolicy)
                .run(() -> assertThat(queueHandler
                    .recievedMessages()
                    .values(),
                    hasSize(3)));
        }
    }
}
