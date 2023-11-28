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

import static dev.aherscu.qa.jgiven.commons.utils.UnitilsJUnitTest.*;

import org.testng.annotations.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;

import lombok.extern.slf4j.*;

@DataSet(loadStrategy = InsertLoadStrategy.class)
@Slf4j
public class UnitilsTestNGTest extends UnitilsTestNG {
    @Test
    public void shouldUseDb() {
        assertExistenceOfInitialAndDataSetValues();
    }

    @Test
    public void shouldUseDbAgain() {
        assertExistenceOfInitialAndDataSetValues();
    }
}
