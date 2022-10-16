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

package dev.aherscu.qa.tester.utils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

public class EnumUtilsTest {
    @Test
    public void shouldConvertFromString() {
        assertThat(EnumUtils
            .fromString(E.class, "A"),
            is(E._A));
    }

    @Test(expectedExceptions = EnumUtils.NoSuchMemberException.class)
    public void shouldFailConvertingFromString() {
        assertThat(EnumUtils
            .fromString(E.class, "C"),
            is(E._A));
    }

    @Test
    public void shouldPrettify() {
        assertThat(EnumUtils
            .prettify(E._A),
            is(" A"));
    }

    @Test
    public void shouldConvertToString() {
        assertThat(EnumUtils
            .toString(E._B),
            is("B"));
    }

    enum E {
        _A, _B
    }
}
