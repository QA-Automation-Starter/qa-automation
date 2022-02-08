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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import com.google.common.collect.*;

@Test
public class ExecutionSetRunResultsExTest
    extends AbstractExecutionSetRunResultsExTest {

    ExecutionSetRunResultsExTest() {
        super("Execution_Set_Run_Results.xml");
    }

    public void shouldReadInputRunParam() {
        assertThat(newContext(results.get())
            .getValue(
                "/run/inputRunParams/param[name='Device brand and Model name']/value"),
            is("Samsung Galaxy S9 Plus WQHD"));
    }

    public void shouldReadRunVersion() {
        assertThat(newContext(results.get())
            .getValue("/run/version"),
            is("1.0.0.0"));
    }

    public void shouldUpdateAdditionalFields() {
        results.get()
            .withAdditionalFields(ImmutableMap.<String, String> builder()
                .put(
                    "/run/inputRunParams/param[name='Device brand and Model name']/value",
                    UPDATED)
                .build());

        assertThat(results.get()
            .getRun()
            .getInputRunParams()
            .getParam()
            .stream()
            .filter(param -> "Device brand and Model name"
                .equals(param.getName()))
            .findFirst()
            .orElseThrow(RuntimeException::new)
            .getValue(),
            is(UPDATED));
    }

    public void shouldUpdateInputRunParam() {
        newContext(results.get())
            .setValue(
                "/run/inputRunParams/param[name='Device brand and Model name']/value",
                UPDATED);

        assertThat(results.get()
            .getRun()
            .getInputRunParams()
            .getParam()
            .stream()
            .filter(param -> "Device brand and Model name"
                .equals(param.getName()))
            .findFirst()
            .orElseThrow(RuntimeException::new)
            .getValue(),
            is(UPDATED));
    }

    public void shouldUpdateRunVersion() {
        newContext(results.get())
            .setValue("/run/version", UPDATED);

        assertThat(results.get().getRun().getVersion(),
            is(UPDATED));
    }
}
