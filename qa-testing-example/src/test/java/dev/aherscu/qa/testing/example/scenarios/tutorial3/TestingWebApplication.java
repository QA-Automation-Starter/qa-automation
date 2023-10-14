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

import static dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest.*;
import static dev.aherscu.qa.testing.utils.StreamMatchers.allMatch;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.function.*;

import org.jooq.lambda.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import io.github.bonigarcia.wdm.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class TestingWebApplication {
    private final WebDriver webDriver;

    @Factory(dataProvider = INTERNAL_DATA_PROVIDER)
    public TestingWebApplication(Supplier<WebDriver> webDriver) {
        this.webDriver = webDriver.get();
        log.trace("testing with {}", webDriver);
    }

    @DataProvider
    private static Object[][] data() {
        // NOTE we use suppliers in order to lazily create drivers;
        // thus, only when the test is constructed by TestNG
        return new Object[][] {
            { Unchecked.supplier(() -> {
                log.trace("setting up firefox driver");
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            }) },
            { Unchecked.supplier(() -> {
                log.trace("setting up chrome driver");
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            }) },
            // TODO setup Selenium on GitHub runner or wait for SauceLabs
            // { Unchecked.supplier(() -> new RemoteWebDriver(
            // new URL("http://localhost:4444"),
            // new DesiredCapabilitiesEx() {
            // {
            // setCapability(BROWSER_NAME, "firefox");
            // }
            // })) }
        };

    }

    @Test
    public void shouldOpenWeb() {
        assertThat(webDriver.getTitle(), containsString("Google"));
    }

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
        log.trace("before connecting selenium");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
        webDriver.get("https://google.com");
    }
}
