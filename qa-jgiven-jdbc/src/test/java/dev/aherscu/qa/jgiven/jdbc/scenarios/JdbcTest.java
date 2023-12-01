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

package dev.aherscu.qa.jgiven.jdbc.scenarios;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.adaptedStream;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.hasSpecificItems;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.jdbc.*;

public class JdbcTest extends AbstractJdbcTest {
    protected JdbcTest() {
        super(TestConfiguration.class);
    }

    // ISSUE does not fail but throws multiple SQL errors about already existent
    // table
    @Test
    public void shouldAccessDb1() {
        given()
            .a_query_runner(configuration().queryRunnerFor("db-1"));

        when()
            .executing("create table TEST_TABLE(NAME varchar(20))")
            .and().executing("insert into TEST_TABLE values ('value 1')")
            .and().querying("select * from TEST_TABLE");

        then()
            .the_result_matches(
                adaptedStream(row -> row[0],
                    hasSpecificItems("value 1")));
    }

    @Test
    public void shouldAccessDb2() {
        given()
            .a_query_runner(configuration().queryRunnerFor("db-2"));

        when()
            .executing("create table TEST_TABLE(NAME varchar(20))")
            .and().executing("insert into TEST_TABLE values ('value 1')")
            .and().querying("select * from TEST_TABLE");

        then()
            .the_result_matches(
                adaptedStream(row -> row[0],
                    hasSpecificItems("value 1")));
    }
}
