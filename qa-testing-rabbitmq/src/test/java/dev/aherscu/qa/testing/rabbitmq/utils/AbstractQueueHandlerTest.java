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

import static dev.aherscu.qa.testing.rabbitmq.utils.QueueHandler.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;

import java.util.stream.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.rabbitmq.client.*;

import lombok.*;
import lombok.extern.jackson.*;

public class AbstractQueueHandlerTest {
    public static final ConnectionFactory LOCAL_RABBITMQ =
        connectionFactoryFrom("amqp://guest:guest@localhost");

    static abstract class Base {
        final static ObjectMapper objectMapper = new ObjectMapper();

        @SneakyThrows
        String toJson() {
            return objectMapper.writeValueAsString(this);
        }

        byte[] asBytes() {
            return toJson().getBytes(UTF_8);
        }

        abstract String id();
    }

    @Builder
    @Jacksonized
    @EqualsAndHashCode(callSuper = false)
    static final class AnObject extends Base {
        final static AnObject DUMMY       =
            AbstractQueueHandlerTest.AnObject.builder().id("dummy").build();
        final static String   DUMMY_JSON  = DUMMY.toJson();
        final static byte[]   DUMMY_BYTES = DUMMY_JSON.getBytes(UTF_8);

        @JsonProperty
        final String          id;

        @SneakyThrows
        static AnObject fromBytes(final JsonNode jsonNode) {
            return objectMapper
                .readerFor(AnObject.class)
                .readValue(requireNonNull(jsonNode, "must supply a JSON node"));
        }

        static Stream<QueueHandlerLoadTest.AnObject> generate(
            final IntStream ids) {
            return ids
                .mapToObj(c -> AbstractQueueHandlerTest.AnObject.builder()
                    .id(Integer.toString(c))
                    .build());
        }

        @SneakyThrows
        static AnObject fromBytes(byte[] bytes) {
            return objectMapper.readerFor(AnObject.class).readValue(bytes);
        }

        @Override
        String id() {
            return id;
        }
    }

    @Builder
    @Jacksonized
    @EqualsAndHashCode(callSuper = false)
    static final class AnotherObject extends Base {
        final static AnotherObject DUMMY       =
            AbstractQueueHandlerTest.AnotherObject.builder()
                .anObject(AnObject.DUMMY).build();
        final static String        DUMMY_JSON  = DUMMY.toJson();
        final static byte[]        DUMMY_BYTES = DUMMY_JSON.getBytes(UTF_8);

        @JsonProperty
        final AnObject             anObject;

        @SneakyThrows
        static AnotherObject from(final JsonNode jsonNode) {
            return objectMapper
                .readerFor(AnotherObject.class)
                .readValue(requireNonNull(jsonNode, "must supply a JSON node"));
        }

        static Stream<QueueHandlerLoadTest.AnotherObject> generate(
            final IntStream ids) {
            return ids.mapToObj(
                c -> AbstractQueueHandlerTest.AnotherObject.builder()
                    .anObject(AnObject.builder()
                        .id(Integer.toString(c))
                        .build())
                    .build());
        }

        @SneakyThrows
        static AnotherObject from(byte[] bytes) {
            return objectMapper.readerFor(AnotherObject.class).readValue(bytes);
        }

        @Override
        String id() {
            return anObject.id();
        }
    }
}
