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
package dev.aherscu.qa.tester.utils.assertions;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.apache.commons.jxpath.*;

import lombok.*;

/**
 * Supports asserting on multiple JXPaths.
 *
 * @author Adrian Herscu
 *
 */
@SuppressWarnings("boxing")
public class JXPathAsserterImpl implements JXPathAsserter {

    private final JXPathContext context;

    /**
     * Instantiates a new JXPathAsserter
     *
     * @param object
     *            the object to work on
     */
    public JXPathAsserterImpl(final Object object) {
        this.context = JXPathContext.newContext(object);
    }

    @Override
    public JXPathAsserter assertHas(
        final Iterable<? extends JXPathAssertion<?>> expectedContents) {
        val assertionErrors = new ArrayList<String>();
        int counter = 0;

        for (val jxpathAssertion : expectedContents) {
            try {
                if (null != jxpathAssertion.getRight()) {
                    assertThat(context.getValue(jxpathAssertion.left))
                        .isEqualTo(jxpathAssertion.right);
                } else {
                    assertThatThrownBy(
                        () -> context.getValue(jxpathAssertion.left))
                            .isInstanceOf(JXPathNotFoundException.class);
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
    public JXPathAsserter assertHas(
        final JXPathAssertion<?>... expectedContents) {
        return assertHas(asList(expectedContents));
    }

}
