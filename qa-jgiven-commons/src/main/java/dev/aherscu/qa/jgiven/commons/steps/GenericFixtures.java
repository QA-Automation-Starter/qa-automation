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

import static dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest.queryRunner;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.io.*;
import java.sql.*;

import javax.annotation.concurrent.*;

import org.apache.commons.dbutils.*;
import org.dbunit.dataset.*;
import org.unitils.core.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetfactory.*;
import org.unitils.dbunit.datasetloadstrategy.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic fixtures.
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
@SuppressWarnings("boxing")
public class GenericFixtures<T extends AnyScenarioType, SELF extends GenericFixtures<T, SELF>>
    extends StageEx<SELF>

    implements ScenarioType<T> {

    /**
     * Logs the construction of this stage.
     */
    public GenericFixtures() {
        log.trace("given stage {} constructed", this); //$NON-NLS-1$
    }

    private static DbUnitExModule dbUnitExModule() {
        return Unitils
            .getInstance()
            .getModulesRepository()
            .getModuleOfType(DbUnitExModule.class);
    }

    /**
     * Run a specified Unitils multi-schema dataset file.
     *
     * @see #data(IDataSet)
     *
     * @param multiSchemaDataSetFile
     *            a Unitils multi-schema dataset file; by default, a <a href=
     *            "http://www.unitils.org/tutorial-database.html#Configuring_the_dataset_factory">
     *            multi-schema dataset </a> is expected
     * @return {@link #self()}
     */
    @NestedSteps
    public SELF data(
        final @FileFormatter.Annotation(
            value = 0,
            maxFileSizeToRead = 0) File multiSchemaDataSetFile) {
        log.debug("loading data set from {}", multiSchemaDataSetFile); //$NON-NLS-1$
        dbUnitExModule().insertDataSet(
            dbUnitExModule().createDataSet(multiSchemaDataSetFile));
        return self();
    }

    /**
     * Run a specified DBUnit dataset.
     *
     * <p>
     * This method of data insertion should be prefered instead of Unitils'
     * {@link DataSet} annotation. By using this method your data-sets are
     * rendered in the JGiven report, while otherwise they would not.
     * </p>
     *
     * <p>
     * Internally, uses the default {@link DataSetLoadStrategy} and
     * {@link DataSetFactory}. These may be configured via the
     * {@code unitils.properties} configuration file. This is explained in more
     * detail in <a href=
     * "http://www.unitils.org/tutorial-database.html#Configuring_the_dataset_load_strategy">
     * Configuring the dataset load strategy section of Unitils' Database
     * Tutorial</a>.
     * </p>
     *
     * @see DbUnitExModule#insertDataSet(IDataSet)
     *
     * @param dataSet
     *            a DBUnit dataset object
     * @return {@link #self()}
     */
    // TODO see http://jgiven.org/docs/parameterizedsteps/#tables-as-parameters
    // and also
    // http://jgiven.org/javadoc/com/tngtech/jgiven/annotation/Table.html
    // This should allow formatting the argument as a table in the report.
    @SneakyThrows(DataSetException.class)
    public SELF data(final @DataSetFormatter.Annotation IDataSet dataSet) {
        log.debug("inserting data set into {}", //$NON-NLS-1$
            join(dataSet.getTableNames(), COMMA));
        dbUnitExModule().insertDataSet(dataSet);
        return self();
    }

    /**
     * Runs specified SQL-DML batch.
     *
     * @param sql
     *            the SQL statement to execute without parameters
     *
     * @see QueryRunner#batch(String, Object[][])
     * @return {@link #self()}
     */
    public SELF data(
        @StringFormatter.Annotation(maxWidth = 400) final String sql) {
        return data(sql, new Object[][] { {} });
    }

    /**
     * Runs specified SQL-DML batch.
     *
     * @param sql
     *            the SQL statement to execute
     * @param params
     *            an array of query replacement parameters, where each row in
     *            this array is one set of batch replacement values
     *
     * @see QueryRunner#batch(String, Object[][])
     * @return {@link #self()}
     *
     * @throws IllegalArgumentException
     *             if the array of replacement parameters is empty
     */
    @SneakyThrows(SQLException.class)
    public SELF data(
        @StringFormatter.Annotation(maxWidth = 400) final String sql,
        final Object[][] params) {
        log.debug("executing {} with {} replacement parameters", //$NON-NLS-1$
            sql, params.length);

        if (0 >= params.length)
            throw new IllegalArgumentException("no replacement parameters"); //$NON-NLS-1$

        queryRunner().batch(sql, params);

        return self();
    }

    /**
     * Does nothing -- just initializes the JGiven infrastructure.
     *
     * @return {@link #self()}
     */
    public SELF nothing() {
        return self();
    }
}
