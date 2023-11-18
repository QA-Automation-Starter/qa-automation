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

import java.lang.SuppressWarnings;
import java.math.*;

import org.apache.commons.dbutils.handlers.*;
import org.testng.annotations.*;
import org.unitils.io.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.config.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

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
// ISSUE does not work because in-mem db not initialized with schema/tables
// looks like @BeforeClass above is not enough "before..."
// changed to @BeforeTest... same effect
// workaround:
// instead of jdbc:derby:memory:testing;create=true
// use a file based one -- and put it in source-control
//@DataSet
@Slf4j
public final class DatabaseTest extends
    UnitilsScenarioTest<BaseConfiguration, AnyScenarioType, GenericFixtures<AnyScenarioType, ?>, GenericActions<AnyScenarioType, ?>, GenericVerifications<AnyScenarioType, ?>> {

    // ISSUE not initialized !!!
    @FileContent("GenericTest.sql")
    private String initializationSql;

    /**
     * Initializes with {@link BaseConfiguration}.
     */
    private DatabaseTest() {
        super(BaseConfiguration.class);
    }

    @Test
    @SneakyThrows
    public void shouldAccessDb() {
        QUERY_RUNNER
            .query("select INTEGER_COLUMN from TEST_TABLE",
                new ArrayListHandler())
            .forEach(row -> log.debug("row: {}", row));
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
            "select INTEGER_COLUMN from TEST_TABLE",
            new Object[][] {
                { BigDecimal.valueOf(95144630) },
                { BigDecimal.valueOf(80366961) }
            });
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
            "select INTEGER_COLUMN from TEST_TABLE",
            new Object[][] {
                { 1 },
                { 2 }
            });
    }

    @BeforeTest
    @SneakyThrows
    protected void beforeTestInitDatabase() {
        log.debug("initializing database with: {}", initializationSql);
        // ISSUE all these are not seen by dbunit while performing @DataSet load
        //QUERY_RUNNER.execute("create table TEST_TABLE(INTEGER_COLUMN INTEGER)");
        QUERY_RUNNER.batch(
            "create table TEST_TABLE(INTEGER_COLUMN INTEGER)",
            new Object[][] { {} });
        QUERY_RUNNER.batch(
            "insert into TEST_TABLE values (1)",
            new Object[][] { {} });
        // this works too :)
        QUERY_RUNNER.execute(
            "insert into TEST_TABLE values (2)");
    }
}
