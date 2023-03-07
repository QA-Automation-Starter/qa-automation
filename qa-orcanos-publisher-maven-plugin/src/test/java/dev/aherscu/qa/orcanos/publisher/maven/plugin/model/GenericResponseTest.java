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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import com.fasterxml.jackson.databind.*;

import lombok.*;

public class GenericResponseTest {

    private final GenericResponse response;

    @SneakyThrows
    public GenericResponseTest() {
        response = new ObjectMapper().readValue(
            "{\"IsSuccess\":false,"
                + "\"Data\":{"
                + "\"DONTCARE\""
                + ":\"https://DONTCARE\""
                + "},"
                + "\"Message\":\"Image/File uploaded successfully. Please check the response data for image/file URL.\","
                + "\"HttpCode\":200}",
            GenericResponse.class);
    }

    @Test
    public void shouldParseHref() {
        assertThat(response.href().toString(), is(
            "https://DONTCARE"));
    }

    @Test
    public void shouldParseSuccess() {
        assertThat(response.isSuccess, is(false));
    }
}