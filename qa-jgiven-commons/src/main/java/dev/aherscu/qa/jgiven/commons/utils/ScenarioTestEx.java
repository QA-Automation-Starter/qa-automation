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

import org.slf4j.bridge.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.base.*;
import com.tngtech.jgiven.impl.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * This is a complete replacement of
 * {@link com.tngtech.jgiven.testng.ScenarioTest} in order to use
 * {@link ScenarioTestListenerEx}.
 * 
 * @param <GIVEN>
 *            type of given stage
 * @param <WHEN>
 *            type of when stage
 * @param <THEN>
 *            type of then stage
 */
@Listeners(ScenarioTestListenerEx.class)
@Slf4j
public class ScenarioTestEx<GIVEN, WHEN, THEN> extends
    ScenarioTestBase<GIVEN, WHEN, THEN> {

    static {
        log.trace("installing slf4j bridge handler");
        SLF4JBridgeHandler.install();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        val scenario = ScenarioHolder.get().getScenarioOfCurrentThread();

        if (scenario instanceof Scenario)
            return (Scenario<GIVEN, WHEN, THEN>) scenario;

        throw new Error(
            "should not happen!!! just to make spotbugs happy :)");
    }
}
