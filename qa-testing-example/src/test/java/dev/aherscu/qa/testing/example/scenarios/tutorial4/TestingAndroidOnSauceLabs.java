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

package dev.aherscu.qa.testing.example.scenarios.tutorial4;

import static io.appium.java_client.remote.MobileCapabilityType.*;
import static io.appium.java_client.remote.MobilePlatform.*;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.net.*;
import java.time.*;
import java.time.format.*;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import io.appium.java_client.android.*;
import lombok.*;

/**
 * TODO should run against one of the Android applications, e.g. Calculator
 */
public class TestingAndroidOnSauceLabs {

    private WebDriver webDriver;

    @java.lang.SuppressWarnings("serial")
    @SneakyThrows
    static AndroidDriver<WebElement> saucelabsApp(
        final String name) {
        return new AndroidDriver<>(new URL(
            // FIXME should get these credentials from system enviroment
            //  SAUCELABS_USERNAME and SAUCELABS_PASSWORD
            "https://TBD:TBD@ondemand.saucelabs.com:443/wd/hub"),
            new DesiredCapabilities() {
                {
                    setCapability(PLATFORM_NAME, ANDROID);
                    setCapability(PLATFORM_VERSION, "8");
                    setCapability(DEVICE_NAME,
                        "Samsung Galaxy S9 Plus WQHD GoogleAPI Emulator");
                    setCapability(AUTO_WEBVIEW, true);
                    setCapability(APP, "sauce-storage:app.apk");
                    setCapability("name", name);
                    setCapability("build", "local-"
                        + DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now()));
                }
            });
    }

    @Test
    public void shouldRequireEmail() {
        assertThat(webDriver
            .findElements(By.xpath("//*[text()='Please enter valid email']")),
            not(empty()));
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
        // NOTE: ensure you have TBD.apk uploaded to SauceLabs
        // curl -u "TBD:TBD" -X POST
        // https://saucelabs.com/rest/v1/storage/TBD --data-binary
        // @TBD.apk
        webDriver = saucelabsApp(getClass().getSimpleName());
        webDriver.manage().timeouts().implicitlyWait(5, SECONDS);
    }
}
