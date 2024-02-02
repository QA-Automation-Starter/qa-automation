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

package dev.aherscu.qa.testing.utils;

import static dev.aherscu.qa.testing.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.IOUtilsExtensions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import org.testng.annotations.*;

import lombok.*;

public class ClassUtilsExtensionsTest {

    @Test
    @SneakyThrows
    public void shouldGetRelativeResourceAsStream() {
        try (val resourceStream = getRelativeResourceAsStream(
            ClassUtilsExtensions.class,
            ClassUtilsExtensions.class.getSimpleName())) {
            assertThat(fromUTF8(resourceStream), is("test"));
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    @SneakyThrows
    public void shouldNotGetRelativeResourceAsStream() {
        try (val resourceStream = getRelativeResourceAsStream(
            ClassUtilsExtensions.class, "other")) {
            fail(); // should not reach here
        }
    }
}