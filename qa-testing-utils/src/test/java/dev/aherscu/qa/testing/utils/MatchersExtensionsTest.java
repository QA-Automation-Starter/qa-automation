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

import static com.google.common.collect.Ordering.*;
import static com.jayway.jsonpath.JsonPath.*;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.*;
import static java.lang.Boolean.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import org.apache.commons.jxpath.*;
import org.hamcrest.*;
import org.testng.annotations.*;

import com.google.common.collect.*;
import com.jayway.jsonpath.*;

import lombok.*;

@SuppressWarnings({ "javadoc", "static-method", "boxing", "MagicNumber" })
@Test
public class MatchersExtensionsTest {
    public void shouldAssertNotEmptyIterable() {
        final Iterable<Integer> anIterable = asList(1, 2, 3);
        assertThat(anIterable,
            adaptedIterableToCollectionMatcher(not(collection(empty()))));
    }

    public void shouldAssertOnBooleanString() {
        assertThat("true", adaptedStringToBooleanMatcher(is(TRUE)));
    }

    // fails
    @Test(enabled = false)
    public void shouldAssertOnDoubleInteger() {
        assertThat(5.0,
            adaptedTypeOfNumericMatcher(equalTo(5), Integer.class));
    }

    public void shouldAssertOnDoubleString() {
        assertThat("5.0",
            adaptedStringToNumericMatcher(equalTo(5.0)));
    }

    // fails
    @Test(enabled = false)
    public void shouldAssertOnIntegerDouble() {
        assertThat(5,
            adaptedTypeOfNumericMatcher(equalTo(5.0), Double.class));
    }

    public void shouldAssertOnIntegerString() {
        assertThat("5",
            adaptedStringToNumericMatcher(equalTo(5), Integer.class));
    }

    public void shouldAssertOnListOfStringsByJsonPath() {
        assertThat(
            asList(
                "{'a':'a'}",
                "{'a':'b'}",
                "{'a':'c'}"),
            adapted(s -> parse(s).read("$.a"), hasItems("b")));
    }

    public void shouldAssertOnListWithCombinedMatchers() {
        assertThat(asList(1, 2, 3, -1),
            both(iterable(everyItem(not(0))))
                .and(iterableSuper(hasItem(3))));
    }

    public void shouldBeAlphabeticallyOrdered() {
        assertThat(asList(
            "Ba", "Cain", "Goldstein", "Jimenez", "Thompson"),
            is(ordered(natural())));
    }

    public void shouldBeFilterNumbersWithNulls() {
        assertThat(asList(99, 99, 10, 3, null, null),
            is(ordered(natural().reverse().nullsLast())));
    }

    public void shouldBeOrdered() {
        assertThat(asList(1, 2, 3),
            is(ordered(natural())));
    }

    public void shouldBeOrderedByData() {
        assertThat(
            asList(
                new SomeType(1, "data7"),
                new SomeType(2, "data2")),
            adapted(SomeType::getData,
                is(ordered(Ordering.natural().reverse()))));
    }

    public void shouldBeOrderedById() {
        assertThat(
            asList(
                new SomeType(1, "data7"),
                new SomeType(2, "data2")),
            adapted(SomeType::getId, is(ordered(Ordering.natural()))));
    }

    /*
     * public void shouldAssertOnListOfStringsByRegex() {
     * MatcherAssert.assertThat( asList( "oiojf[[test]]-1fqwe",
     * "fds[[best]]-4fas", "fw[[test]]-4vxc", "sf[[test]]-2fae"),
     * adaptedByRegex("\\[\\[test\\]\\]\\-\\d+",
     * adaptedCollectionToIterableMatcher( hasDistinctElements()))); }
     */

    public void shouldBeOrderedBySpecificProperties() {
        assertThat(
            asList(
                new SomeType(1, "data7"),
                new SomeType(2, "data2")),
            both(adaptedByJXPath("id", is(ordered(Ordering.natural()))))
                .and(adaptedByJXPath("data",
                    is(ordered(Ordering.natural().reverse())))));
    }

    public void shouldBeOrderedTextually() {
        assertThat(
            asList("99+", "99", "12" /* , "2" */), // "2" is greater than "12"
            is(ordered(natural().reverse())));
    }

    @Test
    public void shouldCurryAdapted() {
        val jsonAdapter = MatchersExtensions
            .<Function<String, String>, Matcher<Iterable<String>>, Matcher<Iterable<String>>> curriedAdapter(
                MatchersExtensions::adapted)
            .curry(json -> JsonPath.parse(json).read("$.a"));

        assertThat(
            Arrays.asList(
                "{'a':'a'}",
                "{'a':'b'}",
                "{'a':'c'}"),
            jsonAdapter.apply(hasItems("b")));
    }

    @Test
    public void shouldCurryAdaptedObject() {
        val someTypeAdapter = MatchersExtensions
            .<Function<SomeType, String>, Matcher<String>, Matcher<SomeType>> curriedAdapter(
                MatchersExtensions::adaptedObject)
            .curry(SomeType::getData);

        assertThat(
            new SomeType(123, "blah"),
            someTypeAdapter.apply(equalTo("blah")));
    }

    public void shouldGetValueByJXPath() {
        assertThat(JXPathContext
            .newContext(new SomeType(123, "data"))
            .getValue("id"),
            equalTo(123));
    }

    public void shouldIgnoreMissingProperties() {

        assertThat(Stream
            .of(
                new SomeListType(123, singletonList("data")),
                new SomeListType(123, emptyList()))
            .map(o1 -> new SomeType(o1.id,
                defaultIfBlank(o1.data
                    .stream()
                    .map(e -> e.replace('1', '2'))
                    .collect(Collectors.joining()),
                    null)))
            .collect(Collectors.toList()),
            adapted(o -> o.data, is(ordered(natural().nullsLast()))));
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    public final static class SomeListType {
        public final int          id;
        public final List<String> data;

    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    public final static class SomeType {
        public final int    id;
        public final String data;
    }

}
