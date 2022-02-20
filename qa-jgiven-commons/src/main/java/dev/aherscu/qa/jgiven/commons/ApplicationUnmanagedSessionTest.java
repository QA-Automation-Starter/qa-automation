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

package dev.aherscu.qa.jgiven.commons;

import org.openqa.selenium.*;

import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

/**
 * Provides basic application testing scenario sections.
 *
 * @author Adrian Herscu
 *
 */
@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
@Slf4j
// BUG see UnitilsScenarioTest#unitilsBeforeMethod call to
// UnitilsScenarioTest#setFormattedThreadName
// <C extends AbstractConfiguration<? extends Configuration>, T extends
// AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN
// extends GenericActions<T, ?> & ScenarioType<T>, THEN extends
// GenericVerifications<T, ?> & ScenarioType<T>>
public abstract class ApplicationUnmanagedSessionTest<C extends WebDriverConfiguration, GIVEN extends WebDriverFixtures<?>, WHEN extends WebDriverActions<?>, THEN extends WebDriverVerifications<?>>
    extends UnitilsScenarioTest<C, WebDriverScenarioType, GIVEN, WHEN, THEN> {

    protected ApplicationUnmanagedSessionTest(Class<C> configurationType) {
        super(configurationType);
    }

    /**
     * Scenario starting section that uses a specific WebDriver.
     *
     * @param webDriver
     *            the WebDriver to use for scenario
     */
    protected final void starting_section(final WebDriverEx webDriver) {

        section("starting the application");

        given()
            .a_web_driver(webDriver);

        // then verify login or landing page

    }

    /**
     * Scenario starting section using specific Web Driver capabilities.
     *
     * @param capabilities
     *            the WebDriver capabilities to use for scenario
     */
    protected final void starting_section(final Capabilities capabilities) {
        starting_section(WebDriverEx.from(capabilities));
    }
}
