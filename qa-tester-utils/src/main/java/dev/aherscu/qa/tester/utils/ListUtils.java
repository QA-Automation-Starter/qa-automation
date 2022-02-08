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

import lombok.experimental.*;

/**
 * List utilities.
 *
 * @author Adrian Herscu
 *
 */
@UtilityClass
public class ListUtils {
    /**
     * @param list
     *            the list to retrieve from
     * @param index
     *            the zero-based index to retrieve
     * @param defaultValue
     *            the default value if there is no value for specified index
     * @param <T>
     *            type of values in specified list
     * @return the value at specified index or the default value
     */
    public static <T> T getOrElse(
        final List<T> list,
        final int index,
        final T defaultValue) {
        return index < list.size()
            ? list.get(index)
            : defaultValue;
    }
}
