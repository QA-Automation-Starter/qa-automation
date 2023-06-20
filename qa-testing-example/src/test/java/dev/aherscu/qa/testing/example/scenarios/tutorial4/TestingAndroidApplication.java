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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.*;
import java.net.*;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import io.appium.java_client.android.*;
import lombok.*;

/**
 * TODO should run against one of the Android applications, e.g. Calculator
 */
public class TestingAndroidApplication {
    private WebDriver webDriver;

    @java.lang.SuppressWarnings("serial")
    @SneakyThrows
    static AndroidDriver<WebElement> localApp() {
        return new AndroidDriver<>(
            new URL("http://127.0.0.1:4723/wd/hub"),
            new DesiredCapabilities() {
                {
                    setCapability(PLATFORM_NAME, ANDROID);
                    setCapability(DEVICE_NAME, "DONTCARE"); // local
                    setCapability(AUTO_WEBVIEW, true);
                    setCapability(APP,
                        new File(System.getProperty("user.dir"), "app.apk")
                            .toString());
                }
            });
    }

    @Test
    public void shouldRequireEmail() {
        // ThreadUtils.sleep(4000); -- otherwise will test before stabilization
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
    private void beforeClassOpenWebDriver() {
        // NOTE: ensure you have local Appium server started and Android
        // emulator running and available via adb devices
        // -- see README.md for details
        webDriver = localApp();
    }
}
