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
package dev.aherscu.qa.tester.utils.config;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.io.*;
import org.apache.commons.text.*;

import lombok.*;
import lombok.experimental.Delegate;

/**
 * Makes a {@link Configuration} look like a {@link Map}.
 *
 * @author aherscu
 *
 * @param <T>
 *            type of wrapped configuration
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract class AbstractConfiguration<T extends Configuration>
    implements Configuration, Map<Object, Object> {

    /**
     * The configuration sources.
     */
    public static final String CONFIGURATION_SOURCES =
        "configuration-sources.xml"; //$NON-NLS-1$

    static {
        // IMPORTANT: otherwise it will try to parse strings containing commas
        // into lists
        org.apache.commons.configuration.AbstractConfiguration
            .setDefaultListDelimiter((char) 0);
    }

    @Delegate(types = { Configuration.class })
    protected final T wrappedConfiguration;

    /**
     * Builds a configuration.
     *
     * @param wrappedConfiguration
     *            the configuration to wrap
     */
    protected AbstractConfiguration(final T wrappedConfiguration) {
        this.wrappedConfiguration = wrappedConfiguration;

        if (wrappedConfiguration instanceof org.apache.commons.configuration.AbstractConfiguration) {
            ((org.apache.commons.configuration.AbstractConfiguration) wrappedConfiguration)
                .setThrowExceptionOnMissing(true);
        }
    }

    /**
     * @return configuration per {@link #CONFIGURATION_SOURCES}
     * @throws ConfigurationException
     *             if an error occurs
     */
    public static Configuration defaultConfiguration()
        throws ConfigurationException {
        return new DefaultConfigurationBuilder(CONFIGURATION_SOURCES)
            .getConfiguration();
    }

    /**
     * Resolves {@link AbstractConfiguration} references in a string.
     *
     * @param value
     *            the value to process after {@link Object#toString()} is called
     * @return the supplied value with all its <code>${...}</code> references
     *         resolved
     */
    public String resolve(final Object value) {
        return StringSubstitutor.replace(value,
            ConfigurationConverter.getProperties(this));
    }

    /**
     * Reads string contents of referenced resource.
     *
     * @param path
     *            the path from root of classes
     * @return the string contents
     * @throws NullPointerException
     *             if the resource could not be found or not enough privileges
     *             to access
     */
    @SneakyThrows(IOException.class)
    protected String stringResourceFrom(final String path) {
        return IOUtils
            .toString(
                Objects.requireNonNull(getClass()
                    .getClassLoader()
                    .getResource(path),
                    "cannot find or access resource"),
                StandardCharsets.UTF_8.toString());
    }

    @Delegate(excludes = MapExcludedMethodsForDelegation.class)
    private Map<Object, Object> getAsProperties() {
        return ConfigurationConverter.getProperties(wrappedConfiguration);
    }

    private interface MapExcludedMethodsForDelegation {
        void clear();

        boolean isEmpty();
    }

}
