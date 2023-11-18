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
package dev.aherscu.qa.jgiven.commons.scenarios;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import java.lang.SuppressWarnings;
import java.math.*;

import org.testng.annotations.*;
import org.unitils.io.annotation.*;

import com.tngtech.jgiven.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.data.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.*;
import dev.aherscu.qa.testing.utils.config.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Contains sample tests just to ensure that the testing infrastructure works as
 * required.
 *
 * @author aherscu
 *
 */
@SuppressFBWarnings(
    value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
    justification = "framework limitation")
@SuppressWarnings({ "static-method" })
@SelfTest
public final class GenericTest extends
    UnitilsScenarioTest<BaseConfiguration, AnyScenarioType, GenericFixtures<AnyScenarioType, ?>, GenericActions<AnyScenarioType, ?>, GenericVerifications<AnyScenarioType, ?>> {

    @FileContent
    private String sql;

    // ISSUE: sometimes creating a temporary file on Windows 7 fails with
    // "access denied".
    // @TempFile
    // private File tempFile;

    /**
     * Initializes with {@link BaseConfiguration}.
     */
    private GenericTest() {
        super(BaseConfiguration.class);
    }

    /**
     * Expecting a {@link RuntimeException}.
     */
    @Test(expectedExceptions = RuntimeException.class)
    public void expectingSomeFailure() {
        throw new RuntimeException(
            "failing on purpose just to see the test passing");
    }

    @Test
    @SneakyThrows
    public void shouldAccessDb() {
        QUERY_RUNNER.batch(
            "create table TEST_TABLE(INTEGER_COLUMN INTEGER)",
            new Object[][] { {} });
        QUERY_RUNNER.batch(
            "insert into TEST_TABLE values (1)",
            new Object[][] { {} });
        // this works too :)
        QUERY_RUNNER.execute(
            "insert into TEST_TABLE values (2)");
        QUERY_RUNNER
            .query("select INTEGER_COLUMN from TEST_TABLE",
                new ArrayListHandler())
            .forEach(row -> log.debug("row: {}", row));
    }

    /**
     * Should fail just to see that failures are reported.
     */
    @Test(expectedExceptions = AssertionError.class)
    public void shouldFail() {
        given().nothing();
        when().doing_nothing();
        then().should_succeed(is(false));
    }

    /**
     * NOTE: JGiven catches {@link Throwable}s in order to generate its report,
     * hence this cannot be handled via {@link Test#expectedExceptions}.
     * <p>
     * Disabled in order to allow {@code mvn install} pass.
     * </p>
     */
    @Test(enabled = false)
    public void shouldFailDueToRuntimeException() {
        given().nothing();
        when().failing_on_purpose_with(
            new RuntimeException("just to see it failing"));
        section("should be skipped");
        then().should_succeed(is(false));
    }

    /**
     * Should fail reading something from a database.
     *
     * <p>
     * FIXME insert required data before running the test
     * </p>
     */
    @Test(enabled = false) // requires a mock database
    public void shouldFailFindingSomethingInDatabase() {
        // noinspection MagicNumber
        then().querying_$_evaluates_as(
            sql,
            new Object[][] {
                { BigDecimal.valueOf(95144630) },
                { BigDecimal.valueOf(80366961) }
            });
    }

    /**
     * Defined to complete within 1000 ms but sleeps for 2000 ms.
     */
    @SneakyThrows(Exception.class)
    @Test(enabled = false, timeOut = 1000) // fails with Error!!!
    public void shouldFailOnTimeout() {
        // noinspection MagicNumber
        when().$("sleeping too much", //$NON-NLS-1$
            (StepFunction<GenericActions<AnyScenarioType, ?>>) stage -> ThreadUtils
                .sleep(2000));
    }

    /**
     * Should fail with an unexpected runtime exception just to verify how these
     * are handled by the testing infrastructure and how these appear in the
     * report.
     */
    @SneakyThrows(Exception.class)
    @Test(expectedExceptions = TestRuntimeException.class)
    public void shouldFailWithUnexpectedRuntimeException() {
        then().$("should throw a runtime exception", //$NON-NLS-1$
            (StepFunction<GenericVerifications<AnyScenarioType, ?>>) stage -> {
                throw new TestRuntimeException("!!!");
            });
    }

    // /**
    // * Should have a temporary file.
    // *
    // * <p>
    // * ISSUE: fails to create temporary file on Jenkins.
    // org.unitils.core.UnitilsException: Error creating temp file for field
    // tempFile
    // Caused by: org.unitils.core.UnitilsException: Unable to delete temp
    // file/dir
    // C:\Users\aherscu\AppData\Local\Temp
    // \dev.aherscu.qa.jgiven.commons.scenarios.GenericTest-shouldTestInParallel.tmp
    // */
    // @SneakyThrows(Exception.class)
    // @Test
    // public void shouldHaveTempFile() {
    // then().$("should find a temporary file", //$NON-NLS-1$
    // (StepFunction<GenericVerifications<AnyScenarioType, ?>>) stage ->
    // assertThat(
    // tempFile.exists()).isTrue());
    // }

    /**
     * Should retry upon any {@link Throwable}, eventually failing.
     */
    @Test(expectedExceptions = Throwable.class)
    public void shouldRetrySomethingBeforeFailing() {
        given().nothing();
        when().retrying(step -> step.failing_on_purpose_with(
            new Throwable("just to see it retry")));
        section("should be skipped");
        then().should_succeed(is(false));
    }

    /**
     * Should succeed just to see the successes are reported.
     */
    @Test
    public void shouldSucceed() {
        given().nothing();
        when().doing_nothing();
        then().should_succeed(is(true));
    }

    /**
     * Should succeed reading something from a database.
     *
     * <p>
     * FIXME insert required data before running the test
     * </p>
     */
    @Test(enabled = false)
    public void shouldSucceedFindingSomethingInDatabase() {
        // noinspection MagicNumber
        then().querying_$_evaluates_as(
            sql,
            new Object[][] {
                { BigDecimal.valueOf(95144630) },
                { BigDecimal.valueOf(80366964) }
            });
    }

    /**
     * Left should be equal to right.
     *
     * @param left
     *            the left
     * @param right
     *            the right
     */
    @Test(dataProvider = "dummyData",
        dataProviderClass = SelfTestsData.class)
    @SneakyThrows(Exception.class)
    public void shouldTestInParallel(final String left, final String right) {
        then().$("left should be equal to right", //$NON-NLS-1$
            (StepFunction<GenericVerifications<AnyScenarioType, ?>>) stage -> assertThat(
                left).isEqualTo(right));
    }
}
