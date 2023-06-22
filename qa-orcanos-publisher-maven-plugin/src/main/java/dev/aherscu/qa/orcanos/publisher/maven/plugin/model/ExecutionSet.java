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

package dev.aherscu.qa.orcanos.publisher.maven.plugin.model;

import com.fasterxml.jackson.annotation.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Models a request to Orcanos {@code Get_Execution_Run_Details_XML} REST API
 * method.
 */
@SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "read by jax-rs framework")
@Builder
public final class ExecutionSet {
    public static final String EXECUTION_SET_ID = "Execution_Set_ID";
    public static final String TEST_ID          = "Test_ID";

    @JsonProperty(EXECUTION_SET_ID)
    public final String        executionSetId;
    @JsonProperty(TEST_ID)
    public final String        testId;
}
