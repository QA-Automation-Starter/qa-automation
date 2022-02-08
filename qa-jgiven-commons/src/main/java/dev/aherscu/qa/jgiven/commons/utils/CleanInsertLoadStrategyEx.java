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

import java.sql.*;

import org.apache.commons.lang3.exception.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.unitils.core.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;
import org.unitils.dbunit.util.*;

import lombok.extern.slf4j.*;

/**
 * Provides better error diagnostics.
 *
 * @author aherscu
 *
 */
@Slf4j
public class CleanInsertLoadStrategyEx extends CleanInsertLoadStrategy {

    /**
     * Just tracks the initialization of this load strategy.
     */
    public CleanInsertLoadStrategyEx() {
        super();
        log.trace("initialized {}", getClass().getName()); //$NON-NLS-1$
    }

    @Override
    public void execute(
        final DbUnitDatabaseConnection dbUnitDatabaseConnection,
        final IDataSet dataSet) {
        try {
            doExecute(dbUnitDatabaseConnection, dataSet);
        } catch (final SQLException | DatabaseUnitException e) {
            log.error("failed to insert data set due to {}", //$NON-NLS-1$
                ExceptionUtils.getRootCauseMessage(e));
            throw new UnitilsException(e);
        }
    }

}
