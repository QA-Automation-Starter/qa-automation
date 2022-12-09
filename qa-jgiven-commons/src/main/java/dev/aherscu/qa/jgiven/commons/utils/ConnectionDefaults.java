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

import static java.lang.Integer.*;

/**
 * Defines connection defaults.
 *
 * @author aherscu
 *
 */
public class ConnectionDefaults {

    /**
     * The default connection timeout in milliseconds. Initialized from
     * {@code jgiven.commons.connection.timeout} System Property or 10000ms if
     * not specified.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT =
        getInteger("jgiven.commons.connection.timeout", 10000);

    /**
     * The default execution timeout in milliseconds. Initialized from
     * {@code jgiven.commons.execution.timeout} System Property or 0ms if not
     * specified.
     */
    public static final int DEFAULT_EXECUTION_TIMEOUT  =
        getInteger("jgiven.commons.execution.timeout", 0);

    /**
     * The default read timeout in milliseconds. Initialized from
     * {@code jgiven.commons.read.timeout} System Property or 30000ms if not
     * specified.
     */
    public static final int DEFAULT_READ_TIMEOUT       =
        getInteger("jgiven.commons.read.timeout", 30000);
}
