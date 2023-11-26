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
package dev.aherscu.qa.jgiven.commons.scenarios;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;

import java.lang.SuppressWarnings;

import org.testng.annotations.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.config.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Contains sample tests just to ensure that the testing infrastructure works as
 * required.
 *
 * @author aherscu
 *
 */
@SuppressFBWarnings(
    value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
    justification = "framework limitation")
@SuppressWarnings({ "static-method" })
@SelfTest
@DataSet(loadStrategy = InsertLoadStrategy.class)
@Slf4j
public final class DatabaseTest extends
    UnitilsScenarioTest<BaseConfiguration, AnyScenarioType, GenericFixtures<AnyScenarioType, ?>, GenericActions<AnyScenarioType, ?>, GenericVerifications<AnyScenarioType, ?>> {

    /**
     * Initializes with {@link BaseConfiguration}.
     */
    private DatabaseTest() {
        super(BaseConfiguration.class);
    }

    @Test
    @SneakyThrows
    public void shouldSucceedFindingSomethingElseInDatabase() {
        then()
            .querying_$_evaluates_as(
                "select NAME from TEST_TABLE",
                adaptedStream(row -> row[0],
                    hasSpecificItems(
                        // NOTE: initial values are preserved by the load
                        // strategy
                        "initial value 1",
                        "dataset value 2")));
    }

    @Test
    @SneakyThrows
    public void shouldSucceedFindingSomethingInDatabase() {
        then()
            .querying_$_evaluates_as(
                "select NAME from TEST_TABLE",
                adaptedStream(row -> row[0],
                    hasSpecificItems(
                        // NOTE: initial values are preserved by the load
                        // strategy
                        "initial value 1",
                        "initial value 2",
                        "dataset value 1",
                        "dataset value 2")));
    }
}
