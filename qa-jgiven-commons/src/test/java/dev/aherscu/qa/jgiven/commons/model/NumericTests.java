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

import static dev.aherscu.qa.testing.utils.ObjectMapperUtils.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import lombok.*;
import lombok.extern.jackson.*;

public class NumericTests {

    @Test
    public final void shouldSerialize() {
        assertThat(deleteWhitespace(toJson(Foo.builder()
            .number(new LongNumeric(777L))
            .doubleNumeric(new DoubleNumeric(3.14))
            .id(new IntId(123))
                        .build())),
            equalToCompressingWhiteSpace(
                "{\"number\":777"
                    + ",\"doubleNumeric\":3.14"
                    + ",\"id\":123}"));
    }

    @Test
    public final void shouldDeserialize() {
        assertThat(fromJson("{\"number\":777"
            + ",\"doubleNumeric\":3.14"
            + ",\"id\":123}", Foo.class),
            equalTo(Foo.builder()
                .number(new LongNumeric(777L))
                .doubleNumeric(new DoubleNumeric(3.14))
                .id(new IntId(123))
                .build()));
    }

    @Jacksonized // meaningful with @Builder/@SuperBuilder
    @Builder // No-args constructor required by Jackson deserialization
    @EqualsAndHashCode
    static class Foo {
        public final LongNumeric   number;
        public final DoubleNumeric doubleNumeric;
        public final IntId         id;
    }

    static class DoubleNumeric extends Numeric<Double> {
        public DoubleNumeric(Double value) {
            super(value);
        }
    }

    static class LongNumeric extends Numeric<Long> {
        public LongNumeric(Long value) {
            super(value);
        }
    }

    static class IntId extends NumericId<Integer> {
        public IntId(Integer value) {
            super(value);
        }
    }
}
