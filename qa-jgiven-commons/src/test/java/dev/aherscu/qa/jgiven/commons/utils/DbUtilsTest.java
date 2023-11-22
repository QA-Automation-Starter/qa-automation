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

package dev.aherscu.qa.jgiven.commons.utils;

import static com.google.common.base.Suppliers.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.function.*;

import javax.sql.*;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.testng.annotations.*;

import com.zaxxer.hikari.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class DbUtilsTest {
    private final Supplier<QueryRunner> queryRunnerSupplier =
        memoize(() -> new QueryRunner(getDataSource()));

    private static DataSource getDataSource() {
        val dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:derby:memory:testing;create=true");
        return dataSource;
    }

    @Test
    @SneakyThrows
    public void shouldUseDb() {
        queryRunner()
            .execute("insert into TEST_TABLE values (7)");
        queryRunner()
            .execute("insert into TEST_TABLE values (8)");
        assertThat(queryRunner()
            .query("select INTEGER_COLUMN from APP.TEST_TABLE",
                new ArrayListHandler())
            .stream(),
            adaptedStream(row -> row[0],
                hasSpecificItems(1, 7, 8)));
    }

    @BeforeClass
    @SneakyThrows
    private void beforeClassInitDb() {
        queryRunner().execute(
            "create table APP.TEST_TABLE(INTEGER_COLUMN INTEGER)");
        queryRunner().execute(
            "insert into APP.TEST_TABLE values (1)");
    }

    private HikariDataSource dataSource(final String url) {
        val dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        return dataSource;
    }

    private QueryRunner queryRunner() {
        return queryRunnerSupplier.get();
    }
}
