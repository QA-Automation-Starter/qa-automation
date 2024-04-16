/*
 * Copyright 2024 Adrian Herscu
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
package dev.aherscu.qa.testing.example;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import javax.annotation.concurrent.*;

import org.apache.commons.configuration.*;

import dev.aherscu.qa.jgiven.webdriver.*;
import jakarta.ws.rs.client.*;

/**
 * Represents the configuration parameters for tests.
 *
 * @author aherscu
 */
@ThreadSafe
public final class TestConfiguration extends WebDriverConfiguration {

    static {
        // IMPORTANT: this makes all property values to be parsed as
        // list if commas are found inside
        AbstractConfiguration
            .setDefaultListDelimiter(COMMA.charAt(0));
    }

    /**
     * Loads the specified configurations.
     *
     * @param configurations
     *            the additional configurations; might be null or empty
     */
    public TestConfiguration(final Configuration... configurations) {
        super(configurations);
    }

    public WebTarget petStore(final Client client) {
        return client.target(getString("swagger.petstore.url"));
    }
}
