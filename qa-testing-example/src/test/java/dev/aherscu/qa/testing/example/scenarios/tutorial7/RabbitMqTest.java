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

package dev.aherscu.qa.testing.example.scenarios.tutorial7;

import static dev.aherscu.qa.tester.utils.StreamMatchersExtensions.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.rabbitmq.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;
import dev.aherscu.qa.tester.utils.config.*;
import lombok.*;

// TODO to make this test run under Github Actions
// see https://github.com/marketplace/actions/rabbitmq-in-github-actions
public class RabbitMqTest
    extends AbstractRabbitMqTest<Integer, String> {

    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     */
    protected RabbitMqTest() {
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
                    .content("hello")
                    .build(),
                Message.<String> builder()
                    .content("world")
                    .build()))
            .and().consuming();

        then()
            .the_retrieved_messages(adaptedStream(message -> message.content,
                hasSpecificItems("world", "hello")));
    }

    @BeforeMethod
    @Override
    @SneakyThrows
    protected void beforeMethodInitiateQueueHandler() {
        @SuppressWarnings("resource")
        // according to docs channels are closed when connection is closed
        val testingChannel = connection.createChannel();
        queueHandler = QueueHandler.<Integer, String> builder()
            .channel(testingChannel)
            .queue(testingChannel.queueDeclare().getQueue())
            .indexingBy(message -> message.content.hashCode())
            .consumingBy(bytes -> new String(bytes, UTF_8))
            .publishingBy(String::getBytes)
            .build();
    }
}
