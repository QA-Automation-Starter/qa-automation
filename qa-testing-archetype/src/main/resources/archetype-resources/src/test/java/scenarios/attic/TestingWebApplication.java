/*
 * Copyright 2024 Adrian Herscu
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

package ${package}.scenarios.attic;

import static dev.aherscu.qa.jgiven.commons.utils.ConfigurableScenarioTest.*;
import static dev.aherscu.qa.testing.utils.StreamMatchers.*;
import static ${package}.scenarios.attic.TestingRemoteWebApplication.*;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.remote.CapabilityType.*;

import java.net.*;
import java.util.function.*;

import org.jooq.lambda.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import edu.umd.cs.findbugs.annotations.*;
import io.github.bonigarcia.wdm.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class TestingWebApplication {
    private final WebDriver webDriver;

    @Factory(dataProvider = INTERNAL_DATA_PROVIDER)
    public TestingWebApplication(final Supplier<WebDriver> webDriver) {
        this.webDriver = webDriver.get();
        log.trace("testing with {}", webDriver);
    }

    // NOTE: parallel with @Factory requires parallel=instances
    // see https://github.com/testng-team/testng/issues/1951
    @DataProvider(parallel = true)
    private static Object[][] data() {
        // NOTE we use suppliers in order to lazily create drivers;
        // thus, only when the test is constructed by TestNG
        return new Object[][] {
            // ISSUE Firefox cannot be installed on certain systems
            // { Unchecked.supplier(() -> {
            // log.trace("setting up firefox driver");
            // WebDriverManager.firefoxdriver().setup();
            // return new FirefoxDriver();
            // }) },
            { Unchecked.supplier(() -> {
                log.trace("setting up chrome driver");
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            }) },
            { Unchecked.supplier(() -> {
                log.trace("setting up remote driver");
                return new RemoteWebDriver(
                    new URL(SAUCELABS_URL),
                    new DesiredCapabilitiesEx()
                        .with(BROWSER_NAME, "firefox"));
            }) }
        };
    }

    @Test
    public void shouldFind() {
        // NOTE the search keyword must be unique such that it is not
        // translated to other languages or written differently
        val SEARCH_KEYWORD = "testng";
        log.debug("searching for {} on Google", SEARCH_KEYWORD);
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
        log.debug("window title must contain Google");
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
        log.trace("before connecting selenium");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
        webDriver.get("https://google.com?hl=en"); // ensure English
    }
}
