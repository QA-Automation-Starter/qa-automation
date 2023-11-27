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
import static dev.aherscu.qa.testing.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.function.*;

import javax.sql.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;

import com.zaxxer.hikari.*;

import lombok.*;
import lombok.extern.slf4j.*;

// NOTE: JUnit3 test
@Slf4j
public class DbUnitTest extends DataSourceBasedDBTestCase {

    private final Supplier<StreamingQueryRunner> queryRunnerSupplier =
        memoize(() -> new StreamingQueryRunner(getDataSource()));

    @SneakyThrows
    public void testWithDbUtils() {
        try (val results = queryRunner()
            .queryStream("select * from TEST_TABLE")) {
            assertThat(results,
                adaptedStream(row -> row[0],
                    hasSpecificItems(
                        "dataset value 1",
                        "dataset value 2")));
        }
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder()
            .build(getRelativeResourceAsStream(DbUnitTest.class,
                "DatabaseTest.xml"));
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        // NOTE: ensures table exists before dataset is applied
        queryRunner()
            .execute("create table TEST_TABLE(NAME VARCHAR(20))");
        return super.getSetUpOperation();
    }

    @Override
    protected DataSource getDataSource() {
        val dataSource = new HikariDataSource();
        // NOTE: database name must be unique for this class
        // otherwise its initialization may fail when running with other class
        dataSource.setJdbcUrl("jdbc:derby:memory:DbUnitTest;create=true");
        return dataSource;
    }

    protected StreamingQueryRunner queryRunner() {
        return queryRunnerSupplier.get();
    }
}
