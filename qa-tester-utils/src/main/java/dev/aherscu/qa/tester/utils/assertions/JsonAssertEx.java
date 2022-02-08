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
import com.jayway.jsonpath.*;

import lombok.extern.slf4j.*;

/**
 * Supports asserting on multiple JSON paths.
 *
 * @author aherscu
 *
 */
@Slf4j
public class JsonAssertEx extends JsonAssert {

    /**
     * Creates a JSONAsserter
     *
     * @param json
     *            the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     */
    public static JsonAsserterEx with(final String json) {
        log.trace("verifying JSON contents: {}", json); //$NON-NLS-1$
        return new JsonAsserterExImpl(JsonPath.parse(json).json());
    }

}
