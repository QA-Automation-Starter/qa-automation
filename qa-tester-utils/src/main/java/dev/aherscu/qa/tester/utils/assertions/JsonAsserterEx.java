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

import com.jayway.jsonassert.*;

/**
 * Supports asserting on multiple JSON paths.
 *
 * @author aherscu
 *
 */
public interface JsonAsserterEx extends JsonAsserter {

    /**
     * Asserts that expected contents match.
     * 
     * @param expectedContents
     *            the expected contents
     *
     * @return this to allow fluent assertion chains
     */
    JsonAsserter assertHas(
        Iterable<? extends JsonAssertion<?>> expectedContents);

    /**
     * Asserts that expected contents match.
     * 
     * @param expectedContents
     *            the expected contents
     *
     * @return this to allow fluent assertion chains
     */
    JsonAsserter assertHas(JsonAssertion<?>... expectedContents);
}
