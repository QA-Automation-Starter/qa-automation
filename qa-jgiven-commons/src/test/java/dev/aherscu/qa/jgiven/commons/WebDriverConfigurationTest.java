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

package dev.aherscu.qa.jgiven.commons;

import static dev.aherscu.qa.jgiven.commons.WebDriverConfiguration.*;
import static dev.aherscu.qa.jgiven.commons.WebDriverConfiguration.DeviceType.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.ex.*;
import org.openqa.selenium.chrome.*;
import org.testng.annotations.*;

import com.google.common.collect.*;

import edu.umd.cs.findbugs.annotations.*;
import io.appium.java_client.android.*;
import io.appium.java_client.ios.*;
import io.appium.java_client.windows.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class WebDriverConfigurationTest {
    private final WebDriverConfiguration configuration;
    private final ExpectedCapabilities   expected;

    @Factory(dataProvider = "additionalConfigurations")
    public WebDriverConfigurationTest(
        final Configuration additionalConfigurations,
        final ExpectedCapabilities expected)
        throws ConfigurationException {
        // NOTE: must rename thread in order to avoid SessionName warnings
        Thread.currentThread().setName("a:b:c:d:e");
        log.info("initializing {}", expected);
        this.configuration = new WebDriverConfiguration(
            defaultConfiguration(),
            additionalConfigurations);
        this.expected = expected;
    }

    // NOTE: configuration instances share a global capabilities index
    // hence must not be tested in parallel, otherwise this index is unexpected
    @java.lang.SuppressWarnings("DefaultAnnotationParam")
    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @DataProvider(parallel = false)
    private static Object[][] additionalConfigurations() {
        return new Object[][] {
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.local.")
                .put("device.type", EMPTY)
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(DeviceType._WEB)
                    .deviceNames(emptyList())
                    .provider("provider.local.")
                    .matchingRequiredCapabilities(1)
                    .build() },
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.local.")
                .put("device.type", "android")
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(DeviceType._ANDROID)
                    .deviceNames(asList(
                        "Google Pixel 3a XL GoogleAPI Emulator",
                        "Google Pixel 3 XL GoogleAPI Emulator",
                        "Google Pixel 3 GoogleAPI Emulator"))
                    .provider("provider.local.")
                    .matchingRequiredCapabilities(3)
                    .build() },
            { new MapConfiguration(ImmutableMap.<String, String> builder()
                .put("provider", "provider.local.")
                .put("device.type", "windows")
                .build()),
                ExpectedCapabilities.builder()
                    .deviceType(DeviceType._WINDOWS)
                    .deviceNames(singletonList("WindowsPC"))
                    .provider("provider.local.")
                    .matchingRequiredCapabilities(0)
                    .build() },
        };
    }

    // NOTE: the TestNG Factory mechanism creates all instances at once,
    // hence we still need to reset the next capabilities index just before
    // tests methods begin to execute in each instance
    @BeforeClass
    protected void beforeClassResetNextCapabilitiesIndex() {
        log.debug("resetting next capabilities index from {}",
            resetNextRequiredCapabilitiesIndex());
    }

    @DataProvider
    private Iterator<Object[]> expectedDevices() {
        return expected.deviceNames
            .stream()
            .map(deviceName -> new Object[] {
                expected.deviceType,
                deviceName })
            .peek(o -> log.debug("expecting device name {}", o[0]))
            .iterator();
    }

    @Test
    public void shouldHaveDeviceType() {
        assertThat(configuration.deviceType(),
            is(expected.deviceType));
    }

    // TODO must test how next required capabilities index behaves across
    // instances

    @Test
    public void shouldHaveProvider() {
        assertThat(configuration.provider(),
            is(expected.provider));
    }

    /**
     * Required capabilities as defined in required-capabilities.properties
     */
    @Test
    public void shouldHaveRequiredNumberOfCapabilities() {
        assertThat(configuration.requiredCapabilities(),
            hasSize(expected.matchingRequiredCapabilities));
    }

    // NOTE: configuration instances share a global capabilities index
    // hence this test must run first, otherwise this index is unexpected
    @Test(priority = -1, dataProvider = "expectedDevices")
    public void shouldLoopOverCapabilities(
        final DeviceType deviceType,
        final String deviceName) {
        assertThat(configuration
            .capabilities()
            .getCapability("deviceName"),
            is(deviceName));
    }

    @Test
    public void shouldRetrieveCapabilitiesBySpecificPrefix() {
        assertThat(configuration
            .capabilitiesFor("provider.local.android")
            .getCapability("class"),
            is("io.appium.java_client.android.AndroidDriver"));
    }

    @Test(dataProvider = "capabilitiesPerPlatform")
    public void shouldRetrieveCapabilitiesForSpecificPlatform(
        final WebDriverConfiguration.DeviceType deviceType,
        final Class<?> clazz) {
        assertThat(configuration
            .capabilities(deviceType)
            .getCapability("class"),
            is(clazz.getName()));
    }

    @DataProvider
    private Object[][] capabilitiesPerPlatform() {
        return new Object[][] {
            { _ANDROID, AndroidDriver.class },
            { _WINDOWS, WindowsDriver.class },
            { _IOS, IOSDriver.class },
            { _WEB, ChromeDriver.class }
        };
    }

    @Builder
    @ToString
    private static class ExpectedCapabilities {
        final DeviceType   deviceType;
        final String       provider;
        final List<String> deviceNames;
        final int          matchingRequiredCapabilities;
    }
}
