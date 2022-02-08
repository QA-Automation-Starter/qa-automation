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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

public class _3_TestingWebApplication {

    private WebDriver webDriver;

    @Test
    public void shouldOpenWeb() {
        webDriver
            .get("https://google.com");

        assertThat(webDriver.getTitle(), equalTo("Google"));
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
        // NOTE: ensure you have matching ChromeDriver from
        // https://chromedriver.chromium.org/downloads
        // and added it to system path
        webDriver = new ChromeDriver();

        Thread.sleep(10_000); // just to re-position the window

        // NOTE: should uncomment in order to deal with latencies
        // webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
    }
}
