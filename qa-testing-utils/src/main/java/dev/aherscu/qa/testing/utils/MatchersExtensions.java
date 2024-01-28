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

import static dev.aherscu.qa.testing.utils.NumberUtils.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.util.Objects.*;
import static java.util.regex.Pattern.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

import java.util.*;
import java.util.stream.*;

import javax.annotation.concurrent.*;

import org.apache.commons.jxpath.*;
import org.apache.commons.lang3.*;
import org.hamcrest.*;

import com.google.common.collect.*;

import jcurry.util.*;
import jcurry.util.function.*;

/**
 * Drop-in replacement for Hamcrest's {@link Matchers}.
 *
 * @author aherscu
 *
 */
@ThreadSafe
@SuppressWarnings({ "boxing", })
public class MatchersExtensions extends Matchers {
    /**
     * Adapts a matcher of collection of objects by specified converter
     * function.
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
    public static <T, R> Matcher<Iterable<T>> adapted(
        final java.util.function.Function<T, R> converter,
        final Matcher<Iterable<R>> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Iterable<T> actual) {
                return matcher.matches(StreamSupport
                    .stream(actual.spliterator(), false)
                    .map(converter)
                    .collect(Collectors.toList()));
            }
        };
    }

    /**
     * Adapts a matcher of collection of objects by specified JXPath expression;
     * see <a href=
     * "https://commons.apache.org/proper/commons-jxpath/users-guide.html">Commons
     * Apache JXPath User Manual</a>
     *
     * @param jxpath
     *            the JXPath expression
     * @param matcher
     *            the matcher
     * @return adapted matcher
     */
    public static <T, R> Matcher<Iterable<T>> adaptedByJXPath(
        final String jxpath,
        final Matcher<Iterable<R>> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Iterable<T> actual) {
                return matcher.matches(StreamSupport
                    .stream(actual.spliterator(), false)
                    .map(object -> JXPathContext
                        .newContext(object)
                        .getValue(jxpath))
                    .collect(Collectors.toList()));
            }
        };
    }

    /**
     * Adapts a matcher of collection of strings by specified regular
     * expression.
     *
     * <p>
     * For example:
     * </p>
     *
     * <pre>
     * MatcherAssert.assertThat(
     *     asList("oiojf[[test]]-1fqwe", "fds[[best]]-4fas",
     *         "fw[[test]]-3vxc", "sf[[test]]-3fae"),
     *     adaptedByRegex("\\[\\[test\\]\\]\\-\\d+",
     *         adaptedCollectionToIterableMatcher(hasDistinctElements())));
     * </pre>
     *
     * <p>
     * should fail because there are two items containing {@code [[test]]-3}
     * </p>
     *
     * @param regex
     *            the regular expression
     * @param matcher
     *            the matcher
     * @return adapted matcher
     */
    public static Matcher<Stream<String>> adaptedByRegex(
        final String regex,
        final Matcher<Stream<String>> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Stream<String> actual) {
                return matcher
                    .matches(StreamSupport.stream(actual.spliterator(), false)
                        .map(s -> substring(s, compile(regex)))
                        .collect(Collectors.toList()));
            }
        };
    }

    /**
     * Adapts a {@link Collection} matcher to an {@link Iterable} matcher.
     *
     * @param matcher
     *            the matcher to adapted
     * @return the adapted matcher
     */
    public static <T> Matcher<Collection<T>> adaptedCollectionToIterableMatcher(
        final Matcher<Iterable<T>> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Collection<T> actual) {
                return matcher.matches(actual);
            }
        };
    }

    /**
     * Adapts a {@link Collection} matcher to an {@link Iterable} matcher.
     *
     * @param matcher
     *            the matcher to adapted
     * @return the adapted matcher
     */
    public static <T> Matcher<Iterable<T>> adaptedIterableToCollectionMatcher(
        final Matcher<Collection<T>> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            protected boolean matchesSafely(final Iterable<T> actual) {
                return matcher.matches(actual);
            }
        };
    }

    /**
     * Adapts a matcher of object by specified converter function.
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
    public static <T, R> Matcher<T> adaptedObject(
        final java.util.function.Function<T, R> converter,
        final Matcher<R> matcher) {
        // NOTE changed from TypeSafeMatcher in order to support anything()
        return new BaseMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(matcher);
            }

            @Override
            // NOTE changed from matchesSafely in order to support anything()
            public boolean matches(final Object actual) {
                return matcher.matches(
                    isNull(actual)
                        ? null
                        : converter.apply((T) actual));
            }
        };
    }

    /**
     * Adapts an expected string to a boolean matcher.
     *
     * @param booleanMatcher
     *            the boolean matcher
     * @return a string matcher
     */
    public static Matcher<String> adaptedStringToBooleanMatcher(
        final Matcher<Boolean> booleanMatcher) {
        // NOTE changed from TypeSafeMatcher in order to support anything()
        return new BaseMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(booleanMatcher);
            }

            @Override
            // NOTE changed from matchesSafely in order to support anything()
            public boolean matches(final Object actual) {
                return booleanMatcher.matches(
                    BooleanUtils.toBoolean((String) actual));
            }
        };
    }

    /**
     * Adapts an expected string to a numeric matcher.
     *
     * @param numericMatcher
     *            the numeric matcher
     * @param typeOfExpectedNumber
     *            optional, the desired type to be adapted; if not supplied
     *            {@link Double} is assumed
     * @return a string matcher
     */
    @SafeVarargs
    public static Matcher<String> adaptedStringToNumericMatcher(
        final Matcher<? extends Number> numericMatcher,
        final Class<? extends Number>... typeOfExpectedNumber) {
        // NOTE changed from TypeSafeMatcher in order to support anything()
        return new BaseMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(numericMatcher);
            }

            @Override
            // NOTE changed from matchesSafely in order to support anything()
            public boolean matches(final Object actual) {
                return isCreatable((String) actual)
                    && numericMatcher.matches(
                        numericValueOf((String) actual, typeOfExpectedNumber));
            }
        };
    }

    /**
     * Adapts an expected number to a numeric matcher. Use when a receiving
     * matcher expects a different numeric type than provided.
     *
     * <p>
     * <strong>Example:</strong>
     *
     * <pre>
     * hasSize(
     *     adaptedTypeOfNumericMatcher(
     *         greaterThanOrEqualTo(
     *             configuration.getNonDuplicatedLogsQuantity() * 0.9)))
     * </pre>
     *
     * <p>
     * In this case, {@link Matchers #hasSize(Matcher)} expects an
     * {@link Integer}, while {@link Matchers#greaterThanOrEqualTo(Comparable)}
     * provides a double because of the {@code 0.9} multiplication.
     * </p>
     *
     * <p>
     * NOT READY YET: fails to convert Integer to Double
     * </p>
     *
     * @param numericMatcher
     *            the numeric matcher
     * @param targetType
     *            the target type
     * @return a numeric matcher
     */
    public static Matcher<Number> adaptedTypeOfNumericMatcher(
        final Matcher<? extends Number> numericMatcher,
        final Class<? extends Number> targetType) {
        // NOTE changed from TypeSafeMatcher in order to support anything()
        return new BaseMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendDescriptionOf(numericMatcher);
            }

            @Override
            // NOTE changed from matchesSafely in order to support anything()
            public boolean matches(final Object actual) {
                return numericMatcher.matches(targetType.cast(actual));
            }
        };
    }

    /**
     * Workaround for https://github.com/hamcrest/JavaHamcrest/issues/289
     *
     * @param matcher
     *            matcher to fix
     * @param <T>
     *            matched type
     * @return fixed matcher
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Matcher<Collection<T>> collection(
        final Matcher<Collection<? extends T>> matcher) {
        return (Matcher) matcher;
    }

    /**
     * Experimental. Can be used to bind an adapter to a fixed converter.
     *
     * @param adapted
     *            the adapter to be bound
     * @param <C>
     *            the type of converter
     * @param <MR>
     *            the type of matcher to adapt into
     * @param <MT>
     *            the type of matcher
     * @return the currying bi-function
     */
    public static <C, MR extends Matcher<?>, MT extends Matcher<?>> CurryingBiFunction<C, MR, MT> curriedAdapter(
        final java.util.function.BiFunction<C, MR, MT> adapted) {
        return Currying.biFunction(adapted);
    }

    /**
     * Workaround for https://github.com/hamcrest/JavaHamcrest/issues/289
     *
     * @param matcher
     *            matcher to fix
     * @param <T>
     *            matched type
     * @return fixed matcher
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Matcher<Iterable<T>> iterable(
        final Matcher<Iterable<? extends T>> matcher) {
        return (Matcher) matcher;
    }

    /**
     * Workaround for https://github.com/hamcrest/JavaHamcrest/issues/289
     *
     * @param matcher
     *            matcher to fix
     * @param <T>
     *            matched type
     * @return fixed matcher
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Matcher<Iterable<T>> iterableSuper(
        final Matcher<Iterable<? super T>> matcher) {
        return (Matcher) matcher;
    }

    /**
     * Matches an ordered {@link Iterable} by specified ordering.
     *
     * @param ordering
     *            the expected ordering
     * @return the ordering matcher
     */
    public static <T> Matcher<Iterable<T>> ordered(final Ordering<T> ordering) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText(ordering.toString());
            }

            @Override
            protected boolean matchesSafely(final Iterable<T> actual) {
                return ordering.isOrdered(actual);
            }
        };
    }
}
