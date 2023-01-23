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
package dev.aherscu.qa.tester.utils.config;

import static com.google.common.collect.Maps.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Map.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.configuration2.*;

import lombok.extern.slf4j.*;

/**
 * Represents a configuration.
 *
 * @author aherscu
 *
 */
@Slf4j
public class BaseConfiguration
    extends AbstractConfiguration<CompositeConfiguration> {

    /**
     * Loads the specified configurations.
     * 
     * @param configurations
     *            the additional configurations; might be null or empty
     */
    public BaseConfiguration(final Configuration... configurations) {
        super(new CompositeConfiguration(Arrays.asList(configurations)));
        log.trace("configuration loaded {}", toString(CR + LF));
    }

    /**
     * Given following properties:
     *
     * <pre>
     * capability1.class           =ANDROID
     * capability1.device.name     =Samsung Galaxy S9 Plus WQHD
     * capability1.platform.version=8.0
     * capability1.appium.version  =1.13.0
     *
     * capability2.class           =ANDROID
     * capability2.device.name     =Samsung Galaxy S9 Plus HD
     * capability2.platform.version=8.1
     * capability2.appium.version  =1.13.0
     * </pre>
     *
     * calling <tt>groupsOf("capability")</tt> will yield two groups as
     * following:
     *
     * <pre>
     * class           =ANDROID
     * device.name     =Samsung Galaxy S9 Plus WQHD
     * platform.version=8.0
     * appium.version  =1.13.0
     * </pre>
     *
     * <pre>
     * class           =ANDROID
     * device.name     =Samsung Galaxy S9 Plus HD
     * platform.version=8.1
     * appium.version  =1.13.0
     * </pre>
     *
     * @param prefix
     *            the prefix of a group of properties
     * @return groups of properties, where each group is identified by the
     *         characters following the prefix up until first dot
     */
    public Stream<Map<String, String>> groupsOf(final String prefix) {
        return entrySet()
            .stream()
            .map(entry -> immutableEntry(
                entry.getKey().toString(),
                entry.getValue().toString()))
            .filter(entry -> entry.getKey().startsWith(prefix))
            .collect(groupingBy(
                entry -> substringBetween(entry.getKey(), prefix, DOT)))
            .values()
            .stream()
            .map(groupedEntries -> groupedEntries
                .stream()
                .collect(toMap(
                    groupedEntry -> substringAfter(
                        substringAfter(groupedEntry.getKey(), prefix), DOT),
                    Entry::getValue)));
    }

    /**
     * @param delimiter
     *            character(s) to delimit between entries
     * @return string representation of all entries, where each entry is
     *         abbreviated to 127 characters.
     */
    public String toString(final CharSequence delimiter) {
        return entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .map(s -> abbreviate(s, 127))
            .sorted()
            .collect(joining(delimiter));
    }

    @Override
    public String toString() {
        return toString(SEMI);
    }
}
