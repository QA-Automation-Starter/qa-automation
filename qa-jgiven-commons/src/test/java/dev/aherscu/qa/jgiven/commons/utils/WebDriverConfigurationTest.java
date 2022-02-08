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

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.jgiven.commons.utils.WebDriverConfiguration.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.Platform.*;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.configuration.*;
import org.openqa.selenium.Platform;
import org.testng.annotations.*;

import com.google.common.collect.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class WebDriverConfigurationTest {
    private final WebDriverConfiguration configuration;
    private final ExpectedCapabilities   expectedCapabilities;

    @Factory(dataProvider = "runtimeConfigurations")
    public WebDriverConfigurationTest(
        final Configuration runtimeConfiguration,
        final ExpectedCapabilities expectedCapabilities)
        throws ConfigurationException {
        // NOTE: must rename thread in order to avoid SessionName warnings
        Thread.currentThread().setName("a:b:c:d:e");
        log.info("initializing {}", expectedCapabilities);
        this.configuration = new WebDriverConfiguration(
            defaultConfiguration(),
            runtimeConfiguration);
        this.expectedCapabilities = expectedCapabilities;
    }

    // NOTE: configuration instances share a global capabilities index
    // hence must not be tested in parallel, otherwise this index is unexpected
    @java.lang.SuppressWarnings("DefaultAnnotationParam")
    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @DataProvider(parallel = false)
    private static Object[][] runtimeConfigurations() {
        return new Object[][] {
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.local.")
                .put("device.type", "ios")
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(IOS)
                    .deviceNames(singletonList("iPhone 12 Pro"))
                    .provider("provider.local.")
                    .matchingCapabilities(1)
                    .build() },
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.saucelabs.")
                .put("device.type", "android")
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(ANDROID)
                    .deviceNames(asList(
                        "Google Pixel 3a XL GoogleAPI Emulator",
                        "Google Pixel 3 XL GoogleAPI Emulator",
                        "Google Pixel 3 GoogleAPI Emulator"))
                    .provider("provider.saucelabs.")
                    .matchingCapabilities(3)
                    .build() },
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.saucelabs.")
                .put("device.type", "any")
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(ANY)
                    .deviceNames(asList(
                        "Google Pixel 3a XL GoogleAPI Emulator",
                        "iPhone 12 Pro",
                        "Google Pixel 3 XL GoogleAPI Emulator",
                        "Google Pixel 3 GoogleAPI Emulator"))
                    .provider("provider.saucelabs.")
                    .matchingCapabilities(4)
                    .build() }
        };
    }

    // NOTE: the TestNG Factory mechanism creates all instances at once,
    // hence we still need to reset the next capabilities index just before
    // tests methods begin to execute in each instance
    @BeforeClass
    public void beforeClassResetNextCapabilitiesIndex() {
        log.debug("resetting next capabilities index from {}",
            resetNextRequiredCapabilitiesIndex());
    }

    @DataProvider
    public Iterator<Object[]> expectedDevices() {
        return Stream.concat(

            expectedCapabilities.deviceNames
                .stream()
                .map(deviceName -> new Object[] {
                    expectedCapabilities.deviceType,
                    deviceName }),

            Stream.<Object[]> of(
                new Object[] {
                    expectedCapabilities.deviceType,
                    expectedCapabilities.deviceNames.get(0) }))

            .peek(o -> log.debug("expecting device name {}", o[0]))
            .iterator();
    }

    @Test
    public void shouldHaveDeviceType() {
        assertThat(configuration.deviceType(),
            is(expectedCapabilities.deviceType));
    }

    // TODO must test how next required capabilities index behaves across
    // instances

    @Test
    public void shouldHaveProvider() {
        assertThat(configuration.provider(),
            is(expectedCapabilities.provider));
    }

    @Test
    public void shouldHaveRequiredNumberOfCapabilities() {
        assertThat(configuration.requiredCapabilities(),
            hasSize(expectedCapabilities.matchingCapabilities));
    }

    // NOTE: configuration instances share a global capabilities index
    // hence this test must run first, otherwise this index is unexpected
    @Test(priority = -1, dataProvider = "expectedDevices")
    public void shouldLoopOverCapabilities(
        final Platform deviceType,
        final String deviceName) {
        assertThat(configuration
            .capabilities()
            .getCapability("deviceName"),
            is(deviceName));
    }

    @Test
    public void shouldRetrieveCapabilitiesBySpecificPrefix() {
        assertThat(configuration
            .capabilitiesFor("provider.saucelabs.simulator")
            .getCapability("class"),
            is("org.openqa.selenium.remote.RemoteWebDriver"));
    }

    @Test
    public void shouldRetrieveCapabilitiesForSpecificPlatform() {
        assertThat(configuration
            .capabilities(ANDROID)
            .getCapability("class"),
            is("io.appium.java_client.android.AndroidDriver"));
    }

    @Builder
    @ToString
    private static class ExpectedCapabilities {
        final Platform     deviceType;
        final String       provider;
        final List<String> deviceNames;
        final int          matchingCapabilities;
    }
}
