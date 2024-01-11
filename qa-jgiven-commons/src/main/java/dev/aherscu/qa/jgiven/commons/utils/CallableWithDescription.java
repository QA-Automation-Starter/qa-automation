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

package dev.aherscu.qa.jgiven.commons.utils;

import java.util.concurrent.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;

/**
 * Wraps a {@link Callable} with a description.
 *
 * @param <V>
 *            the type of output
 *
 * @author Adrian Herscu
 *
 */
public class CallableWithDescription<V>
    implements DescriptionFormatter.HasDescription, Callable<V> {

    private final String      description;
    private final Callable<V> callable;

    /**
     * @param description
     *            the description
     * @param callable
     *            the wrapped {@link Callable}
     */
    public CallableWithDescription(
        final String description,
        final Callable<V> callable) {
        this.description = description;
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        return this.callable.call();
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public final String toString() {
        return description();
    }
}
