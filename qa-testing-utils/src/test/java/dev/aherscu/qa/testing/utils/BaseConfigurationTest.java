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

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.configuration.*;
import org.testng.annotations.*;

import com.google.common.collect.*;

import dev.aherscu.qa.testing.utils.config.BaseConfiguration;

@Test
public class BaseConfigurationTest {
    @Test
    public void shouldGroupProperties() {
        assertThat(new BaseConfiguration(
            new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("my.group1.foo", "group-1-foo")
                .put("my.group2.foo", "group-2-foo")
                .put("my.group1.goo", "group-1-goo")
                .put("my.group2.goo", "group-2-goo")
                .build()))
            .groupsOf("my.group"))
            .containsExactly(
                ImmutableMap.<String, String> builder()
                    .put("foo", "group-1-foo")
                    .put("goo", "group-1-goo")
                    .build(),
                ImmutableMap.<String, String> builder()
                    .put("foo", "group-2-foo")
                    .put("goo", "group-2-goo")
                    .build());
    }
}
