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

import com.jayway.jsonassert.*;

import lombok.extern.slf4j.*;

/**
 * Supports asserting on multiple JXPaths.
 *
 * @author aherscu
 *
 */
@Slf4j
public class JXPathAssert extends JsonAssert {

    /**
     * Creates a JXPathAsserter
     *
     * @param object
     *            the object to create a JXPathAsserter for
     * @return a JXPath asserter initialized with the provided document
     */
    public static JXPathAsserter with(final Object object) {
        log.trace("verifying object contents: {}", object);
        return new JXPathAsserterImpl(object);
    }

}
