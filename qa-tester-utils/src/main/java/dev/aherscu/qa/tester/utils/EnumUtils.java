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

import static java.text.MessageFormat.*;

import javax.validation.constraints.*;

import org.apache.commons.lang3.*;
import org.apache.commons.text.*;

import lombok.experimental.*;

/**
 * Enumeration utilities.
 * 
 * @author aherscu
 *
 */
@UtilityClass
public final class EnumUtils {

    /**
     * Maps a name to an enumeration member; uses {@link #DEFAULT_ENUM_PREFIX}
     * as a prefix.
     *
     * @param enumType
     *            type of enumeration, not null
     * @param name
     *            the member name, null returns null
     * @return the referenced member
     * @throws NoSuchMemberException
     *             if the member was not found
     * @see #fromString(Class, String, Separator)
     */
    public static <E extends Enum<E>> E fromString(
        final Class<E> enumType, final String name) {
        return fromString(enumType, name, DEFAULT_ENUM_PREFIX);
    }

    /**
     * Default prefix to use for mapping members to their textual representation
     * and vice-versa. This prefix is required in order to allow mapping member
     * names beginning with a digit.
     */
    public static final Separator DEFAULT_ENUM_PREFIX    = Separator.UNDERSCORE;
    /**
     * Default word separator to use for prettifying.
     */
    public static final Separator DEFAULT_ENUM_SEPARATOR = Separator.UNDERSCORE;

    /**
     * Maps a name to an enumeration member.
     *
     * @param enumType
     *            type of enumeration, not null
     * @param name
     *            the member name, null returns null
     * @param prefix
     *            a prefix to add in order to map the name to a member
     * @return the referenced member
     * @throws NoSuchMemberException
     *             if the member was not found
     */
    public static <E extends Enum<E>> E fromString(
        final Class<E> enumType, final String name, final Separator prefix) {
        if (org.apache.commons.lang3.EnumUtils
            .isValidEnum(enumType, prefix.value + name))
            return org.apache.commons.lang3.EnumUtils
                .getEnum(enumType, prefix.value + name);
        else
            throw new NoSuchMemberException(format("enum {0} not found", name));
    }

    /**
     * If no matching enum member found.
     */
    public static final class NoSuchMemberException extends RuntimeException {

        public NoSuchMemberException(final String message) {
            super(message);
        }
    }

    /**
     * Prettifies an enumeration member; uses {@link #DEFAULT_ENUM_SEPARATOR} as
     * a separator.
     *
     * @param e
     *            the enumeration member
     * @return the pretiffied name
     */
    public static <E extends Enum<E>> String prettify(final E e) {
        return prettify(e, DEFAULT_ENUM_SEPARATOR);
    }

    /**
     * Prettifies an enumeration member.
     *
     * <p>
     * Example: SOME_ENUM_MEMBER will be prettified as Some Enum Member
     * </p>
     *
     * @param e
     *            the enumeration member
     * @param separator
     *            the separator to use for separating words
     * @return the pretiffied name
     */
    public static <E extends Enum<E>> String prettify(
        final @NotNull E e, final @NotNull Separator separator) {
        return StringUtils.replaceChars(
            WordUtils.capitalize(e.name(), separator.value),
            separator.value,
            ' ');
    }

    /**
     * Maps an enumeration member to its textual representation; uses
     * {@link #DEFAULT_ENUM_PREFIX} as the prefix.
     *
     * @param e
     *            the enumeration member
     * @return its textual representation
     */
    public static <E extends Enum<E>> String toString(final E e) {
        return toString(e, EnumUtils.DEFAULT_ENUM_PREFIX);
    }

    /**
     * Maps an enumeration member to its textual representation.
     *
     * @param e
     *            the enumeration member
     * @param prefix
     *            the prefix to be prepended to the referenced member name
     * @return its textual representation
     */
    public static <E extends Enum<E>> String toString(
        final E e, final Separator prefix) {
        return StringUtils.substringAfter(
            e.name(),
            CharUtils.toString(prefix.value));
    }

    /**
     * Allowed separators to be used in enumeration member names in order to
     * allow mapping to/from textual representations and to allow prettifying.
     *
     * @author aherscu
     *
     */
    public enum Separator {
        /**
         * The dollar $ character
         */
        DOLLAR('$'),

        /**
         * The underscore _ character.
         */
        UNDERSCORE('_');

        /**
         * The character value of this separator.
         */
        public final char value;

        Separator(final char separatorChar) {
            value = separatorChar;
        }
    }
}
