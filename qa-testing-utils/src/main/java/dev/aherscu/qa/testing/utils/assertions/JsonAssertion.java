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

import org.hamcrest.*;

/**
 * Represents a JSON assertion as a JSON path paired with a Hamcrest matcher.
 * 
 * <p>
 * NOTE: not designed for serialization
 * </p>
 *
 * @param <M>
 *            type of data to match
 * 
 * @author aherscu
 */
@SuppressWarnings("serial")
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SE_NO_SERIALVERSIONID")
public class JsonAssertion<M> extends Assertion<String, M> {

    /**
     * Constructs a JSON assertion without a matcher. Use to assert path
     * existence only.
     * 
     * @param jsonPath
     *            the JSON path
     */
    public JsonAssertion(final String jsonPath) {
        super(jsonPath, null);
    }

    /**
     * Constructs a JSON assertion with specified matcher.
     * 
     * @param jsonPath
     *            the JSON path
     * @param matcher
     *            the matcher
     */
    public JsonAssertion(final String jsonPath, final Matcher<M> matcher) {
        super(jsonPath, matcher);
    }
}