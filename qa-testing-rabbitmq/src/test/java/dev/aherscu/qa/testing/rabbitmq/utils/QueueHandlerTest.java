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

package dev.aherscu.qa.testing.rabbitmq.utils;

import static dev.aherscu.qa.tester.utils.StreamMatchersExtensions.*;
import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import com.fasterxml.jackson.databind.*;

import dev.aherscu.qa.tester.utils.ThreadUtils;
import lombok.*;
import lombok.extern.slf4j.*;

// IMPORTANT: should have RabbitMQ installed on local machine in order to run 
// On GitHub actions must ensure this by installing rabbitmq during prebuild phase
@Slf4j
public class QueueHandlerTest extends AbstractQueueHandlerTest {

    @Test
    @SneakyThrows
    public void selfTest() {
        log.debug(">>>{}", AnObject.DUMMY_JSON);
        log.debug(">>>{}", AnotherObject.DUMMY_JSON);
        log.debug(">>>{}", new ObjectMapper().readTree(AnObject.DUMMY_JSON)
            .findValue("id").asText());
        log.debug(">>>{}", new ObjectMapper().readTree(AnotherObject.DUMMY_JSON)
            .findValue("id").asText());
    }

    @Test
    @SneakyThrows
    public void objectShouldBeEqualById() {
        assertThat(AnObject.DUMMY,
            is(AnObject.builder().id("dummy").build()));
    }

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
    public void shouldRetrieveObjectFromRabbitMq() {
        try (val connection = LOCAL_RABBITMQ.newConnection();
            val channel = connection.createChannel();
            val queueHandler = QueueHandler.<String, AnObject> builder()
                .channel(channel)
                .queue(channel.queueDeclare().getQueue())
                .indexingBy(AnObject::id)
                .consumingBy(AnObject::fromBytes)
                .publishingBy(AnObject::asBytes)
                .build()) {

            queueHandler.publishValues(Stream.of(AnObject.DUMMY));

            queueHandler.consume();

            // TODO get rid of the sleep
            ThreadUtils.sleep(5000);

            assertThat(queueHandler.get().values().stream(),
                adaptedStream(message -> message.content,
                    hasSpecificItems(AnObject.DUMMY)));
        }
    }
}
