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
package dev.aherscu.qa.testing.example;

import static java.util.Objects.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * <p>
 * Support for multi-threaded application tests. Manages WebDriver per test
 * method by opening it with default provider and device types per
 * {@link TestConfiguration}. This enables each test method to run in its own
 * thread.
 * </p>
 * 
 * <p>
 * Eventually, multiple support classes may be derived from this one and
 * override above behaviors as needed.
 * </p>
 *
 * @author aherscu
 */
@UITest
@Slf4j
public abstract class ApplicationPerMethodWebSessionTest
    extends ApplicationUnmanagedSessionTest {

    /**
     * WebDriver client to be managed by this scenario.
     */
    protected final ThreadLocal<WebDriverEx> webDriver =
        new ThreadLocal<>();

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
     * using {@link TestConfiguration}.
     */
    @BeforeMethod
    @SneakyThrows
    protected void beforeMethodOpenWebDriver() {
        log.debug("before method openning web driver");
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
