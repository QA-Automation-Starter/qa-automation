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

import io.github.bonigarcia.wdm.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Manages the application lifecycle (WebDriver) to span the entire testcase.
 * This requires all test methods to run on same thread.
 *
 * <p>
 * IMPORTANT: running all test methods on same WebDriver instance means holding
 * a long session, hence more susceptible to emulator/application crashes or
 * network drops. We provide here the means to recover from such failures via
 * {@link #continuing_section(Runnable)}
 * </p>
 *
 * @author Adrian Herscu
 * @param <C>
 *            type of WebDriverConfiguration
 * @param <GIVEN>
 *            type of fixtures
 * @param <WHEN>
 *            type of actions
 * @param <THEN>
 *            type of verification
 */
@Listeners({ ExceptionPerThreadListener.class })
@Slf4j
// ISSUE https://github.com/cbeust/testng/issues/54
// Current TestNG implementation ignores
// @Test(singleThreaded = true)
// when running with parallel="methods" different threads are used
// causing tests to fail due to uninitialized WebDriver.
// Same for TestNG 7.3.0.
public abstract class ApplicationPerClassWebSessionTest<C extends WebDriverConfiguration, GIVEN extends WebDriverFixtures<?>, WHEN extends WebDriverActions<?>, THEN extends WebDriverVerifications<?>>
    extends ApplicationPerMethodWebSessionTest<C, GIVEN, WHEN, THEN> {
    protected ApplicationPerClassWebSessionTest(Class<C> configurationType) {
        super(configurationType);
    }

    /**
     * Closes the managed WebDriver after all test methods finished.
     */
    @AfterClass(alwaysRun = true)
    protected void afterClassQuitWebDriver() {
        log.debug("after class quitting web driver");
        requireNonNull(webDriver.get(),
            "web driver not initialized nothing to quit")
            .safelyQuit();
    }

    /**
     * Disables per method WebDriver management.
     *
     * @see #beforeMethodOpenWebDriver()
     */
    @Override
    protected final void afterMethodQuitWebDriver() {
        // do nothing
    }

    /**
     * Disables per method WebDriver management.
     *
     * @see #afterMethodQuitWebDriver()
     */
    @Override
    protected final void beforeMethodOpenWebDriver() {
        // do nothing
    }

    /**
     * Opens the managed WebDriver before any test method begins running.
     */
    @BeforeClass
    @SneakyThrows
    protected void beforeClassOpenWebDriver() {
        log.debug("before class opening web driver");
        webDriver.set(WebDriverEx.from(configuration().capabilities(),
            WebDriverManager::setup));
    }

    /**
     * Continues on same device session without recovery.
     *
     * @see #continuing_section(Runnable)
     */
    protected void continuing_section() {
        continuing_section(() -> log.error( // NOTE: cannot throw here because
                                            // it breaks dryrun-mode
            "web driver session dropped and no recovery procedure defined"));
    }

    /**
     * Continues on same device with specified recovery procedure.
     *
     * @param recoveryProcedure
     *            recovery procedure to run in case of Selenium server failure
     */
    protected void continuing_section(final Runnable recoveryProcedure) {
        section("continuing on running application");

        if (nonNull(webDriver.get())) {
            given()
                .a_web_driver(webDriver.get());
        } else {
            log.debug("running recovery procedure");
            recoveryProcedure.run();
        }
    }
}
