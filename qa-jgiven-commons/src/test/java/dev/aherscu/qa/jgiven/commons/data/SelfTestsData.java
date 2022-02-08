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
package dev.aherscu.qa.jgiven.commons.data;

import org.testng.annotations.*;

import lombok.experimental.*;

/**
 * Provides data for the self tests.
 * 
 * @author aherscu
 *
 */
// NOTE classes containing data providers must be public
@UtilityClass
public final class SelfTestsData {

    /**
     * A set of dummy data just to test parallelism.
     * 
     * @return the data set
     */
    @DataProvider(parallel = true)
    public static Object[][] dummyData() {
        return new Object[][] {
            { "one", "one" }, //$NON-NLS-1$ //$NON-NLS-2$
            { "two", "two" } //$NON-NLS-1$ //$NON-NLS-2$
        };
    }
}
