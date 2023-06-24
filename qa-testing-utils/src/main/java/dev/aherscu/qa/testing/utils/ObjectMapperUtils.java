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

package dev.aherscu.qa.testing.utils;

import com.fasterxml.jackson.databind.*;

import lombok.*;
import lombok.experimental.*;

@UtilityClass
public class ObjectMapperUtils {
    public static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static <T> T fromJson(final String json, final Class<T> type) {
        return mapper.readValue(json, type);
    }

    @SneakyThrows
    public static <T> String toJson(final T object) {
        return mapper.writeValueAsString(object);
    }
}
