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
package dev.aherscu.qa.jgiven.webdriver.scenarios;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.aherscu.qa.testing.utils.MatchersExtensions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.model.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;
import dev.aherscu.qa.jgiven.webdriver.tags.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

/**
 * Contains web driver sample tests just to ensure that the testing
 * infrastructure works as required.
 *
 * @author aherscu
 *
 */
@SelfTest
@WebDriverTest
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE",
    justification = "JGiven framework limitation")
@Slf4j
public final class GenericWebDriverTest extends AbstractWireMockTest<WebDriverScenarioType, WebDriverFixtures<?>, WebDriverActions<?>, WebDriverVerifications<?>> {

    private WebDriverEx driver;

    @Test(dependsOnMethods = "shouldOpenWebDriver",
        expectedExceptions = InvalidSelectorException.class)
    public void shouldFailFinding() {
        given().a_web_driver(driver);
        // same browser on same page as set by previous method
        then().the_title(containsString("hello")).and().element(
            By.xpath("bad-xpath["),
            adaptedObject(WebElement::getText, is("kuku")));
    }

    /**
     * Should open web driver.
     */
    @Test
    public void shouldOpenWebDriver() {
        given().a_web_driver(driver);
        when().opening(wireMockServer.baseUrl());
        then().the_title(containsString("hello"));
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @AfterClass(alwaysRun = true)
    private void afterClassQuitWebDriver() {
        driver.safelyQuit();
        log.debug("quit web driver");
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @BeforeClass
    private void beforeClassOpenWebDriver() {
        driver = new WebDriverEx(new HtmlUnitDriver(true),
            new DesiredCapabilitiesEx());
        log.debug("opened web driver {}", driver);
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @BeforeClass
    private void beforeClassSetWebStub() {
        wireMockServer.stubFor(get(urlEqualTo("/"))
            .willReturn(aResponse()
                .withBody("<html><title>hello</title><html>")));
    }
}
