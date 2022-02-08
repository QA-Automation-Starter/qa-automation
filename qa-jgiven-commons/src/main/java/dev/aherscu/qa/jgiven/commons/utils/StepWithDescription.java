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

import static org.apache.commons.lang3.StringUtils.*;

import java.util.function.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;

/**
 * Wraps a {@link Function} with a description.
 *
 * @param <SELF>
 *            the type of step
 * @author Adrian Herscu
 */
public class StepWithDescription<SELF>
    implements DescriptionFormatter.HasDescription, Function<SELF, SELF> {

    private final String               description;
    private final Function<SELF, SELF> function;

    /**
     * @param description
     *            the description
     * @param function
     *            the wrapped {@link Function}
     */
    public StepWithDescription(
        final String description,
        final Function<SELF, SELF> function) {
        this.description = description;
        this.function = function;
    }

    public StepWithDescription(
        final Function<SELF, SELF> function) {
        this.description = EMPTY;
        this.function = function;
    }

    @Override
    public SELF apply(final SELF self) {
        return this.function.apply(self);
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
