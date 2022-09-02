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

import static com.google.common.base.Suppliers.*;
import static dev.aherscu.qa.jgiven.commons.utils.SessionName.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Collections.*;
import static java.util.Locale.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static org.openqa.selenium.Platform.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.configuration.*;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.*;

import com.google.common.collect.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.tester.utils.config.BaseConfiguration;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Provides WebDriver-related configuration items.
 */
@Slf4j
public class WebDriverConfiguration extends BaseConfiguration {
    // NOTE must be static otherwise all tests will run with same capabilities.
    // This also means that if two instances are created with different
    // devices, hence different sets of capabilities, this mechanism will break.
    private static final AtomicInteger                  nextRequiredCapabilitiesIndex =
        new AtomicInteger(0);
    private static final AtomicReference<Platform>      theDeviceType                 =
        new AtomicReference<>();
    private final Supplier<List<DesiredCapabilitiesEx>> requiredCapabilities          =
        memoize(() -> unmodifiableList(loadRequiredCapabilities()
            .peek(capabilities -> log.trace("loaded {}", capabilities))
            .collect(toList())));

    /**
     * @param configurations
     *            set of configurations to be aggregated; the `device.type`
     *            property has a filtering effect on results of
     *            {@link #requiredCapabilities()} and of {@link #capabilities()}
     *            methods, hence these methods will work correctly only if all
     *            instances are created with same `device.type`, or otherwise
     *            {@link #resetNextRequiredCapabilitiesIndex()} is called in
     *            between
     */
    public WebDriverConfiguration(final Configuration... configurations) {
        super(configurations);
        if (theDeviceType.compareAndSet(null, deviceType())) {
            log.info("initialized with {}", deviceType());
        } else {
            log.warn(
                "attempting to re-initialize with {}; resetting next required capabilities index from {}",
                deviceType(),
                resetNextRequiredCapabilitiesIndex());
        }
    }

    /**
     * Resets the index of next required set of capabilities to specified value.
     *
     * <p>
     * Useful for unit testing.
     * </p>
     *
     * @param value
     *            the new value
     *
     * @return previous index value
     */
    static int resetNextRequiredCapabilitiesIndex(final int value) {
        val oldValue = nextRequiredCapabilitiesIndex.get();
        nextRequiredCapabilitiesIndex.set(value);
        return oldValue;
    }

    /**
     * Resets the index of next required set of capabilities to zero.
     *
     * <p>
     * Useful for unit testing.
     * </p>
     *
     * @return previous index value
     */
    static int resetNextRequiredCapabilitiesIndex() {
        return resetNextRequiredCapabilitiesIndex(0);
    }

    /**
     * @return capabilities for configured {@link #provider()} and
     *         {@link #deviceType()};
     *
     *         <p>
     *         if {@link #deviceType()} is not blank then retrieves only
     *         matching capabilities from
     *         {@code required-capabilities.properties};
     *
     *         <p>
     *         if no matching capabilities found, then returns whatever is
     *         defined in {@code webdriver.properties} for current
     *         {@link #provider()}
     */
    @SneakyThrows
    public DesiredCapabilitiesEx capabilities() {
        return nextRequiredCapabilities(deviceType())
            .orElse(capabilities(deviceType()));
    }

    /**
     * @param deviceType
     *            a specific device type
     * @return matching capabilities for configured {@link #provider()} and
     *         specified device type
     */
    @SneakyThrows
    public DesiredCapabilitiesEx capabilities(final Platform deviceType) {
        return capabilitiesFor(provider()
            + deviceType.toString().toLowerCase(US));
    }

    /**
     * @return capabilities for specified prefix; also adds
     *         {@code <thread-name>:<current-time-millis>} to retrieved
     *         capabilities
     */
    @SuppressWarnings("serial")
    @SneakyThrows
    public DesiredCapabilitiesEx capabilitiesFor(final String prefix) {
        return new DesiredCapabilitiesEx() {
            {
                // NOTE: on Chrome 75+ need to turn off W3C mode
                // https://support.saucelabs.com/hc/en-us/articles/360057263354
                setCapability(ChromeOptions.CAPABILITY, ImmutableMap.builder()
                    .put("w3c", false)
                    .build());
                setCapability("name", generateFromCurrentThreadAndTime());
                entrySet()
                    .stream()
                    .map(entry -> new AbstractMap.SimpleImmutableEntry<>(
                        entry.getKey().toString(),
                        entry.getValue().toString()))
                    .filter(entry -> entry.getKey()
                        .startsWith(prefix + DOT))
                    .forEach(capabilitiesEntry -> setCapability(
                        substringAfter(capabilitiesEntry.getKey(),
                            prefix + DOT),
                        capabilitiesEntry.getValue()));
            }
        };
    }

    /**
     * @return the `device.type` as specified in source configuration, or
     *         {@link Platform#ANY} if none specified
     */
    public Platform deviceType() {
        val deviceType = getString("device.type");
        return isBlank(deviceType)
            ? ANY
            : fromString(deviceType);
    }

    /**
     * @return the `provider` as specified source configuration
     */
    public String provider() {
        return getString("provider");
    }

    /**
     * @param deviceType
     *            device type to filter by
     * @return all matching device capabilities, if any
     */
    public List<DesiredCapabilitiesEx> requiredCapabilities(
        final Platform deviceType) {
        return requiredCapabilities.get()
            .stream()
            .filter(capabilities -> requireNonNull(capabilities.getPlatform(),
                "must supply platform or platformName in configuration")
                    .is(deviceType))
            .collect(toList());
    }

    /**
     * @return device capabilities per configuration
     */
    public List<DesiredCapabilitiesEx> requiredCapabilities() {
        return requiredCapabilities(deviceType());
    }

    private Stream<DesiredCapabilitiesEx> loadRequiredCapabilities() {
        return groupsOf("required.capability")
            .map(requiredCapabilitiesGroup -> new DesiredCapabilitiesEx(
                capabilitiesFor(
                    provider() + requiredCapabilitiesGroup.get("type")))
                        .with(requiredCapabilitiesGroup
                            .entrySet()
                            .stream()
                            .filter(e -> !"type".equals(e.getKey()))
                            .map(e -> Maps.immutableEntry(
                                e.getKey(),
                                e.getValue()))));
    }

    /**
     * @param deviceType
     *            device type to filter by
     * @return next matching device capabilities as listed in
     *         <tt>required-capabilities.properties</tt>
     */
    private Optional<DesiredCapabilitiesEx> nextRequiredCapabilities(
        final Platform deviceType) {
        val capabilities = requiredCapabilities(deviceType);

        // NOTE: this method might be called from multiple tests running in
        // parallel hence must use an atomic update operation on the
        // nextAvailableDeviceCapabilities index
        return capabilities.isEmpty()
            ? Optional.empty()
            : Optional.of(capabilities
                .get(nextRequiredCapabilitiesIndex
                    .getAndUpdate(
                        currentIndex -> currentIndex < capabilities.size() - 1
                            ? currentIndex + 1
                            : 0))
                // NOTE: since the capabilities are loaded on the main thread
                // during the initialization of the test class, there is no way
                // to assign them a test (thread) name because these are not
                // running yet -- hence we do it here
                .with("name", generateFromCurrentThreadAndTime()));
    }
}
