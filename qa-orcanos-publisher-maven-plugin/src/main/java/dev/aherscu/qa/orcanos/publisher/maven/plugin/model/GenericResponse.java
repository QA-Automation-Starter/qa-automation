/*
 * Copyright 2022 Adrian Herscu
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

package dev.aherscu.qa.orcanos.publisher.maven.plugin.model;

import java.net.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

/**
 * Models a response from various Orcanos REST API methods.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public final class GenericResponse {
    @JsonProperty("IsSuccess")
    public boolean isSuccess; // erroneously returns false
    @JsonProperty("Data")
    public Object  data;
    @JsonProperty("Message")
    public String  message;
    @JsonProperty("HttpCode")
    public int     httpCode;

    @SneakyThrows
    public URI href() {
        if (data instanceof Map) {
            return new URI(((Map<?, ?>) data)
                .values()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                    "missing href in " + data.toString()))
                .toString());
        }

        throw new RuntimeException("no map in " + data);
    }
}
