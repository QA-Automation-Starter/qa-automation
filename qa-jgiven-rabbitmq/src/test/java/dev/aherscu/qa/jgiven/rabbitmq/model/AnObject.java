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

package dev.aherscu.qa.jgiven.rabbitmq.model;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;

import java.util.stream.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

import lombok.*;
import lombok.extern.jackson.*;

@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public final class AnObject {
    public final static AnObject DUMMY        = AnObject.builder()
        .id("dummy")
        .build();
    final static ObjectMapper    objectMapper = new ObjectMapper();
    public final static String   DUMMY_JSON   = DUMMY.toJson();
    final static byte[]          DUMMY_BYTES  = DUMMY_JSON.getBytes(UTF_8);

    @JsonProperty
    public final String          id;

    @SneakyThrows
    static AnObject fromBytes(final JsonNode jsonNode) {
        return objectMapper.readerFor(AnObject.class)
            .readValue(requireNonNull(jsonNode, "must supply a JSON node"));
    }

    public static Stream<AnObject> generate(final IntStream ids) {
        return ids
            .mapToObj(c -> AnObject.builder().id(Integer.toString(c)).build());
    }

    @SneakyThrows
    public static AnObject fromBytes(byte[] bytes) {
        return objectMapper.readerFor(AnObject.class).readValue(bytes);
    }

    @SneakyThrows
    String toJson() {
        return objectMapper.writeValueAsString(this);
    }

    public byte[] asBytes() {
        return toJson().getBytes(UTF_8);
    }

    public String id() {
        return id;
    }
}
