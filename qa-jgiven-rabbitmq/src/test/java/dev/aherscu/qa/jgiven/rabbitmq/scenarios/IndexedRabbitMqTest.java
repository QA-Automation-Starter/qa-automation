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

import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import com.rabbitmq.client.*;

import dev.aherscu.qa.jgiven.rabbitmq.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;
import dev.aherscu.qa.tester.utils.config.*;
import lombok.*;

public class IndexedRabbitMqTest
    extends AbstractRabbitMqTest<String, String> {

    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     */
    protected IndexedRabbitMqTest() {
        super(TestConfiguration.class);
    }

    @Test
    @SneakyThrows
    public void shouldRetrieve() {
        given()
            .a_queue(queueHandler);

        when()
            .publishing(asList(
                Message.<String> builder()
                    .properties(new AMQP.BasicProperties().builder()
                        .messageId("1").build())
                    .content("hello")
                    .build(),
                Message.<String> builder()
                    .properties(new AMQP.BasicProperties().builder()
                        .messageId("2").build())
                    .content("world")
                    .build()))
            .and().consuming();

        then()
            .the_message_with_$_key("2",
                is(Message.<String> builder()
                    .properties(new AMQP.BasicProperties().builder()
                        .messageId("2").build())
                    .content("world")
                    .build()));
    }

    @BeforeMethod
    @Override
    @SneakyThrows
    protected void beforeMethodInitiateQueueHandler() {
        // according to docs channels are closed when connection is closed
        val testingChannel = connection.createChannel();
        queueHandler = QueueHandler.<String, String> builder()
            .channel(testingChannel)
            .queue(testingChannel.queueDeclare().getQueue())
            .indexingBy(message -> message.properties.getMessageId())
            .consumingBy(bytes -> new String(bytes, UTF_8))
            .publishingBy(String::getBytes)
            .build();
    }
}
