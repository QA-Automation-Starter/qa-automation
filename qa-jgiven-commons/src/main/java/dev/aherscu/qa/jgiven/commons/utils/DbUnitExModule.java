/*
 * Copyright 2022 Adrian Herscu
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

import java.io.*;

import org.dbunit.dataset.*;
import org.unitils.dbunit.*;
import org.unitils.dbunit.datasetloadstrategy.*;
import org.unitils.dbunit.util.*;

import lombok.extern.slf4j.*;

/**
 * Provides data insertion method using the default dataset factory.
 * 
 * <p>
 * In order to use this class during runtime, the {@code unitils.properties}
 * file of the project shall contain
 * {@code unitils.module.dbunit.className=DbUnitExModule} .
 *
 * @author aherscu
 *
 */
@Slf4j
public class DbUnitExModule extends DbUnitModule {

    /**
     * Initializes the database module.
     */
    public DbUnitExModule() {
        super();
        log.trace("initialized database module"); //$NON-NLS-1$
    }

    // ISSUE: no straightforward implementation because the expected
    // MultiSchemaDataset is not IDataSet compatible
    // /**
    // * Inserts the test data coming from multiple DbUnit dataset files using
    // the
    // * default {@link DataSetLoadStrategy} and {@link DataSetFactory} class.
    // *
    // * @param dataSetFiles
    // * one or more data set files, not null
    // */
    // public void insertDataSet(final File... dataSetFiles) {
    // val dataSetFactory = getDefaultDataSetFactory();
    // val dataSetLoadStrategy = getDefaultDataSetLoadStrategy();
    //
    // List<IDataSet> dataSets = new ArrayList<>();
    // for (val dataSetFile : dataSetFiles) {
    // dataSets.add(dataSetFactory.createDataSet(dataSetFile));
    // }
    // insertDataSet(multiSchemaDataSet, dataSetLoadStrategy);
    // }

    /**
     * Creates a multi-schema data-set from given DbUnit dataset file.
     * 
     * @param dataSetFile
     *            The test data set, not null
     * @return the data-set
     */
    public MultiSchemaDataSet createDataSet(final File dataSetFile) {
        return getDefaultDataSetFactory().createDataSet(dataSetFile);
    }

    /**
     * Loads the given dataset into the default schema of the database, using
     * the default load strategy.
     * 
     * @param dataSet
     *            dataset that is inserted in the database
     */
    public void insertDataSet(final IDataSet dataSet) {
        insertDataSet(dataSet, getDefaultDataSetLoadStrategy());
    }

    /**
     * Loads the given dataset into the default schema of the database, using a
     * specified load strategy.
     * 
     * @param dataSet
     *            dataset that is inserted in the database
     * @param dataSetLoadStrategy
     *            the load strategy that is used
     */
    public void insertDataSet(
        final IDataSet dataSet,
        final DataSetLoadStrategy dataSetLoadStrategy) {
        try {
            dataSetLoadStrategy
                .execute(
                    getDbUnitDatabaseConnection(
                        getDefaultDbSupport().getSchemaName()),
                    dataSet);
        } finally {
            closeJdbcConnection();
        }
    }

    /**
     * Loads the given dataset into the default schema of the database, using
     * the default load strategy.
     * 
     * @param multiSchemaDataSet
     *            multi-schema dataset that is inserted in the database
     */
    public void insertDataSet(final MultiSchemaDataSet multiSchemaDataSet) {
        super.insertDataSet(multiSchemaDataSet,
            getDefaultDataSetLoadStrategy());
    }
}
