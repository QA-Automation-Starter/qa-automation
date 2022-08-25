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

package dev.aherscu.qa.testing.example.scenarios.tutorial;

import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static uk.co.probablyfine.matchers.StreamMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import io.github.bonigarcia.wdm.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class _3_TestingWebApplication {

    private WebDriver webDriver;

    @Test
    public void shouldOpenWeb() {
        assertThat(webDriver.getTitle(), containsString("Google"));
    }

    @Test
    public void shouldFind() {
        // NOTE the search keyword must be unique such that it is not
        // translated to other languages or written differently
        final String SEARCH_KEYWORD = "testng";
        webDriver.findElement(By.name("q"))
            .sendKeys(SEARCH_KEYWORD + Keys.ENTER);
        assertThat(
            webDriver.findElements(By.xpath("//a/h3"))
                .stream()
                .map(webElement -> webElement.getAttribute("textContent"))
                .peek(resultTitle -> log.debug("found {}", resultTitle)),
            allMatch(containsStringIgnoringCase(SEARCH_KEYWORD)));
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
        WebDriverManager.chromedriver().setup();
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
        webDriver.get("https://google.com");
    }
}
