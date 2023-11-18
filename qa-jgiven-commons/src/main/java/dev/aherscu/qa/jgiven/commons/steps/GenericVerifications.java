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
package dev.aherscu.qa.jgiven.commons.steps;

import static com.danhaywood.java.assertjext.Conditions.*;
import static dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest.QUERY_RUNNER;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;
import java.util.function.*;

import javax.annotation.concurrent.*;

import org.apache.commons.dbutils.handlers.*;
import org.apache.commons.io.*;
import org.hamcrest.*;

import com.google.common.collect.*;
import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.assertions.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

/**
 * Generic verifications.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @param <T>
 *            type of scenario
 *
 * @author aherscu
 */
@ThreadSafe
@Slf4j
@SuppressWarnings({ "boxing" })
public class GenericVerifications<T extends AnyScenarioType, SELF extends GenericVerifications<T, SELF>>
    extends StageEx<SELF>
    implements ScenarioType<T> {

    /**
     * Logs the construction of this stage.
     */
    public GenericVerifications() {
        log.trace("then stage {} constructed", this); //$NON-NLS-1$
    }

    @SneakyThrows(SQLException.class)
    private static List<Object[]> resultSetOf(final String sql) {
        log.debug("querying {}", sql); //$NON-NLS-1$
        final List<Object[]> resultSet =
            QUERY_RUNNER.query(sql, new ArrayListHandler());
        log.trace("result set contains {} rows", //$NON-NLS-1$
            resultSet.size());
        return resultSet;
    }

    /**
     * Repeatedly executes specified block on <strong>same thread</strong>,
     * verifying its outcome matches the expected.
     *
     * @param step
     *            the block to execute
     * @param additionalRetryPolicies
     *            additional retry policies
     * @return {@link #self()}
     * @throws AssertionError
     *             if the supplied object does not match
     */
    @SafeVarargs
    public final SELF eventually(
        final Function<SELF, SELF> step,
        final Policy<SELF>... additionalRetryPolicies) {
        return eventually(new StepWithDescription<>(EMPTY, step),
            additionalRetryPolicies);
    }

    /**
     * Executes specified step on <strong>same thread</strong>, and repeats it
     * upon {@link AssertionError}. The interval and duration of these
     * repetitions are configured via {@link #beforeScenarioConfigurePolling()}.
     *
     * @param step
     *            the block to execute
     * @param additionalRetryPolicies
     *            additional retry policies
     * @return {@link #self()}
     * @throws AssertionError
     *             if the supplied object does not match
     *
     * @see #beforeScenarioConfigurePolling()
     */
    @SafeVarargs
    public final SELF eventually(final StepWithDescription<SELF> step,
        final Policy<SELF>... additionalRetryPolicies) {
        try {
            return Failsafe
                .with(Lists.asList(retryPolicy, additionalRetryPolicies))
                .get(() -> step.apply(self()));
        } catch (final Throwable t) {
            log.error("eventually got {}", t.getMessage());
            throw t;
        }
    }

    /**
     * Asserts supplied object matches specified matcher on <strong>same
     * thread</strong>. Upon {@link AssertionError}, asks for updated object and
     * asserts again. Ignores all exceptions. The interval and duration of these
     * assertions are configured via {@link #beforeScenarioConfigurePolling()}.
     *
     * @param objectToBeAsserted
     *            the supplied object to assert upon
     * @param matcher
     *            the matcher
     * @param <V>
     *            the type of object to assert upon
     * @param additionalRetryPolicies
     *            additional retry policies
     * @return {@link #self()}
     * @throws AssertionError
     *             if the supplied object does not match
     *
     * @see #beforeScenarioConfigurePolling()
     */
    @SafeVarargs
    public final <V> SELF eventually_assert_that(
        final java.util.function.Supplier<V> objectToBeAsserted,
        final Matcher<V> matcher,
        final Policy<SELF>... additionalRetryPolicies) {
        return eventually(self -> {
            final V value;
            try {
                value = objectToBeAsserted.get();
            } catch (final Throwable t) {
                log.trace("while evaluating got {}", t.getMessage());
                throw t;
            }
            log.trace("asserting value {} against {}",
                prettified(value),
                matcher);
            assertThat(value, matcher);
            return self;
        }, additionalRetryPolicies);
    }

    /**
     * Repeatedly runs specified SQL statement until the returned result set
     * matches the expected results, or until a predefined timeout.
     *
     * @see #beforeScenarioConfigurePolling()
     *
     * @param sql
     *            the SQL statement to execute
     * @param expectedResults
     *            the expected result set
     *
     * @return {@link #self()}
     */
    public SELF querying_$_evaluates_as(
        @StringFormatter.Annotation(maxWidth = 400) final String sql,
        @ObjectsMatrixFormatter.Annotation(
            args = { "30" }) final Object[][] expectedResults) {
        val resultSet = resultSetOf(sql);
        assertThat(resultSet.toArray(new Object[resultSet.size()][]))
            .isEqualTo(expectedResults);
        return self();
    }

    /**
     * Repeatedly runs specified SQL statement until the returned result set
     * matches the expected results in any order, or until a predefined timeout.
     *
     * @see #beforeScenarioConfigurePolling()
     *
     * @param sql
     *            the SQL statement to execute
     * @param expectedResults
     *            the expected result set
     *
     * @return {@link #self()}
     */
    public SELF querying_$_evaluates_as_$_in_any_order(
        @StringFormatter.Annotation(maxWidth = 400) final String sql,
        @ObjectsMatrixFormatter.Annotation(
            args = { "30" }) final Object[][] expectedResults) {
        assertThat(resultSetOf(sql))
            .containsExactlyInAnyOrder(expectedResults);
        return self();
    }

    /**
     * Asserts that the result set returned by specified SQL statement matches
     * the expected results.
     *
     * @see #beforeScenarioConfigurePolling()
     *
     * @param sql
     *            the SQL statement to execute
     * @param expectedResults
     *            the expected result set
     *
     * @return {@link #self()}
     */
    public SELF querying_$_immediately_evaluates_as(
        @StringFormatter.Annotation(maxWidth = 400) final String sql,
        @ObjectsMatrixFormatter.Annotation(
            args = { "30" }) final Object[][] expectedResults) {
        // FIXME Warning:(240, 14) 'isEqualTo()' between objects of
        // inconvertible types 'Condition<Object[][]>' and 'List<Object[]>'
        assertThat(resultSetOf(sql))
            .isEqualTo(matchedBy(equalTo(expectedResults)));
        return self();
    }

    /**
     * Asserts that the result set returned by specified SQL statement matches
     * the expected results in any order.
     *
     * @see #beforeScenarioConfigurePolling()
     *
     * @param sql
     *            the SQL statement to execute
     * @param expectedResults
     *            the expected result set
     *
     * @return {@link #self()}
     */
    public SELF querying_$_immediately_evaluates_as_$_in_any_order(
        @StringFormatter.Annotation(maxWidth = 400) final String sql,
        @ObjectsMatrixFormatter.Annotation(
            args = { "30" }) final Object[][] expectedResults) {
        assertThat(resultSetOf(sql))
            .is(matchedBy(containsInAnyOrder((Cloneable[]) expectedResults)));
        return self();
    }

    /**
     * Verifies that the expected is true.
     *
     * @param matcher
     *            the matching rule
     * @return {@link #self()}
     */
    public SELF should_succeed(final Matcher<Boolean> matcher) {
        assertThat(true, matcher);
        return self();
    }

    /**
     * Verifies the contents of the specified JSON file.
     *
     * @param jsonFile
     *            the JSON file
     * @param expectedContents
     *            the expected contents
     * @return {@link #self()}
     */
    @SneakyThrows(IOException.class)
    @As("the JSON file $ should contain")
    public SELF the_JSON_file_$_should_contain(
        final File jsonFile,
        @JsonAssertionsFormatter.Annotation final Iterable<? extends JsonAssertion<?>> expectedContents) {
        try (val jsonInputStream = new FileInputStream(jsonFile)) {
            // FIXME: duplicated from RestClientThen#the_response_contains
            val jsonVerifier = JsonAssert.with(jsonInputStream);
            for (val pair : expectedContents) {
                if (null == pair.getValue()) {
                    jsonVerifier.assertNotDefined(pair.getKey());
                } else {
                    jsonVerifier.assertEquals(pair.getKey(), pair.getValue());
                }
            }
        }
        return self();
    }

    /**
     * Verifies the contents of the specified file.
     *
     * <p>
     * <strong>IMPORTANT</strong>: not suitable for large files
     * </p>
     *
     * @param file
     *            the type of IDX file
     * @param contents
     *            the contents
     * @return {@link #self()}
     */
    public SELF the_file_$_should_contain(
        final File file,
        final String contents) {
        assertThat(file).hasContent(contents);
        return self();
    }

    /**
     * Verifies the contents of the specified file.
     *
     * <p>
     * <strong>IMPORTANT</strong>: not suitable for large files
     * </p>
     *
     * @param file
     *            the type of IDX file
     * @param expected
     *            the contents to match
     * @return {@link #self()}
     */
    @SneakyThrows(IOException.class)
    public SELF the_file_$_should_match(
        final File file,
        final Matcher<String> expected) {
        assertTrue(expected.matches(IOUtils
            .toString(file.toURI(), StandardCharsets.UTF_8)));
        return self();
    }

    @Override
    protected void beforeScenarioConfigurePolling() {
        super.beforeScenarioConfigurePolling();
        retryPolicy.handle(AssertionError.class);
    }
}
