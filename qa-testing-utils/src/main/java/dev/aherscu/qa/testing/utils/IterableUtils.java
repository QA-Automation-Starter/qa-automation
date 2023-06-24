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

import java.util.*;
import java.util.stream.*;

import lombok.experimental.*;

/**
 * Iterable utilities.
 *
 * @author Adrian Herscu
 *
 */
@UtilityClass
public class IterableUtils {
    /**
     * @param iterator
     *            the iterator to wrap
     * @param <T>
     *            iterated type
     * @return the iterable over specified iterator
     */
    public <T> Iterable<T> iterable(final Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * Converts an iterable of objects to a list of strings by calling
     * {@link String#toString()} on each element.
     *
     * @param i
     *            the iterable
     * @return the list of strings
     */
    public List<String> toString(final Iterable<?> i) {
        return StreamSupport.stream(i.spliterator(), false)
            .map(Object::toString)
            .collect(Collectors.toList());
    }
}
