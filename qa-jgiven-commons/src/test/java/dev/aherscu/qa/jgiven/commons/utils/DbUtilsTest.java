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
import static java.time.Duration.*;
import static java.util.concurrent.CompletableFuture.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.function.*;
import java.util.stream.*;

import javax.sql.*;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.jooq.lambda.*;
import org.testng.annotations.*;

import com.zaxxer.hikari.*;

import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

@Slf4j
public class DbUtilsTest {
    private final Supplier<QueryRunner> queryRunnerSupplier =
        memoize(() -> new QueryRunner(getDataSource()));

    private static DataSource getDataSource() {
        val dataSource = new HikariDataSource();
        // NOTE: database name must be unique for this class
        // otherwise its initialization may fail when running with other class
        dataSource.setJdbcUrl("jdbc:derby:memory:DbUtilsTest;create=true");
        return dataSource;
    }

    @Test
    @SneakyThrows
    public void shouldUseDb() {
        assertThat(queryRunner()
            .query("select NAME from TEST_TABLE",
                new ArrayListHandler())
            .stream(),
            adaptedStream(row -> row[0],
                hasSpecificItems("inserted value 1")));
    }

    @Test
    // @Ignore // TODO complete implementation
    @SneakyThrows
    public void shouldUseDbInParallel() {
        val r = 10_000; // more repetitions need more retries
        allOf(
            runAsync(() -> IntStream.range(0, r)
                .parallel()
                .forEach(Unchecked.intConsumer(i -> queryRunner()
                    .execute("insert into TEST_TABLE values (?)",
                        String.valueOf(i))))),
            runAsync(() -> IntStream.range(0, r)
                .parallel()
                .forEach(Unchecked.intConsumer(i -> Failsafe
                    .with(new RetryPolicy<>()
                        .withDelay(ofSeconds(1))
                        .onRetry(e -> log.debug(">>> {}", e)))
                    .run(() -> assertThat(queryRunner()
                        .query("select NAME from TEST_TABLE",
                            new ArrayListHandler())
                        .stream(),
                        // .peek(row -> log.debug(">>> {}", row[0])),
                        adaptedStream(row -> row[0],
                            hasSpecificItems(String.valueOf(i)))))))))
            .join();
    }

    @BeforeClass
    @SneakyThrows
    private void beforeClassInitDb() {
        queryRunner().execute(
            "create table TEST_TABLE(NAME VARCHAR(20))");
        queryRunner().execute(
            "insert into TEST_TABLE values ('inserted value 1')");
    }

    private QueryRunner queryRunner() {
        return queryRunnerSupplier.get();
    }
}
