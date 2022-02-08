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

import static java.util.stream.Collectors.*;

import java.lang.SuppressWarnings;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import javax.annotation.concurrent.*;

import org.hamcrest.*;

import com.google.common.collect.*;

import edu.umd.cs.findbugs.annotations.*;
import uk.co.probablyfine.matchers.*;

// NOTE cannot use lombok.val/var here on JDK 16
// ISSUE https://github.com/projectlombok/lombok/issues/2839

/**
 * Drop-in replacement for Java-8-matchers' {@link StreamMatchers}.
 *
 * <p>
 * Can be statically imported along Hamcrest's {@link Matchers}.
 * </p>
 *
 * @author aherscu
 */
@ThreadSafe
@SuppressWarnings({ "boxing", })
public class StreamMatchersExtensions extends StreamMatchers {
    /**
     * Adapts a Stream to be matched with an Iterable matcher. Use with
     * combinable matchers.
     *
     * @param supplier
     *            the specific collection; e.g. <tt>ArrayList::new</tt>
     * @param matcher
     *            against which to compare objects from the Stream
     * @param <T>
     *            type of objects produced by the Stream
     * @return adapted Iterable matcher
     */
    public static <T> Matcher<Stream<T>> adapt(
        final Supplier<Collection<T>> supplier,
        final Matcher<Iterable<T>> matcher) {
        return new TypeSafeMatcher<Stream<T>>() {
            private Iterable<T> actualCollected;

            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                actualCollected = actual.collect(toCollection(supplier));
                return matcher.matches(actualCollected);
            }

            @Override
            public void describeMismatchSafely(
                final Stream<T> actual,
                final Description description) {
                description.appendText("was a list of "
                    + actualCollected + " items");
            }
        };

    }

    /**
     * Adapts a matcher of stream of objects by specified converter function.
     *
     * @param converter
     *            the converter function
     * @param matcher
     *            the matcher
     * @param <T>
     *            type to be adapted
     * @param <R>
     *            type after adaption
     * @return adapted matcher
     */
    public static <T, R> Matcher<Stream<T>> adaptedStream(
        final java.util.function.Function<T, R> converter,
        // TODO for readability purposes, pass the matcher via separate method
        // see CombinableMatcher as an example, perhaps as a base class
        // For example, adaptStreamBy(x->x.something).andThen(matcher)
        final Matcher<Stream<R>> matcher) {
        return new StreamMatcher<T>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                accumulator = new LinkedList<>();
                return matcher.matches(actual
                    .peek(accumulator::add)
                    .map(converter));
            }
        };
    }

    /**
     * @param message
     *            optional, something to be added to description
     * @param <T>
     *            dummy matcher that matches anything
     * @return something that matches any stream
     */
    public static <T> Matcher<Stream<T>> anyStream(final String... message) {
        return new TypeSafeMatcher<Stream<T>>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText(Arrays.toString(message));
            }

            @Override
            protected boolean matchesSafely(final Stream<T> dontcare) {
                return true;
            }
        };
    }

    public static <T> Matcher<Stream<T>> counts(
        final long expectedNumberOfItems) {
        return counts(Matchers.is(expectedNumberOfItems));
    }

    /**
     * @param expectedNumberOfItems
     *            the expected number of items
     * @param <T>type
     *            of stream item
     * @return the counting matcher
     */
    public static <T> Matcher<Stream<T>> counts(
        final Matcher<Long> expectedNumberOfItems) {
        return new TypeSafeMatcher<Stream<T>>() {
            private long actualCount;

            @Override
            public void describeTo(final Description description) {
                description.appendText("contains "
                    + expectedNumberOfItems + " items");
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                actualCount = actual.count();
                return expectedNumberOfItems.matches(actualCount);
            }

            @Override
            public void describeMismatchSafely(
                final Stream<T> actual,
                final Description description) {
                // NOTE cannot reuse the stream -- hence storing the count above
                description.appendText("was a stream producing "
                    + actualCount + " items");
            }
        };
    }

    /**
     * Fixes method signature from {@link #empty()}.
     *
     * @param <T>
     *            type of objects
     * @return an empty stream matcher
     */
    public static <T> Matcher<Stream<T>> emptyStream() {
        return new TypeSafeMatcher<Stream<T>>() {

            private Iterator<T> actualIterator;

            @Override
            public void describeTo(Description description) {
                description.appendText("an empty Stream");
            }

            @Override
            protected boolean matchesSafely(Stream<T> actual) {
                actualIterator = actual.iterator();
                return !actualIterator.hasNext();
            }

            @SuppressFBWarnings(
                value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
                justification = "this method is always called after matchesSafely")
            @Override
            protected void describeMismatchSafely(Stream<T> item,
                Description description) {
                description.appendText("was non empty Stream starting with ")
                    .appendValue(actualIterator.next());
            }
        };
    }

    /**
     * A matcher for Stream of objects, must produce objects that match the
     * given Matchers in given order. Gaps between matched objects are
     * acceptable.
     *
     * @param matchers
     *            against which to compare objects from the Stream
     * @param <T>
     *            type of objects produced by the Stream
     * @return matcher for stream
     */
    public static <T> Matcher<Stream<T>> hasItemsMatching(
        final List<Matcher<T>> matchers) {
        return new StreamMatcher<T>() {
            @Override
            public void describeTo(final Description description) {
                description
                    .appendText("to match a sequence of following: ")
                    .appendValueList("[", ",", "]", matchers);
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                if (matchers.isEmpty()
                    && !actual.findAny().isPresent()) {
                    return true;
                }

                accumulator = new LinkedList<>();
                final Iterator<T> actualIterator = actual.iterator();
                int index = 0;
                while (actualIterator.hasNext()) {
                    final T object = actualIterator.next();
                    accumulator.add(object);

                    if (matchers.get(index).matches(object)) {
                        index += 1;
                        if (index >= matchers.size())
                            return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * A matcher for Stream of objects, must produce objects that match the
     * given Matchers in given order. Gaps between matched objects are
     * acceptable.
     *
     * @param values
     *            against which to compare objects from the Stream
     * @param <T>
     *            type of objects produced by the Stream
     * @return matcher for stream
     */
    @SafeVarargs
    public static <T> Matcher<Stream<T>> hasItemsMatching(
        final Matcher<T>... values) {
        return hasItemsMatching(ImmutableList.copyOf(values));
    }

    /**
     * A matcher for Stream of objects, must produce objects that match the
     * given Matchers. Gaps between matched objects are acceptable.
     *
     * @param matchers
     *            against which to compare objects from the Stream
     * @param <T>
     *            type of objects produced by the Stream
     * @return matcher for stream
     */
    public static <T> Matcher<Stream<T>> hasItemsMatchingInAnyOrder(
        final Set<Matcher<T>> matchers) {
        final HashSet<Matcher<T>> toBeMatched = new HashSet<>(matchers);
        return new StreamMatcher<T>() {
            @Override
            public void describeTo(final Description description) {
                description
                    .appendText(
                        "to match a sequence of following in any order: ")
                    .appendValueList("[", ",", "]", matchers);
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                if (matchers.isEmpty()
                    && !actual.findAny().isPresent()) {
                    return true;
                }

                accumulator = new LinkedList<>();
                final Iterator<T> actualIterator = actual.iterator();
                while (actualIterator.hasNext()) {
                    final T object = actualIterator.next();
                    accumulator.add(object);

                    toBeMatched.removeIf(matcher -> matcher.matches(object));

                    if (toBeMatched.isEmpty())
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * A matcher for Stream of objects, must produce objects that match the
     * given Matchers. Gaps between matched objects are acceptable.
     *
     * @param values
     *            against which to compare objects from the Stream
     * @param <T>
     *            type of objects produced by the Stream
     * @return matcher for stream
     */
    @SafeVarargs
    public static <T> Matcher<Stream<T>> hasItemsMatchingInAnyOrder(
        final Matcher<T>... values) {
        return hasItemsMatchingInAnyOrder(ImmutableSet.copyOf(values));
    }

    /**
     * Wraps each of specified values into {@link CoreMatchers#is}.
     *
     * @param values
     *            expected objects
     * @param <T>
     *            type of objects in stream
     * @return matcher for stream
     * @see #hasItemsMatching(List)
     */
    public static <T> Matcher<Stream<T>> hasSpecificItems(
        final List<T> values) {
        return hasItemsMatching(
            values.stream()
                .map(CoreMatchers::is)
                .collect(Collectors.toList()));
    }

    /**
     * Wraps each of specified values into {@link CoreMatchers#is}.
     *
     * @param values
     *            expected objects
     * @param <T>
     *            type of objects in stream
     * @return matcher for stream
     * @see #hasItemsMatching(List)
     */
    @SafeVarargs
    public static <T> Matcher<Stream<T>> hasSpecificItems(final T... values) {
        return hasSpecificItems(Lists.newArrayList(values));
    }

    /**
     * Wraps each of specified values into {@link CoreMatchers#is}.
     *
     * @param values
     *            expected objects
     * @param <T>
     *            type of objects in stream
     * @return matcher for stream
     * @see #hasItemsMatchingInAnyOrder(Set)
     */
    @SafeVarargs
    public static <T> Matcher<Stream<T>> hasSpecificItemsInAnyOrder(
        final T... values) {
        return hasSpecificItemsInAnyOrder(ImmutableSet.copyOf(values));
    }

    /**
     * Wraps each of specified values into {@link CoreMatchers#is}.
     *
     * @param values
     *            expected objects
     * @param <T>
     *            type of objects in stream
     * @return matcher for stream
     * @see #hasItemsMatchingInAnyOrder(Set)
     */
    public static <T> Matcher<Stream<T>> hasSpecificItemsInAnyOrder(
        final Set<T> values) {
        return hasItemsMatchingInAnyOrder(
            values.stream()
                .map(CoreMatchers::is)
                .collect(toSet()));
    }

    /**
     * Matches an ordered {@link Stream} by specified ordering.
     *
     * @param ordering
     *            the expected ordering
     * @return the ordering matcher
     */
    public static <T> Matcher<Stream<T>> orderedStream(
        final Ordering<T> ordering) {
        return new StreamMatcher<T>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText(ordering.toString());
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                accumulator = new LinkedList<>();
                actual.forEach(accumulator::add);
                return ordering.isOrdered(accumulator);
            }
        };
    }

    /**
     * Limits amount of scanned objects produced by a stream.
     *
     * @param limit
     *            how many objects to scan at most
     * @param matcher
     *            delegate matcher
     * @param <T>
     *            type of objects
     * @return a stream limiting matcher
     */
    public static <T> Matcher<Stream<T>> scanningAtMost(
        final int limit,
        // TODO for readability purposes, pass the matcher via separate method
        // see CombinableMatcher as an example, perhaps as a base class
        // For example,
        // scanningAtMost(5).adaptStreamBy(x->x.something).andThen(matcher)
        final Matcher<Stream<T>> matcher) {
        return new StreamMatcher<T>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher)
                    .appendText(" scanning at most ")
                    .appendValue(limit);
            }

            @Override
            protected boolean matchesSafely(final Stream<T> actual) {
                accumulator = new LinkedList<>();
                return matcher.matches(actual
                    .peek(accumulator::add)
                    .limit(limit));
            }
        };
    }

    private static abstract class StreamMatcher<T>
        extends TypeSafeMatcher<Stream<T>> {
        protected Collection<T> accumulator;

        @Override
        public void describeMismatchSafely(
            final Stream<T> actual,
            final Description description) {
            description
                .appendText("was a stream producing ")
                .appendValueList("[", ",", "]", accumulator);
        }
    }
}
