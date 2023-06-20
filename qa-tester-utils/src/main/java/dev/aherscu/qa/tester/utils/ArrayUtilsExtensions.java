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
package dev.aherscu.qa.tester.utils;

import java.util.*;

import org.apache.commons.lang3.*;

import lombok.experimental.*;

/**
 * Array utilities.
 *
 * @author aherscu
 *
 */
@UtilityClass
public class ArrayUtilsExtensions extends ArrayUtils {

    /**
     * @param array
     *            an array
     * @param defaultValue
     *            a default value
     * @param <T>
     *            type of array
     * @return the default value if the array is empty or null, otherwise the
     *         value at index 0
     */
    public static <T> T defaultIfEmpty(
        final T[] array,
        final T defaultValue) {
        return isEmpty(array)
            ? defaultValue
            : array[0];
    }

    /**
     * @param list
     *            array list
     * @param defaultValue
     *            a default value
     * @param <T>
     *            type of array
     * @return the default value if the list is empty or null, otherwise the
     *         value at index 0
     */
    @SuppressWarnings("unchecked")
    public static <T> T defaultIfEmptyList(
        final List<T> list,
        final T defaultValue) {
        return defaultIfEmpty((T[]) list.toArray(),
            defaultValue);
    }

}
