package dev.aherscu.qa.testing.utils;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class OptionalMatchers {

    /**
     * Matches a not present Optional.
     */
    public static <T> Matcher<Optional<T>> notPresent() {
        return new TypeSafeMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> item) {
                return !item.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("An Optional with no value");
            }
        };
    }

    /**
     * Matches a not present Optional.
     *
     * @deprecated name clashes with {@link org.hamcrest.Matchers#empty()}, use
     *             {@link #notPresent()} instead
     */
    @Deprecated
    public static <T> Matcher<Optional<T>> empty() {
        return notPresent();
    }

    /**
     * Matches a present Optional with the given content
     *
     * @param content
     *            Expected contents of the Optional
     * @param <T>
     *            The type of the Optional's content
     */
    public static <T> Matcher<Optional<T>> present(T content) {
        return new TypeSafeMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> item) {
                return item.map(content::equals).orElse(false);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Optional.of(content).toString());
            }
        };
    }

    /**
     * Matches a present Optional with the given content
     *
     * @param content
     *            Expected contents of the Optional
     * @param <T>
     *            The type of the Optional's content
     *
     * @deprecated name clashes with
     *             {@link org.hamcrest.Matchers#contains(Object...)}, use
     *             {@link #present(Object)} instead
     */
    @Deprecated
    public static <T> Matcher<Optional<T>> contains(T content) {
        return present(content);
    }

    /**
     * Matches a present Optional with content matching the given matcher
     *
     * @param matcher
     *            To match against the Optional's content
     * @param <T>
     *            The type of the Optional's content
     * @param <S>
     *            The type matched by the matcher, a subtype of T
     */
    public static <T, S extends T> Matcher<Optional<S>> present(
        Matcher<T> matcher) {
        return new TypeSafeMatcher<Optional<S>>() {
            @Override
            protected boolean matchesSafely(Optional<S> item) {
                return item.map(matcher::matches).orElse(false);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                    "Optional with an item that matches " + matcher);
            }
        };
    }

    /**
     * Matches a present Optional with content matching the given matcher
     *
     * @param matcher
     *            To match against the Optional's content
     * @param <T>
     *            The type of the Optional's content
     * @param <S>
     *            The type matched by the matcher, a subtype of T
     * @deprecated name clashes with
     *             {@link org.hamcrest.Matchers#contains(Matcher)}, use
     *             {@link #present(Matcher)} instead
     */
    @Deprecated
    public static <T, S extends T> Matcher<Optional<S>> contains(
        Matcher<T> matcher) {
        return present(matcher);
    }

    /**
     * Matches an not present OptionalInt.
     */
    public static Matcher<OptionalInt> notPresentInt() {
        return new TypeSafeMatcher<OptionalInt>() {
            @Override
            protected boolean matchesSafely(OptionalInt item) {
                return !item.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("An OptionalInt with no value");
            }
        };
    }

    /**
     * Matches a not present OptionalInt.
     *
     * @deprecated Matcher is replaced with {@link #notPresentInt()} to align
     *             with naming of {@link #notPresent()}.
     */
    @Deprecated
    public static Matcher<OptionalInt> emptyInt() {
        return notPresentInt();
    }

    /**
     * Matches a present OptionalInt with the given content
     *
     * @param content
     *            Expected contents of the Optional
     */
    public static Matcher<OptionalInt> presentInt(int content) {
        return new TypeSafeMatcher<OptionalInt>() {
            @Override
            protected boolean matchesSafely(OptionalInt item) {
                return item.isPresent() && item.getAsInt() == content;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Optional.of(content).toString());
            }
        };
    }

    /**
     * Matches a present OptionalInt with the given content
     *
     * @param content
     *            Expected contents of the Optional
     *
     * @deprecated Matcher is replaced with {@link #presentInt(int)} to align
     *             with naming of {@link #present(Object)}.
     */
    @Deprecated
    public static Matcher<OptionalInt> containsInt(int content) {
        return presentInt(content);
    }

    /**
     * Matches a present OptionalInt with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalInt's content
     */
    public static Matcher<OptionalInt> presentInt(Matcher<Integer> matcher) {
        return new TypeSafeMatcher<OptionalInt>() {
            @Override
            protected boolean matchesSafely(OptionalInt item) {
                return item.isPresent() && matcher.matches(item.getAsInt());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                    "OptionalInt with an item that matches " + matcher);
            }
        };
    }

    /**
     * Matches a present OptionalInt with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalInt's content
     *
     * @deprecated Matcher is replaced with {@link #presentInt(Matcher)} to
     *             align with naming of {@link #present(Matcher)}.
     */
    @Deprecated
    public static Matcher<OptionalInt> containsInt(Matcher<Integer> matcher) {
        return presentInt(matcher);
    }

    /**
     * Matches an OptionalLong with no value.
     */
    public static Matcher<OptionalLong> notPresentLong() {
        return new TypeSafeMatcher<OptionalLong>() {
            @Override
            protected boolean matchesSafely(OptionalLong item) {
                return !item.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("An OptionalLong with no value");
            }
        };
    }

    /**
     * Matches a not present OptionalLong.
     *
     * @deprecated Matcher is replaced with {@link #notPresentLong()} to align
     *             with naming of {@link #notPresent()}.
     */
    @Deprecated
    public static Matcher<OptionalLong> emptyLong() {
        return notPresentLong();
    }

    /**
     * Matches a present OptionalLong with the given content
     *
     * @param content
     *            Expected contents of the Optional
     */
    public static Matcher<OptionalLong> presentLong(long content) {
        return new TypeSafeMatcher<OptionalLong>() {
            @Override
            protected boolean matchesSafely(OptionalLong item) {
                return item.isPresent() && item.getAsLong() == content;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Optional.of(content).toString());
            }
        };
    }

    /**
     * Matches a present OptionalLong with the given content
     *
     * @param content
     *            Expected contents of the Optional
     *
     * @deprecated Matcher is replaced with {@link #presentLong(long)} to align
     *             with naming of {@link #present(Object)}.
     */
    @Deprecated
    public static Matcher<OptionalLong> containsLong(long content) {
        return presentLong(content);
    }

    /**
     * Matches a present OptionalLong with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalLong's content
     */
    public static Matcher<OptionalLong> presentLong(Matcher<Long> matcher) {
        return new TypeSafeMatcher<OptionalLong>() {
            @Override
            protected boolean matchesSafely(OptionalLong item) {
                return item.isPresent() && matcher.matches(item.getAsLong());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                    "OptionalLong with an item that matches " + matcher);
            }
        };
    }

    /**
     * Matches a present OptionalLong with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalLong's content
     *
     * @deprecated Matcher is replaced with {@link #presentLong(Matcher)} to
     *             align with naming of {@link #present(Matcher)}.
     */
    @Deprecated
    public static Matcher<OptionalLong> containsLong(Matcher<Long> matcher) {
        return presentLong(matcher);
    }

    /**
     * Matches a not present OptionalDouble.
     */
    public static Matcher<OptionalDouble> notPresentDouble() {
        return new TypeSafeMatcher<OptionalDouble>() {
            @Override
            protected boolean matchesSafely(OptionalDouble item) {
                return !item.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("An OptionalDouble with no value");
            }
        };
    }

    /**
     * Matches a not present OptionalDouble.
     *
     * @deprecated Matcher is replaced with {@link #notPresentDouble()} to align
     *             with naming of {@link #notPresent()}.
     */
    @Deprecated
    public static Matcher<OptionalDouble> emptyDouble() {
        return notPresentDouble();
    }

    /**
     * Matches a present OptionalDouble with the given content
     *
     * @param content
     *            Expected contents of the Optional
     */
    public static Matcher<OptionalDouble> presentDouble(double content) {
        return new TypeSafeMatcher<OptionalDouble>() {
            @Override
            protected boolean matchesSafely(OptionalDouble item) {
                return item.isPresent() && item.getAsDouble() == content;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Optional.of(content).toString());
            }
        };
    }

    /**
     * Matches a present OptionalDouble with the given content
     *
     * @param content
     *            Expected contents of the Optional
     *
     * @deprecated Matcher is replaced with {@link #presentDouble(double)} to
     *             align with naming of {@link #present(Object)}.
     */
    @Deprecated
    public static Matcher<OptionalDouble> containsDouble(double content) {
        return presentDouble(content);
    }

    /**
     * Matches a present OptionalDouble with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalDouble's content
     */
    public static Matcher<OptionalDouble> presentDouble(
        Matcher<Double> matcher) {
        return new TypeSafeMatcher<OptionalDouble>() {
            @Override
            protected boolean matchesSafely(OptionalDouble item) {
                return item.isPresent() && matcher.matches(item.getAsDouble());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                    "OptionalDouble with an item that matches " + matcher);
            }
        };
    }

    /**
     * Matches a present OptionalDouble with content matching the given matcher
     *
     * @param matcher
     *            To match against the OptionalDouble's content
     *
     * @deprecated Matcher is replaced with {@link #presentDouble(Matcher)} to
     *             align with naming of {@link #present(Matcher)}.
     */
    @Deprecated
    public static Matcher<OptionalDouble> containsDouble(
        Matcher<Double> matcher) {
        return presentDouble(matcher);
    }
}
