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

/**
 * Represents an JXPath assertion as a path paired with a Hamcrest matcher.
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
public class JXPathAssertion<M> extends Assertion<String, M> {

    /**
     * Constructs a JXPath assertion without and expected value. Use to assert
     * path existence only.
     * 
     * @param jxpath
     *            the JXPath
     */
    public JXPathAssertion(final String jxpath) {
        super(jxpath, null);
    }
}
