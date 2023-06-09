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

package dev.aherscu.qa.jgiven.rabbitmq.scenarios;

import static org.hamcrest.Matchers.*;

import java.util.stream.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.rabbitmq.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;
import dev.aherscu.qa.tester.utils.config.*;
import lombok.*;

public class IndexedRabbitMqTest
    extends AbstractRabbitMqTest<String, AnObject> {

    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     */
    protected IndexedRabbitMqTest() {
        super(TestConfiguration.class);
    }

    @Test
    @SneakyThrows
    public void shouldRetrieveMessageFromRabbitMq() {
        given()
            .a_queue(queueHandler);

        when()
            .publishing(AnObject.generate(IntStream.range(0, 10))
                .map(anObject -> Message.<AnObject> builder()
                    .content(anObject)
                    .build()))
            .and().consuming();

        then()
            .the_message_with_$_key("0",
                is(Message.<AnObject> builder()
                    .content(AnObject.builder()
                        .id("0")
                        .build())
                    .build()));
    }

    @BeforeMethod
    @Override
    @SneakyThrows
    protected void beforeMethodInitiateQueueHandler() {
        val testingChannel = connection.createChannel();
        queueHandler = QueueHandler.<String, AnObject> builder()
            .channel(testingChannel)
            .queue(testingChannel.queueDeclare().getQueue())
            .indexingBy(message -> message.content.id)
            .consumingBy(AnObject::fromBytes)
            .publishingBy(AnObject::asBytes)
            .build();
    }
}
