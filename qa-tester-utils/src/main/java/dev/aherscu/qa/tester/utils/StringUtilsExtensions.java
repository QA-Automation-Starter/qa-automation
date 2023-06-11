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

import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.annotation.concurrent.*;

import org.apache.commons.lang3.*;

import lombok.*;

/**
 * Provides enhanced string utilities.
 *
 * @author aherscu
 *
 */
@ThreadSafe
@SuppressWarnings("boxing")
public class StringUtilsExtensions extends StringUtils {
    /**
     * A colon.
     */
    public static final String COLON        = ":";   //$NON-NLS-1$
    /**
     * A comma.
     */
    public static final String COMMA        = ",";   //$NON-NLS-1$
    /**
     * A dash.
     */
    public static final String DASH         = "-";   //$NON-NLS-1$
    /**
     * A dot.
     */
    public static final String DOT          = ".";   //$NON-NLS-1$
    /**
     * A double quote.
     */
    public static final String DOUBLE_QUOTE = "\"";  //$NON-NLS-1$
    /**
     * An ellipsis is tree dots.
     */
    public static final String ELLIPSIS     = "..."; //$NON-NLS-1$
    /**
     * A equal.
     */
    public static final String EQUAL        = "=";   //$NON-NLS-1$
    /**
     * A single quote.
     */
    public static final String QUOTE        = "'";   //$NON-NLS-1$
    /**
     * A semi-colon.
     */
    public static final String SEMI         = ";";   //$NON-NLS-1$
    /**
     * A forward slash.
     */
    public static final String SLASH        = "/";   //$NON-NLS-1$
    public static final String UNDERSCORE   = "_";

    public static String abbreviate(final String str, final int maxWidth) {
        return StringUtils.abbreviate(str, maxWidth)
            + ifExcessiveLength(str, maxWidth);
    }

    public static String abbreviateMiddle(final String str, final String middle,
        final int length) {
        return StringUtils.abbreviateMiddle(str, middle, length)
            + ifExcessiveLength(str, length);
    }

    public static String format(
        final String template,
        final Pattern pattern,
        final String string) {
        return MessageFormat.format(template,
            groups(pattern, string).toArray());
    }

    public static String format(
        final String template,
        final String pattern,
        final String string) {
        return MessageFormat.format(template,
            groups(pattern, string).toArray());
    }

    /**
     * Splits a string according to specified regular expression groups.
     * {@code groups("(\\d+)(\\w+)", "10FF 99GG")} will return four items: "10",
     * "FF", "99", "GG"
     *
     * @param pattern
     *            compiled regular expression to apply; must contain groups
     * @param string
     *            the string to split
     * @return split string
     */
    // TODO make a streaming variant
    public static Collection<String> groups(
        final Pattern pattern,
        final String string) {
        val allMatches = new LinkedList<String>();
        val matcher = pattern.matcher(string);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++)
                allMatches.add(matcher.group(i));
        }
        return allMatches;
    }

    public static Collection<String> groups(
        final String pattern,
        final String string) {
        return groups(Pattern.compile(pattern), string);
    }

    /**
     * If given string has excessive length return its length.
     *
     * @param str
     *            the string to be processed
     * @param allowedLength
     *            the allowed length
     * @return the length of the string as formatted by {@link #length(String)}
     */
    public static String ifExcessiveLength(
        final String str,
        final int allowedLength) {
        return str.length() > allowedLength ? length(str) : EMPTY;
    }

    public static String left(final String str, final int length) {
        return StringUtils.left(str, length)
            + ifExcessiveLength(str, length);
    }

    /**
     * Formats the length of given string.
     *
     * @param str
     *            the string for which to return the length
     * @return the length of the string formatted as
     *         <code>(<i>ddd</i> chars)</code>, where <i>ddd</i> is the actual
     *         length
     */
    public static String length(final String str) {
        return length(str, "(%d chars)"); //$NON-NLS-1$
    }

    /**
     * Formats the length of given string.
     *
     * @param str
     *            the string for which to return the length
     * @param format
     *            the template to use for formatting; see
     *            {@link String#format(String, Object...)}
     * @return the length of the string formatted as
     *         <code>(<i>ddd</i> chars)</code>, where <i>ddd</i> is the actual
     *         length
     */
    public static String length(final String str, final String format) {
        return String.format(format, str.length());
    }

    /**
     * Returns the prettified version of an object.
     *
     * @param object
     *            the object
     * @return {@code <<null>>} for {@code null}, {@code <<empty>>} for object
     *         with length {@code 0}, and {@code <<blank>>} for object
     *         containing only whitespace characters according to
     *         {@link StringUtils#isBlank(CharSequence)}
     */
    public static String prettified(final Object object) {
        return null == object
            ? "<<null>>"
            : isEmpty(String.valueOf(object))
                ? "<<empty>>"
                : isBlank(String.valueOf(object))
                    ? "<<blank>>"
                    : String.valueOf(object);
    }

    /**
     * Extracts a substring by specified regular expression.
     *
     * @param s
     *            the string to extract from
     * @param regex
     *            the regular expression
     * @return substring extracted; empty if no match could be found
     */
    public static String substring(final String s, final Pattern regex) {
        val matcher = regex.matcher(s);
        return matcher.find() ? matcher.group() : EMPTY;
    }

    /**
     * Converts Windows end-of-line to *nix end-of-line.
     *
     * @param str
     *            the string to convert
     * @return the converted string
     */
    public static String toUnix(final String str) {
        return replace(str, CR + LF, LF);
    }
}
