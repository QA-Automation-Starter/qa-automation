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

package dev.aherscu.qa.testing.rabbitmq.scenarios;

import static dev.aherscu.qa.tester.utils.StreamMatchersExtensions.*;
import static dev.aherscu.qa.testing.rabbitmq.utils.AbstractQueueHandlerTest.*;
import static java.nio.charset.StandardCharsets.*;

import java.util.stream.*;

import org.testng.annotations.*;

import com.rabbitmq.client.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.tester.utils.config.*;
import dev.aherscu.qa.testing.rabbitmq.*;
import dev.aherscu.qa.testing.rabbitmq.actions.*;
import dev.aherscu.qa.testing.rabbitmq.fixtures.*;
import dev.aherscu.qa.testing.rabbitmq.model.*;
import dev.aherscu.qa.testing.rabbitmq.utils.*;
import dev.aherscu.qa.testing.rabbitmq.verifications.*;
import lombok.*;

public class RabbitMqTest extends
    UnitilsScenarioTest<TestConfiguration, RabbitMqScenarioType, RabbitMqFixtures<Integer, String, ?>, RabbitMqActions<Integer, String, ?>, RabbitMqVerifications<Integer, String, ?>> {
    private Connection                    connection;
    private QueueHandler<Integer, String> messagesRetriever;

    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     *
     * @param configurationType
     *            type of configuration
     */
    protected RabbitMqTest() {
        super(TestConfiguration.class);
    }

    @BeforeClass
    @SneakyThrows
    protected void beforeClassOpenChannel() {
        connection = LOCAL_RABBITMQ.newConnection();
        val testingChannel = connection.createChannel();
        messagesRetriever = QueueHandler.<Integer, String> builder()
            .channel(testingChannel)
            .queue(testingChannel.queueDeclare().getQueue())
            .indexingBy(String::hashCode)
            .consumingBy(bytes -> new String(bytes, UTF_8))
            .publishingBy(String::getBytes)
            .build();
    }

    @AfterClass(alwaysRun = true)
    @SneakyThrows
    protected void afterClassCloseChannel() {
        connection.close();
    }

    @Test
    @SneakyThrows
    public void shouldRetrieveMessageFromRabbitMq() {
        given()
            .a_queue(messagesRetriever);

        when()
            .publishing(Stream.of(
                Message.<String> builder()
                    .content("hello")
                    .build(),
                Message.<String> builder()
                    .content("world")
                    .build()))
            .and().consuming();

        then()
            .the_retrieved_messages(adaptedStream(message -> message.content,
                // ISSUE world does not arrive...
                hasSpecificItems("hello", "world")));
    }
}
