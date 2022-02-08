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

import static dev.aherscu.qa.tester.utils.TrustAllX509TrustManager.*;

import javax.ws.rs.client.*;

import org.openqa.selenium.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.tester.utils.rest.*;
import dev.aherscu.qa.testing.example.steps.*;
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
public abstract class ApplicationUnmanagedSessionTest extends
    AbstractApplicationTest<WebDriverScenarioType, ApplicationUIFixtures<?>, ApplicationUIActions<?>, ApplicationUIVerifications<?>> {

    /**
     * Application REST client for this scenario.
     */
    protected final ThreadLocal<Client>       applicationRestClient =
        new ThreadLocal<>();

    /**
     * Application REST fixtures for this scenario.
     */
    @ScenarioStage
    protected ApplicationRestFixtures<?>      applicationRestFixtures;
    /**
     * Application REST actions for this scenario.
     */
    @ScenarioStage
    protected ApplicationRestActions<?>       applicationRestActions;
    /**
     * Application REST verifications for this scenario.
     */
    @ScenarioStage
    protected ApplicationRestVerifications<?> applicationRestVerifications;

    /**
     * After all test methods finish, closes the managed application REST client
     */
    @AfterClass(alwaysRun = true)
    protected void afterClassCloseApplicationRestClient() {
        log.debug("closing application REST client");
        applicationRestClient.get().close();

    }

    /**
     * Before test methods begin execution, initializes the managed application
     * REST client from {@link TestConfiguration}.
     */
    @BeforeClass
    protected void beforeClassOpenApplicationRestClient() {
        log.debug("opening application REST client");
        applicationRestClient.set(LoggingClientBuilder
            .newClient(ClientBuilder.newBuilder()
                .sslContext(TRUST_ALL_SSL_CONTEXT)
                .build()));
        // NOTE register filters, encryptors, etc. here;
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
