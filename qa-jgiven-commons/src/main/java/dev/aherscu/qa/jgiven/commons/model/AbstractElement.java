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

import dev.aherscu.qa.tester.utils.*;

/**
 * Provides default implementations for all model elements.
 *
 * @param <T>
 *            the derived type
 *
 * @author aherscu
 */
public abstract class AbstractElement<T> implements Element<T> {

    @Override
    public T fromStrings(final String... strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        // noinspection MagicNumber
        return ObjectUtilsExtensions.toString(this, ",", 20); //$NON-NLS-1$
    }

}
