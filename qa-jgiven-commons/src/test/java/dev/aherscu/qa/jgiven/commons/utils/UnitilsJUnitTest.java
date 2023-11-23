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

import lombok.*;
import lombok.extern.slf4j.*;

// ISSUE dataset fails to apply due to
//  org.dbunit.dataset.NoSuchTableException: TEST_TABLE
// It seems like the DbMaintainer script is applied to different Derby instance
@DataSet("DatabaseTest.xml")
@Slf4j
public class UnitilsJUnitTest extends UnitilsJUnit3 {
    @SneakyThrows
    public void testUsingDb() {
        assertThat(new QueryRunner(DatabaseUnitils.getDataSource())
            .query("select * from TEST_TABLE", new ArrayListHandler())
            .stream(),
            adaptedStream(row -> row[0],
                hasSpecificItems(7, 8, 1, 2)));
    }
}
