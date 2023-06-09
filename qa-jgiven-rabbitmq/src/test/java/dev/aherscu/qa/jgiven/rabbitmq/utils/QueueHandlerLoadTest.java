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
import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

// IMPORTANT: you should have RabbitMQ installed on your machine in order to run this test.
// On CICD machines (e.g. Jenkins) you may ensure this by running cinst rabbitmq or similar during prebuild phase
@Slf4j
public class QueueHandlerLoadTest extends AbstractQueueHandlerTest {

    @Test
    @Parameters("message-quantity")
    @SneakyThrows
    public void shouldRetrieveManyObjectsFromRabbit(
        @Optional("1000") final int messageQuantity) {
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel();
            val queueHandler = QueueHandler.<String, AnObject> builder()
                .channel(channel)
                .queue(channel.queueDeclare().getQueue())
                .indexingBy(message -> message.content.id)
                .consumingBy(AnObject::fromBytes)
                .publishingBy(AnObject::asBytes)
                .build()) {

            queueHandler.consume();

            queueHandler.publishValues(AnObject
                .generate(range(0, messageQuantity)));

            AnObject
                .generate(range(0, messageQuantity))
                .parallel()
                .forEach(anObject -> Failsafe.with(retryPolicy)
                    .run(() -> assertThat(
                        queueHandler.recievedMessages()
                            .get(anObject.id).content,
                        is(anObject))));
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
            val queueHandler = QueueHandler.<String, AnObject> builder()
                .channel(connection.createChannel())
                .queue(channel.queueDeclare().getQueue())
                .indexingBy(message -> message.content.id)
                .consumingBy(AnObject::fromBytes)
                .publishingBy(AnObject::asBytes)
                .build()) {

            queueHandler.consume();

            queueHandler.channel.basicPublish(EMPTY,
                queueHandler.queue, null,
                noise.getBytes(UTF_8));

            allOf(
                runAsync(() -> queueHandler
                    .publishValues(AnObject
                        .generate(range(0, messageQuantity)))),
                runAsync(() -> AnObject
                    .generate(range(0, messageQuantity))
                    .parallel()
                    .forEach(anObject -> Failsafe.with(retryPolicy)
                        .run(() -> assertThat(
                            queueHandler.recievedMessages()
                                .get(anObject.id).content,
                            is(anObject))))))
                .get();
        }
    }
}
