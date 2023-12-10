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

package dev.aherscu.qa.jgiven.webdriver;

import org.openqa.selenium.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.model.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;
import edu.umd.cs.findbugs.annotations.*;

/**
 * Provides basic application testing scenario sections.
 *
 * @param <C>
 *            type of WebDriverConfiguration
 * @param <GIVEN>
 *            type of fixtures
 * @param <WHEN>
 *            type of actions
 * @param <THEN>
 *            type of verification
 * @author Adrian Herscu
 *
 */
@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
// BUG see UnitilsScenarioTest#unitilsBeforeMethod call to
// UnitilsScenarioTest#setFormattedThreadName
// <C extends AbstractConfiguration<? extends Configuration>, T extends
// AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN
// extends GenericActions<T, ?> & ScenarioType<T>, THEN extends
// GenericVerifications<T, ?> & ScenarioType<T>>
public abstract class ApplicationUnmanagedSessionTest<C extends WebDriverConfiguration, GIVEN extends WebDriverFixtures<?>, WHEN extends WebDriverActions<?>, THEN extends WebDriverVerifications<?>>
    extends
    ConfigurableScenarioTest<C, WebDriverScenarioType, GIVEN, WHEN, THEN> {

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
