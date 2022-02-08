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

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;

import java.util.*;

import javax.annotation.concurrent.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.reflect.*;

import lombok.*;
import lombok.experimental.*;

/**
 * Object utilities.
 * 
 * @author aherscu
 *
 */
@UtilityClass
@ThreadSafe
public final class ObjectUtilsExtensions extends ObjectUtils {

    /**
     * @param values
     *            some values
     * @return null if all values passed are null, otherwise the first non-null
     *         value amongst the passed values
     */
    @SafeVarargs
    public static <T> T defaultIfNull(final T... values) {
        return defaultIfNull(null, values);
    }

    /**
     * @param defaultValue
     *            the default value to return if all the passed values are null
     * @param values
     *            the values
     * @return the default value if all passed values are null, otherwise the
     *         first non-null value amongst the passed values
     */
    @SafeVarargs
    public static <T> T defaultIfNull(final T defaultValue, final T... values) {
        return org.apache.commons.lang3.ObjectUtils.defaultIfNull(
            org.apache.commons.lang3.ObjectUtils.firstNonNull(values),
            defaultValue);
    }

    /**
     * Checks the given object is not null
     *
     * @param obj
     *            the object the check
     * @param message
     *            the message to add to thrown exception
     * @return the object passed in if not null
     * @throws IllegalStateException
     *             if the object is null
     */
    public static <T> T requireNonNull(final T obj, final String message) {
        return requireNonNull(obj, new IllegalStateException(message));
    }

    /**
     * Checks the given object is not null
     *
     * @param obj
     *            the object the check
     * @param e
     *            the exception to throw if the passed object is null
     * @return the object passed in if not null
     * @throws RuntimeException
     *             if the object is null
     */
    public static <T> T requireNonNull(final T obj, final RuntimeException e) {
        if (null == obj)
            throw e;
        return obj;
    }

    /**
     * Returns a string representation of specified object, while keeping the
     * length of its fields to the specified length.
     * 
     * @param obj
     *            the object from which to extract the string
     * @param separator
     *            separator string to use between each field
     * @param length
     *            the allowed length of each field
     * @return list of fields with their text values
     */
    public static String toString(
        final Object obj,
        final String separator,
        final int length) {
        final Map<String, String> fields = new HashMap<>();
        try {
            for (val field : FieldUtils.getAllFieldsList(obj.getClass())) {
                fields.put(field.getName(),
                    abbreviateMiddle(
                        Objects.toString(field.get(obj)),
                        ELLIPSIS,
                        length));
            }
        } catch (final IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return join(fields, separator);
    }
}
