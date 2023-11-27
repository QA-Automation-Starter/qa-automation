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

package dev.aherscu.qa.jgiven.commons.utils.dbunit;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;

import javax.sql.*;

import org.unitils.*;
import org.unitils.database.annotations.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;

import dev.aherscu.qa.jgiven.commons.utils.dbunit.*;
import lombok.*;
import lombok.extern.slf4j.*;

@DataSet(loadStrategy = InsertLoadStrategy.class)
@Slf4j
public class UnitilsJUnitTest extends UnitilsJUnit3 {
    @TestDataSource // ("db1")
    private DataSource dataSource1;

    // ISSUE -- see
    // https://github.com/QA-Automation-Starter/qa-automation/issues/209
    // @TestDataSource("db2")
    // private DataSource dataSource2;

    @SneakyThrows
    public static void assertExistenceOfInitialAndDataSetValues(
        final DataSource dataSource) {
        try (val results =
            new StreamingQueryRunner(dataSource)
                .queryStream("select * from TEST_TABLE")) {
            assertThat(results,
                adaptedStream(row -> row[0],
                    hasSpecificItems(
                        // NOTE: initial values are preserved by the load
                        // strategy
                        "initial value 1",
                        "initial value 2",
                        "dataset value 1",
                        "dataset value 2")));
        }
    }

    public void testUsingDb() {
        assertExistenceOfInitialAndDataSetValues(dataSource1);
    }
}
