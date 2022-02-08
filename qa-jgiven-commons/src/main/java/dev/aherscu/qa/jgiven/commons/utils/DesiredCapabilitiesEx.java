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

import static java.util.Objects.*;

import java.lang.SuppressWarnings;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Allows definition of {@link DesiredCapabilities} to be chained.
 */
@SuppressWarnings("serial")
@SuppressFBWarnings("SE_NO_SERIALVERSIONID")
public class DesiredCapabilitiesEx extends DesiredCapabilities {

    /**
     * Begins chaining with no capabilities.
     */
    public DesiredCapabilitiesEx() {
        super();
    }

    /**
     * Begins chaining with specified capabilities.
     * 
     * @param capabilities
     *            the capabilities to chain on
     */
    public DesiredCapabilitiesEx(final Capabilities capabilities) {
        super(capabilities);
    }

    /**
     * Chains a <tt>true</tt> capability.
     * 
     * @param key
     *            the capability to chain
     * @return chained capabilities, on new set of capabilities
     */
    public DesiredCapabilitiesEx with(final String key) {
        return with(key, true);
    }

    /**
     * Chains a capability.
     *
     * @param key
     *            the capability to chain
     * @param value
     *            the value
     * @return chained capabilities, on new set of capabilities
     */
    public DesiredCapabilitiesEx with(
        final String key,
        final Object value) {
        val ammendedCapabilities = new DesiredCapabilitiesEx(this);
        ammendedCapabilities.setCapability(key, value);
        return ammendedCapabilities;
    }

    /**
     * Appends on existing capability. If no such capability exists it will be
     * created.
     *
     * @param key
     *            the capability to append on
     * @param value
     *            value to append
     * @return chained capabilities, on new set of capabilities
     * @throws NullPointerException
     *             if capability does not exist
     */
    public DesiredCapabilitiesEx withAdded(
        final String key,
        final Object value) {
        return with(key,
            requireNonNull(getCapability(key),
                "capability does not exist")
                    .toString()
                + value);
    }
}
