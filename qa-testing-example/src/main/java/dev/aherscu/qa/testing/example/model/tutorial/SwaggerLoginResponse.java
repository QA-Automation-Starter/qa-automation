/*
 * Copyright 2024 Adrian Herscu
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

package dev.aherscu.qa.testing.example.model.tutorial;

import static dev.aherscu.qa.testing.utils.ObjectMapperUtils.*;

import lombok.*;
import lombok.extern.jackson.*;

@Jacksonized
@Builder
@ToString
@Getter
public class SwaggerLoginResponse {
    public final int    code;
    public final String type;
    public final String message;

    public static SwaggerLoginResponse from(final String json) {
        return fromJson(json, SwaggerLoginResponse.class);
    }
}
