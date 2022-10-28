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

package dev.aherscu.qa.testing.example.scenarios.tutorial5;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.net.*;

import org.openqa.selenium.remote.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import io.appium.java_client.windows.*;
import lombok.*;

public class TestingWindowsApplication {

    private WindowsDriver<?> driver;

    @Test
    public void shouldOpenCalculator() {
        assertThat(driver.getTitle(), equalTo("Calculator"));
    }

    @Test
    public void shouldCalculate() {
        driver.findElementByAccessibilityId("CalculatorResults")
            .sendKeys("8+7=");
        assertThat(driver
            .findElementByAccessibilityId("CalculatorResults")
            .getText(),
            stringContainsInOrder("Display is", "15"));
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @AfterClass(alwaysRun = true) // important, otherwise we may leak resources
    private void afterClassCloseWebDriver() {
        driver.quit();
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @BeforeClass
    @SneakyThrows
    private void beforeClassOpenWebDriver() {
        // NOTE: ensure you have Appium-compatible Windows Application Driver
        // from https://github.com/Microsoft/WinAppDriver/releases
        val capabilities = new DesiredCapabilities();
        capabilities.setCapability("app",
            "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App");
        driver = new WindowsDriver<>(new URL("http://127.0.0.1:4723"),
            capabilities);

        // NOTE: should uncomment in order to deal with latencies
        // webDriver.manage().timeouts().implicitlyWait(10, SECONDS);
    }
}
