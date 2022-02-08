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
package dev.aherscu.qa.testing.example.model;

import static dev.aherscu.qa.tester.utils.ListUtils.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static dev.aherscu.qa.testing.example.model.Person.NameOrder.*;
import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Represents a person.
 *
 * @author Adrian Herscu
 *
 */
@AllArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN",
    justification = "hashcode implemented by lombok")
public class Person {
    /**
     * The first name.
     */
    public final Name firstName;
    /**
     * The last name.
     */
    public final Name lastName;

    /**
     * @param fullName
     *            first name followed by last name, separated by specified
     *            delimiter
     * @param regexDelimiter
     *            the delimiting regular expression
     * @throws IllegalArgumentException
     *             if the full name is blank, empty or null
     * @throws PatternSyntaxException
     *             if the regular expression describing the delimiter is
     *             malformed
     * @return a person
     */
    public static Person personFrom(
        final String fullName,
        final String regexDelimiter,
        final NameOrder order) {
        if (isBlank(fullName))
            throw new IllegalArgumentException(
                "full name cannot be blank, empty or null");

        val parsedFullName = Stream.of(fullName.split(regexDelimiter))
            .map(String::trim)
            .collect(toList());

        return NameOrder.FIRST_LAST.equals(order)
            ? new Person(
                new Name(getOrElse(parsedFullName, 0, EMPTY)),
                new Name(getOrElse(parsedFullName, 1, EMPTY)))
            : new Person(
                new Name(getOrElse(parsedFullName, 1, EMPTY)),
                new Name(getOrElse(parsedFullName, 0, EMPTY)));
    }
    // TODO should include dateOfBirth and age -- maybe validated during init

    public static Person personFrom(
        final String fullName,
        final String regexDelimiter) {
        return personFrom(fullName, regexDelimiter, NameOrder.FIRST_LAST);
    }

    /**
     * Person's DOM identifier.
     */
    public Name domId() {
        return new Name(fullName(UNDERSCORE, LAST_FIRST)
            .toString()
            .replaceAll("(.)([0-9])", "$1_$2")
            .replaceAll(" ", "_")
            .toLowerCase(Locale.ENGLISH));
    }

    /**
     * Full name with specified delimiter and order
     *
     * @param delimiter
     *            the delimiter to use between names
     * @param order
     *            the order to use for names
     * @return the full name
     */
    public Name fullName(final String delimiter, final NameOrder order) {
        return new Name(NameOrder.FIRST_LAST.equals(order)
            ? firstName + delimiter + lastName
            : lastName + delimiter + firstName);
    }

    /**
     * @return first name followed by last name, space separated
     */
    public Name fullName() {
        return fullName(SPACE, NameOrder.FIRST_LAST);
    }

    public enum NameOrder {
        FIRST_LAST, LAST_FIRST
    }
}
