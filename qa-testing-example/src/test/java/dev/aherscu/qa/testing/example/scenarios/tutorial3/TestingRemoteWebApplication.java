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

package dev.aherscu.qa.testing.example.scenarios.tutorial3;

import static dev.aherscu.qa.testing.utils.StreamMatchers.*;
import static java.util.Objects.*;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.remote.CapabilityType.*;

import java.net.*;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class TestingRemoteWebApplication {
    public final static String SAUCELABS_PASSWORD;
    public final static String SAUCELABS_URL;
    public final static String SAUCELABS_USER;

    static {
        SAUCELABS_PASSWORD =
            requireNonNull(System.getenv("SAUCELABS_PASSWORD"),
                "missing SauceLabs password");
        SAUCELABS_USER =
            requireNonNull(System.getenv("SAUCELABS_USER"),
                "missing SauceLabs username");
        SAUCELABS_URL = String
            .format("https://%s:%s@ondemand.saucelabs.com:443/wd/hub",
                SAUCELABS_USER, SAUCELABS_PASSWORD);
    }

    private WebDriver webDriver;

    @Test
    public void shouldFind() {
        // NOTE the search keyword must be unique such that it is not
        // translated to other languages or written differently
        val SEARCH_KEYWORD = "testng";
        webDriver.findElement(By.name("q"))
            .sendKeys(SEARCH_KEYWORD + Keys.ENTER);
        assertThat(
            webDriver.findElements(By.xpath("//a/h3"))
                .stream()
                .map(webElement -> webElement.getAttribute("textContent"))
                .peek(resultTitle -> log.debug("found {}", resultTitle)),
            allMatch(either(containsStringIgnoringCase("testng"))
                .or(containsStringIgnoringCase("Try again"))
                .or(containsStringIgnoringCase("More results"))));
    }

    @Test
    public void shouldOpenWeb() {
        assertThat(webDriver.getTitle(), containsString("Google"));
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @AfterClass(alwaysRun = true) // important, otherwise we may leak resources
    private void afterClassCloseWebDriver() {
        webDriver.quit();
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @BeforeClass
    @SneakyThrows
    private void beforeClassOpenWebDriver() {
        log.trace("connecting saucelabs with {}:{}",
            SAUCELABS_USER, SAUCELABS_PASSWORD);
        webDriver = new RemoteWebDriver(new URL(SAUCELABS_URL),
            new DesiredCapabilitiesEx()
                .with(BROWSER_NAME, "firefox")
        // NOTE since Selenium 4, non-standard capabilities must be prefixed;
        // see https://www.w3.org/TR/webdriver1/#capabilities
        // and https://docs.saucelabs.com/dev/test-configuration-options/
        // and
        // https://docs.saucelabs.com/dev/test-configuration-options/#desktop-and-mobile-capabilities-sauce-specific--optional
        // and
        // https://docs.saucelabs.com/mobile-apps/automated-testing/appium/appium-2-migration/
        // For example,
        // .with("my:capability", "kuku")
        );

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
        webDriver.get("https://google.com?hl=en");
    }
}
