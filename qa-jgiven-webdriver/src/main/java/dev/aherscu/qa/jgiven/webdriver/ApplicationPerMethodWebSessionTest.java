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

import static java.util.Objects.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;
import dev.aherscu.qa.jgiven.webdriver.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * <p>
 * Support for multi-threaded application tests. Manages WebDriver per test
 * method by opening it with default provider and device types per
 * {@link WebDriverConfiguration}. This enables each test method to run in its
 * own thread.
 * </p>
 *
 * <p>
 * Eventually, multiple support classes may be derived from this one and
 * override above behaviors as needed.
 * </p>
 *
 * @param <C>
 *            type of WebDriverConfiguration
 * @param <GIVEN>
 *            type of fixtures
 * @param <WHEN>
 *            type of actions
 * @param <THEN>
 *            type of verification
 * @author aherscu
 */
@UITest
@Slf4j
public abstract class ApplicationPerMethodWebSessionTest<C extends WebDriverConfiguration, GIVEN extends WebDriverFixtures<?>, WHEN extends WebDriverActions<?>, THEN extends WebDriverVerifications<?>>
    extends ApplicationUnmanagedSessionTest<C, GIVEN, WHEN, THEN> {

    /**
     * WebDriver client to be managed by this scenario.
     */
    protected final ThreadLocal<WebDriverEx> webDriver =
        new ThreadLocal<>();

    protected ApplicationPerMethodWebSessionTest(Class<C> configurationType) {
        super(configurationType);
    }

    /**
     * After each test method finishes, closes the managed WebDriver.
     */
    @AfterMethod(alwaysRun = true)
    protected void afterMethodQuitWebDriver() {
        log.debug("after method quitting web driver");
        requireNonNull(webDriver.get(),
            "web driver not initialized nothing to quit")
            .safelyQuit();
    }

    /**
     * Before each test method begins execution, opens the managed WebDriver
     * using {@link WebDriverConfiguration}.
     */
    @BeforeMethod
    @SneakyThrows
    protected void beforeMethodOpenWebDriver() {
        log.debug("before method opening web driver");
        webDriver.set(WebDriverEx
            .from(configuration().capabilities()));
    }

    /**
     * Scenario starting section that uses the managed WebDriver.
     */
    protected final void starting_section() {
        starting_section(webDriver.get());
    }
}
