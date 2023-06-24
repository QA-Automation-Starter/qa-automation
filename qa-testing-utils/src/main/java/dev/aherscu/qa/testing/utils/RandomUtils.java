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

import java.text.*;

import javax.annotation.concurrent.*;

import org.apache.commons.lang3.*;

import lombok.*;
import lombok.experimental.*;

/**
 * Random utilities.
 *
 * @author aherscu
 */
@UtilityClass
@ThreadSafe
public class RandomUtils {

    /**
     * The default random string length.
     */
    public static final int                  DEFAULT_RANDOM_STRING_LENGTH = 4;

    private static final ThreadLocal<String> lastRandomString             =
        new ThreadLocal<>();

    /**
     * Fills a provided template with the last random value generated on current
     * thread.
     *
     * @param template
     *            string template into which to insert the last random value;
     *            see {@link MessageFormat}
     * @return the template filled with the last random value, or
     *         {@link StringUtils#EMPTY} if no such value exists
     */
    public static String lastRandomAlphaNumeric(final String template) {
        return MessageFormat.format(template,
            StringUtils.defaultString(
                lastRandomString.get()));
    }

    /**
     * Fills a provided template with a random value such that the entire length
     * of the returned string is as specified.
     *
     * @param template
     *            string template into which to insert a random value; see
     *            {@link MessageFormat}
     * @param length
     *            the desired length of the returned string
     * @return the template filled with a
     *         {@link RandomStringUtils#randomAlphanumeric(int)}
     * @throws IllegalArgumentException
     *             if the desired length is less that the length of the template
     *             filled with an empty string
     */
    public static String padRandomAlphaNumeric(
        final String template,
        final int length) {
        val templateLength = MessageFormat
            .format(template, StringUtils.EMPTY)
            .length();
        return randomAlphaNumeric(template, length - templateLength);
    }

    /**
     * Fills a provided template with a random value.
     *
     * @param template
     *            string template into which to insert a random value; see
     *            {@link MessageFormat}
     * @return the template filled with a
     *         {@link RandomStringUtils#randomAlphanumeric(int)}
     */
    public static String randomAlphaNumeric(final String template) {
        return randomAlphaNumeric(template, DEFAULT_RANDOM_STRING_LENGTH);
    }

    /**
     * Fills a provided template with a random value.
     *
     * @param template
     *            string template into which to insert a random value; see
     *            {@link MessageFormat}
     * @param randomStringLength
     *            the random string length
     * @return the template filled with a
     *         {@link RandomStringUtils#randomAlphanumeric(int)}
     * @throws IllegalArgumentException
     *             if the randomStringLength is negative
     */
    public static String randomAlphaNumeric(
        final String template,
        final int randomStringLength) {
        val randomAlphanumeric = RandomStringUtils
            .randomAlphanumeric(randomStringLength);
        lastRandomString.set(randomAlphanumeric);
        return MessageFormat.format(template, randomAlphanumeric);
    }
}
