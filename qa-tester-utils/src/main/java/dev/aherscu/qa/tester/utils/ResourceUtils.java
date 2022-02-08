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
package dev.aherscu.qa.tester.utils;

import java.util.*;

import com.github.rodionmoiseev.c10n.*;

import lombok.experimental.*;

/**
 * Resource utilities.
 *
 * @author aherscu
 *
 */
@UtilityClass
public class ResourceUtils {
    /**
     * Retrieves the resource associated with current locale.
     *
     * @param c10nInterface
     *            the resource interface
     * @return the resource
     */
    public static <T> T get(final Class<T> c10nInterface) {
        return C10N.get(c10nInterface);
    }

    /**
     * Retrieves the resource associated with {@link Locale#ROOT}
     *
     * @param c10nInterface
     *            the resource interface
     * @return the resource
     */
    public static <T> T getRootFor(final Class<T> c10nInterface) {
        return C10N.get(c10nInterface, Locale.ROOT);
    }
}
