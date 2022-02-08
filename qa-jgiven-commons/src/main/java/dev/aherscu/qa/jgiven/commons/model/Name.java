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
package dev.aherscu.qa.jgiven.commons.model;

import static dev.aherscu.qa.tester.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.IOUtilsExtensions.*;
import static java.lang.Math.*;

import java.util.*;

import lombok.*;

/**
 * Represents a name.
 *
 * @author aherscu
 */
@Getter
public class Name extends Text {
    private static final List<String> firstNames =
        readUTF8Lines(
            getRelativeResourceAsStream(Name.class, "first-names.txt"));

    private static final List<String> lastNames  =
        readUTF8Lines(
            getRelativeResourceAsStream(Name.class, "last-names.txt"));

    /**
     * @param value
     *            the value value
     */
    public Name(final String value) {
        super(value);
    }

    public static Name firstNameFor(final int key) {
        return name(firstNames, key);
    }

    public static Name lastNameFor(final int key) {
        return name(lastNames, key);
    }

    public static Name name(final List<String> names, final int key) {
        return new Name(names.get(abs(key) % names.size()));
    }
}
