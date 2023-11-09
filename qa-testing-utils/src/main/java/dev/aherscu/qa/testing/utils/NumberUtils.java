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

import static dev.aherscu.qa.testing.utils.ArrayUtilsExtensions.*;
import static java.lang.Integer.*;
import static java.lang.Long.*;

import java.lang.reflect.*;

import lombok.experimental.*;

/**
 * Number utilities.
 *
 * @author aherscu
 *
 */
@UtilityClass
public final class NumberUtils {

    /**
     * Tries to call {@code valueOf(String)} on specified numeric type with
     * specified text.
     *
     * <p>
     * NOTE: There is no generic method for parsing text into any given
     * {@link Number} subtype; this method will work for part of these subtypes,
     * for others will just throw an {@link UnsupportedOperationException}:
     * </p>
     * <ul>
     * <li>{@link Byte#valueOf(String)}</li>
     * <li>{@link Short#valueOf(String)}</li>
     * <li>{@link Integer#valueOf(String)}</li>
     * <li>{@link Long#valueOf(String)}</li>
     * <li>{@link Float#valueOf(String)}</li>
     * <li>{@link Double#valueOf(String)}</li>
     * </ul>
     *
     * @param text
     *            the text containing numeric value
     * @param expectedNumericType
     *            the expected numeric type; optional, assuming {@link Double}
     *            if not provided
     * @return the numeric value
     * @throws UnsupportedOperationException
     *             if failed to call the valueOf method on expected number type
     */
    @SafeVarargs
    public static Number numericValueOf(
        final String text,
        final Class<? extends Number>... expectedNumericType) {
        try {
            return (Number) defaultIfEmpty(expectedNumericType, Double.class)
                .getMethod("valueOf", String.class) //$NON-NLS-1$
                .invoke(null, text);
        } catch (final IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Safely parses specified integer.
     *
     * @param value
     *            integer value as string
     * @return the integer value if parsed successfully, otherwise 0
     */
    public static int parseIntOrZero(final String value) {
        try {
            return parseInt(value);
        } catch (@SuppressWarnings("unused") final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Safely parses specified long.
     *
     * @param value
     *            long value as string
     * @return the long value if parsed successfully, otherwise 0
     */
    public static long parseLongOrZero(final String value) {
        try {
            return parseLong(value);
        } catch (@SuppressWarnings("unused") final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns a double even if no number was supplied.
     *
     * @param number
     *            the number
     * @return the double representation of the supplied number or
     *         {@link Double#NaN} if no number was supplied.
     */
    public static double safeDoubleOf(final Number number) {
        return null != number ? number.doubleValue() : Double.NaN;
    }
}
