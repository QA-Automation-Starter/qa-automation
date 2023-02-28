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

import java.nio.charset.*;

import javax.xml.bind.*;

import org.apache.commons.io.output.*;

import com.fasterxml.jackson.annotation.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Models a request to Orcanos Record_Execution_Results_New REST API.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "value object")
@Builder
public final class RecordExecutionResults {
    private final ExecutionSetRunResults executionSetRunResults;

    @JsonProperty("sXML")
    @SneakyThrows
    public String sXml() {
        val marshaller = JAXBContext
            .newInstance(ExecutionSetRunResults.class)
            .createMarshaller();
        // marshaller.setProperty(JAXB_FORMATTED_OUTPUT, false);
        try (ByteArrayOutputStream buffer =
            new ByteArrayOutputStream()) {
            marshaller.marshal(executionSetRunResults, buffer);
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }
}
