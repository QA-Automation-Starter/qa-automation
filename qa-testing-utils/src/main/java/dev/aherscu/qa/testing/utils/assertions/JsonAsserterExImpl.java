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

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.util.Arrays.*;

import java.util.ArrayList;

import dev.aherscu.qa.testing.utils.assertions.impl.*;
import lombok.*;

/**
 * Supports asserting on multiple JSON paths.
 *
 * @author aherscu
 *
 */
@SuppressWarnings("boxing")
final class JsonAsserterExImpl extends JsonAsserterImpl
    implements JsonAsserterEx {

    /**
     * Instantiates a new JSONAsserter
     *
     * @param jsonObject
     *            the object to make asserts on
     */
    public JsonAsserterExImpl(final Object jsonObject) {
        super(jsonObject);
    }

    @Override
    public JsonAsserter assertHas(
        final Iterable<? extends JsonAssertion<?>> expectedContents) {
        // noinspection TypeMayBeWeakened
        val assertionErrors = new ArrayList<String>();
        int counter = 0;

        for (val jsonAssertion : expectedContents) {
            try {
                if (null != jsonAssertion.getRight()) {
                    assertThat(
                        jsonAssertion.getLeft(), jsonAssertion.getRight());
                } else {
                    assertNotDefined(jsonAssertion.getLeft());
                }
            } catch (final AssertionError e) {
                assertionErrors.add(
                    String.format("%d) %s", //$NON-NLS-1$
                        ++counter,
                        e.getMessage()));
            }
        }

        if (!assertionErrors.isEmpty())
            throw new AssertionError(
                join(assertionErrors, SEMI + CR + LF));

        return this;
    }

    @Override
    public JsonAsserter assertHas(
        final JsonAssertion<?>... expectedContents) {
        return assertHas(asList(expectedContents));
    }

}
