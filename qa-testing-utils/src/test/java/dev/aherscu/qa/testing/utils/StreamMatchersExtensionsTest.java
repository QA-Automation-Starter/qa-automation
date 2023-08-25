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

import static dev.aherscu.qa.testing.utils.MatchersExtensions.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.startsWith;
import static org.hamcrest.MatcherAssert.*;

import java.lang.SuppressWarnings;
import java.util.*;
import java.util.stream.*;

import org.hamcrest.*;
import org.testng.annotations.*;

import com.google.common.collect.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
    justification = "no real impact on memory/disk consumption")
@SuppressWarnings({ "javadoc", "static-method", "boxing" })
@Slf4j
public class StreamMatchersExtensionsTest {
    @Test
    public void shouldAllowMatcherReuse() {
        final Matcher<Stream<Integer>> m = orderedStream(Ordering.natural());
        try {
            assertThat(Stream
                .of(8, 4, 5, 6, 7, 9)
                .peek(i -> log.debug("scanning {}", i)),
                m);
        } catch (final AssertionError ae) {
            log.debug(">>> {}", ae.getMessage());
            assertThat(Stream
                .of(1, 4, 5, 6, 7, 9)
                .peek(i -> log.debug("scanning {}", i)),
                m);
        }
    }

    @Test
    public void shouldAssertEmpty() {
        assertThat(Stream.<Integer> empty(),
            hasItemsMatching());
    }

    @Test
    public void shouldAssertEmptyStream() {
        assertThat(Stream.empty(), emptyStream());
    }

    @Test
    public void shouldAssertOnAdaptedStream() {
        @AllArgsConstructor
        @ToString
        class Foo {
            final int    id;
            final String data;
        }

        assertThat(Stream
            .of(new Foo(4, "blah"), new Foo(7, "trah")),
            StreamMatchersExtensions
                .adaptedStream(foo -> foo.id,
                    anyMatch(is(7))));
    }

    @Test
    public void shouldAssertOnCollectedStream() {
        assertThat(Stream.of(1, 2, 10, 3), adapt(HashSet::new,
            both(iterable(everyItem(not(0))))
                .and(iterableSuper(hasItem(1)))));
    }

    @Test
    public void shouldAssertOnFirstMatch() {
        assertThat(Stream
            .of(1, 4, 2)
            .peek(i -> log.debug("scanning {}", i)),
            anyMatch(is(4)));
    }

    @Test
    public void shouldAssertOnFirstMatches() {
        assertThat(Stream
            .of(1, 4, 9, 2, 7, 0)
            .peek(i -> log.debug("scanning {}", i)),
            hasItemsMatching(
                is(4), is(2), greaterThan(6)));
    }

    @Test
    public void shouldAssertOnFirstValues() {
        assertThat(Stream
            .of(1, 4, 9, 2, 7, 0)
            .peek(i -> log.debug("scanning {}", i)),
            hasSpecificItems(4, 2));
    }

    @Test
    public void shouldAssertOnOrder() {
        assertThat(Stream
            .of(1, 4, 5, 6, 7, 9)
            .peek(i -> log.debug("scanning {}", i)),
            orderedStream(Ordering.natural()));
    }

    @Test
    public void shouldAssertOnVeryFirstMatches() {
        assertThat(Stream
            .of(1, 4, 2)
            .peek(i -> log.debug("scanning {}", i)),
            startsWith(1, 4));
    }

    @Test
    public void shouldAssertWithAnyOrder() {
        assertThat(Stream
            .of(1, 4, 9, 2, 7, 0),
            hasItemsMatchingInAnyOrder(
                is(2),
                is(9)));
    }

    @Test
    public void shouldCount() {
        assertThat(Stream.of(1, 2, 3), counts(3));
    }

    @Test
    public void shouldCountAtLeast() {
        assertThat(Stream.of(1, 2, 3, 4), counts(greaterThanOrEqualTo(3L)));
    }

    @Test
    public void shouldLimitAssertionOnFirstMatches() {
        assertThat(Stream
            .of(1, 4, 9, 2, 7, 0)
            .peek(i -> log.debug("scanning {}", i)),
            scanningAtMost(5,
                hasItemsMatching(
                    is(4), is(2), greaterThan(6))));
    }

    @Test
    public void shouldMatchAll() {
        assertThat(Stream.of("bardjffoodjio", "foobar", "jfbarioffoo"),
            allMatch(both(containsString("foo"))
                .and(containsString("bar"))));
    }
}
