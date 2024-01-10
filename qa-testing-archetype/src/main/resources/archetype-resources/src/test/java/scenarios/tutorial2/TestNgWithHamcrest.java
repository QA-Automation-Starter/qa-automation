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

package ${package}.scenarios.tutorial2;

import static com.google.common.collect.Ordering.*;
import static com.jayway.jsonpath.JsonPath.*;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.*;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.both;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.closeTo;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.containsString;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.hasItem;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.is;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import java.util.stream.*;

import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

public class TestNgWithHamcrest {

    @Test
    public void _1_shouldSucceed() {
        assert true; // false; // no description generated for this failure
    }

    @Test
    public void _2_shouldAssertOnBoolean() {
        assertThat(true, is(true));
    }

    @Test
    public void _3_shouldAssertOnStringContents() {
        assertThat("should verify string contents",
            both(containsString("verify"))
                .and(containsString("string")));
    }

    @Test
    public void _4_shouldAssertOnListContents() {
        assertThat(asList("should", "verify", "list", "contents"),
            both(hasItem(startsWith("verify")))
                .and(hasItem(equalTo("list"))));
    }

    @Test
    public void _5_shouldAssertOnListOrder() {
        assertThat(asList(1, 2, 3, 4, 5), is(ordered(natural())));
    }

    @Test
    public void _6_shouldAssertOnEquality() {
        @AllArgsConstructor // lombok generates a constructor
        @ToString // lombok generates a nice toString method
        @EqualsAndHashCode // comment to make it fail the test
        class Foo {
            final int    id;
            final String contents;
        }

        assertThat(new Foo(1, "hello"),
            is(equalTo(new Foo(1, "hello"))));
    }

    @Test
    public void _7_shouldAssertOnStreamContents() {
        @AllArgsConstructor
        @ToString
        class Foo {
            final int    id;
            final String contents;
        }

        assertThat(Stream.of(
            new Foo(1, "should"),
            new Foo(2, "verify"),
            new Foo(3, "stream"),
            new Foo(4, "contents")),
            adaptedStream(f -> f.contents, // we need to "adapt" Foo into a
                                           // String
                hasItemsMatching(equalTo("stream"))));
    }

    @Test
    public void _8_shouldAssertOnStreamOfJsons() {
        assertThat(Stream.of(
            "{'id':1, 'contents':'should'}",
            "{'id':2, 'contents':'verify'}",
            "{'id':3, 'contents':'json'}",
            "{'id':4, 'contents':'contents'}"),
            // we need to adapt JSON structure into contents value
            adaptedStream(json -> parse(json).read("$.contents"),
                hasItemsMatching(equalTo("json"))));
    }

    @Test(dataProvider = "sinusFunctionTable")
    public void _9_shouldCalculateSinusWithTolerance(
        final double angle,
        final double sinus) {
        assertThat(sin(angle), is(closeTo(sinus, 1e-10)));
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @DataProvider
    private Object[][] sinusFunctionTable() {
        return new Object[][] {
            // @formatter:off
            // there are toooooooo much possibilities...
            { PI,                0 },
            { 2 * PI,            0 },
            // { pow(2, 1000) * PI, 0 }, // this will fail due to much precision loss
            // @formatter:on
        };
    }
}
