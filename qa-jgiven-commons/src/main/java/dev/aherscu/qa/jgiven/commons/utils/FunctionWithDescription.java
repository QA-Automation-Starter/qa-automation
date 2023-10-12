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

import java.util.function.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;

/**
 * Wraps a {@link Function} with a description.
 *
 * @param <T>
 *            the type of input
 *
 * @param <R>
 *            the type of output
 *
 * @author Adrian Herscu
 *
 */
public class FunctionWithDescription<T, R>
    implements DescriptionFormatter.HasDescription, Function<T, R> {

    private final String         description;
    private final Function<T, R> functor;

    /**
     * @param description
     *            the description
     * @param functor
     *            the wrapped {@link UnaryOperator}
     */
    public FunctionWithDescription(
        final String description,
        final Function<T, R> functor) {
        this.description = description;
        this.functor = functor;
    }

    @Override
    public R apply(final T t) {
        return this.functor.apply(t);
    }

    @Override
    public String description() {
        return description;
    }

}
