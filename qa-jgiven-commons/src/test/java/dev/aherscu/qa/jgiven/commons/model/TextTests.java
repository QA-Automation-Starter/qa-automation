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

package dev.aherscu.qa.jgiven.commons.model;

import static dev.aherscu.qa.tester.utils.ObjectMapperUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import lombok.*;
import lombok.extern.jackson.*;

public class TextTests {
    @Test
    public final void shouldSerialize() {
        assertThat(toJson(Foo.builder()
            .text(new Text("blah"))
            .name(new Name("kuku"))
            .password(new Password("123"))
            .build()),
            equalToCompressingWhiteSpace(
                "{\"text\":\"blah\"" +
                    ",\"password\":\"123\"" +
                    ",\"name\":\"kuku\"}"));
    }

    @Test
    public final void shouldDeserialize() {
        assertThat(fromJson("{\"text\":\"blah\"}", Foo.class),
            equalTo(Foo.builder().text(new Text("blah")).build()));
    }

    @Jacksonized // meaningful with @Builder/@SuperBuilder
    @Builder // No-args constructor required by Jackson deserialization
    @EqualsAndHashCode
    static class Foo {
        public final Text     text;
        public final Password password;
        public final Name     name;
    }
}