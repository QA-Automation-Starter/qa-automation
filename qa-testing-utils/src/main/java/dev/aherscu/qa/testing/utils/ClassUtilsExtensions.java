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

package dev.aherscu.qa.testing.utils;

import static dev.aherscu.qa.testing.utils.ObjectUtilsExtensions.*;

import java.io.*;

import org.apache.commons.lang3.*;

import lombok.experimental.*;

@UtilityClass
public class ClassUtilsExtensions extends ClassUtils {
    /**
     * Retrieves a resource relative to specified class.
     *
     * @param clazz
     *            the class from which the relative look-up should work
     * @param resourceName
     *            the resource name to look-up for
     * @return the resource as an input stream
     * @throws IllegalArgumentException
     *             if the resource was not found
     */
    public static InputStream getRelativeResourceAsStream(
        final Class<?> clazz,
        final String resourceName) {
        return requireNonNull(clazz.getResourceAsStream(resourceName),
            new IllegalArgumentException(resourceName + " file not found"));
    }
}
