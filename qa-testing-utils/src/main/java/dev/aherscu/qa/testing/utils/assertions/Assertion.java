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
package dev.aherscu.qa.testing.utils.assertions;

import org.apache.commons.lang3.tuple.*;
import org.hamcrest.*;

/**
 *
 *
 * @param <E>
 *            type of expression to assert
 * @param <M>
 *            type of data to match on
 *
 * @author Adrian Herscu
 */
@SuppressWarnings({ "serial", })
public abstract class Assertion<E, M> extends MutablePair<E, Matcher<M>> {

    /**
     * Constructs an assertion.
     *
     * @param expr
     *            the expression
     * @param matcher
     *            the Hamcrest matcher; pass null to do a path existence
     *            assertion only
     */
    public Assertion(final E expr, final Matcher<M> matcher) {
        super(expr, matcher);
    }

    @Override
    public String toString() {
        return toString(
            null != right
                ? "%2$s <<<--->>> %1$s"
                : "%1$s exists");
    }

}
