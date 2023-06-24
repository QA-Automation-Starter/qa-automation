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
package dev.aherscu.qa.jgiven.commons.formatters;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;

import org.dbunit.dataset.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

import lombok.*;

/**
 * Formats an {@link IDataSet} such that each table appears in a separate
 * section and each row appears on separate line.
 * 
 * <p>
 * NOTE: meanwhile, only table names are reported
 * </p>
 *
 * @author aherscu
 *
 */
@ThreadSafe
public class DataSetFormatter implements
    ArgumentFormatter<IDataSet> {

    @Override
    @SneakyThrows(DataSetException.class)
    public String format(
        final IDataSet dataSet,
        final String... notUsed) {
        // for (String tableName : dataSet.getTableNames()) {
        // final Column[] columns = dataSet
        // .getTable(tableName)
        // .getTableMetaData()
        // .getColumns();
        // int rowCount = dataSet.getTable(tableName).getRowCount();
        // for(int currentRow = 0; currentRow < rowCount; currentRow++) {
        //
        // dataSet.getTable(tableName).getValue(row, column);
        // }
        // }
        // TODO loop over the dataset
        // ISSUE looping over the dataset is not enough; the output should be
        // formatted
        return join(dataSet.getTableNames(), COMMA);
    }

    /**
     * Formatter annotation for {@link IDataSet}s.
     *
     * @author aherscu
     */
    @Format(value = DataSetFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        // no parameter to declare
    }
}
