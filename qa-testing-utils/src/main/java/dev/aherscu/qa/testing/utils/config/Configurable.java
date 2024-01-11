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
package dev.aherscu.qa.testing.utils.config;

/**
 * Base class for all things that use {@link AbstractConfiguration}.
 *
 * @author aherscu
 *
 * @param <T>
 *            configuration type
 */
public class Configurable<T extends AbstractConfiguration<?>> {
    /**
     * The configuration to use.
     */
    protected final T configuration;

    /**
     * Sets up configuration.
     *
     * @param configuration
     *            the configuration; if null, operations requiring configuration
     *            will fail
     */
    protected Configurable(final T configuration) {
        this.configuration = configuration;
    }

    /**
     * Resolves a value according to configured definitions.
     *
     * @param value
     *            the value to resolve definitions references within
     * @return the resolved value
     * @throws NullPointerException
     *             if there is no configuration
     */
    protected String resolved(final String value) {
        return configuration.resolve(value);
    }
}
