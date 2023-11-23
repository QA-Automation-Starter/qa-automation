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

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.unitils.*;
import org.unitils.database.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;

import lombok.*;
import lombok.extern.slf4j.*;

@DataSet(value = "DatabaseTest.xml", loadStrategy = InsertLoadStrategy.class)
@Slf4j
public class UnitilsJUnitTest extends UnitilsJUnit3 {
    @SneakyThrows
    public void testUsingDb() {
        assertThat(new QueryRunner(DatabaseUnitils.getDataSource("testing"))
            .query("select * from TEST_TABLE", new ArrayListHandler())
            .stream(),
            adaptedStream(row -> row[0],
                hasSpecificItems(
                    // NOTE: initial values are preserved by the load strategy
                    "initial value 1",
                    "initial value 2",
                    "dataset value 1",
                    "dataset value 2")));
    }
}
