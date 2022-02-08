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

package dev.aherscu.qa.orcanos.publisher.maven.plugin.model;

import static org.apache.commons.jxpath.JXPathContext.*;

import org.apache.commons.jxpath.*;
import org.testng.annotations.*;

@Test
public class EmptyExecutionSetRunResultsExTest extends
    AbstractExecutionSetRunResultsExTest {

    EmptyExecutionSetRunResultsExTest() {
        super("Empty_Execution_Set_Run_Results.xml");
    }

    @Test(expectedExceptions = JXPathNotFoundException.class)
    public void shouldFailReadingInputRunParam() {
        newContext(results)
            .getValue(
                "/run/inputRunParams/param[name='Device brand and Model name']/value");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailReadingSteps() {
        results.get().steps();
    }

    @Test(expectedExceptions = JXPathException.class)
    public void shouldFailUpdatingInputRunParam() {
        newContext(results.get())
            .setValue(
                "/run/inputRunParams/param[name='Device brand and Model name']/value",
                UPDATED);
    }
}
