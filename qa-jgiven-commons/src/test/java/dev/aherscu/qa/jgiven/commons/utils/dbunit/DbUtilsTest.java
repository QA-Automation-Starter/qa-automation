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

import static com.google.common.base.Suppliers.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.time.Duration.*;
import static java.util.concurrent.CompletableFuture.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.function.*;
import java.util.stream.*;

import javax.sql.*;

import org.jooq.lambda.*;
import org.testng.annotations.*;

import com.zaxxer.hikari.*;

import dev.aherscu.qa.jgiven.commons.utils.dbunit.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

@Slf4j
public class DbUtilsTest {
    private final Supplier<StreamingQueryRunner> queryRunnerSupplier =
        memoize(() -> new StreamingQueryRunner(getDataSource()));

    private static DataSource getDataSource() {
        val dataSource = new HikariDataSource();
        // NOTE: database name must be unique for this class
        // otherwise its initialization may fail when running in parallel with
        // other class
        dataSource.setJdbcUrl("jdbc:derby:memory:DbUtilsTest;create=true");
        return dataSource;
    }

    @Test
    @SneakyThrows
    public void shouldUseDb() {
        try (val results = queryRunner()
            .queryStream("select NAME from TEST_TABLE")) {
            assertThat(results,
                adaptedStream(row -> row[0],
                    hasSpecificItems("inserted value 1")));
        }
    }

    @Test
    @SneakyThrows
    public void shouldUseDbInParallel() {
        val repetitions = 100_000; // takes about 7 seconds
        val retryPolicy = new RetryPolicy<>()
            .withDelay(ofSeconds(2))
            .withMaxDuration(ofSeconds(10))
            .onRetry(e -> log.debug(">>> {}",
                abbreviate(e.toString(), 120)));

        // NOTE: the QueryRunner is thread-safe and each time its methods
        // are called a new connection is retrieved from its associated data
        // source.
        allOf(
            runAsync(() -> IntStream.range(0, repetitions).parallel()
                .forEach(Unchecked.intConsumer(i -> Failsafe
                    .with(retryPolicy).run(() -> queryRunner()
                        .execute("insert into TEST_TABLE values (?)",
                            String.valueOf(i)))))),
            runAsync(() -> IntStream.range(0, repetitions).parallel()
                .forEach(Unchecked.intConsumer(i -> Failsafe
                    .with(retryPolicy).run(() -> {
                        try (val results = queryRunner()
                            .queryStream(
                                // NOTE: select one row to save time and memory
                                "select NAME from TEST_TABLE where NAME=?",
                                String.valueOf(i))) {
                            assertThat(results,
                                // .peek(row -> log.debug(">>> {}", row[0])),
                                adaptedStream(row -> row[0],
                                    hasSpecificItems(String.valueOf(i))));
                        }
                    })))))
            .join();
    }

    @BeforeClass
    @SneakyThrows
    private void beforeClassInitDb() {
        queryRunner().execute(
            "create table TEST_TABLE(NAME VARCHAR(20))");
        // NOTE: indexing allows running fast shouldUseDbInParallel
        queryRunner().execute(
            "create index TEST_TABLE_INDEX on TEST_TABLE(NAME)");
        queryRunner().execute(
            "insert into TEST_TABLE values ('inserted value 1')");
    }

    private StreamingQueryRunner queryRunner() {
        return queryRunnerSupplier.get();
    }
}
