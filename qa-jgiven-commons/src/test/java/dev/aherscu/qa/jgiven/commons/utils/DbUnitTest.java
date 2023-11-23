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
import static dev.aherscu.qa.testing.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.function.*;

import javax.sql.*;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
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

    private final Supplier<QueryRunner> queryRunnerSupplier =
        memoize(() -> new QueryRunner(getDataSource()));

    @SneakyThrows
    public void testWithDbUtils() {
        queryRunner()
            .execute("insert into TEST_TABLE values (7)");
        queryRunner()
            .execute("insert into TEST_TABLE values (8)");
        assertThat(queryRunner()
            .query("select * from TEST_TABLE", new ArrayListHandler())
            .stream(),
            adaptedStream(row -> row[0],
                // NOTE 1 and 2 arrive from DatabaseTest.xml
                hasSpecificItems(1, 2, 7, 8)));
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
            .execute("create table TEST_TABLE(INTEGER_COLUMN INTEGER)");
        return super.getSetUpOperation();
    }

    @Override
    protected DataSource getDataSource() {
        val dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:derby:memory:testing;create=true");
        return dataSource;
    }

    protected QueryRunner queryRunner() {
        return queryRunnerSupplier.get();
    }
}
