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

/**
 * Root of all model elements.
 *
 * @param <T>
 *            the derived type
 *
 * @author aherscu
 */
public interface Element<T> {
    /**
     * Deserializes this object from given strings.
     *
     * @param strings
     *            the strings from which to deserialize
     * @return the deserialized object
     */
    T fromStrings(final String... strings);
}
