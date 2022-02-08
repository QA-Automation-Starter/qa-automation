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
 * Represents a numeric identifier.
 *
 * @param <T>
 *            type of numeric
 *
 * @author Adrian Herscu
 */
public class NumericId<T extends Number> extends Numeric<T> {

    /**
     * @param value
     *            the value
     */
    protected NumericId(final T value) {
        super(value);
    }
}
