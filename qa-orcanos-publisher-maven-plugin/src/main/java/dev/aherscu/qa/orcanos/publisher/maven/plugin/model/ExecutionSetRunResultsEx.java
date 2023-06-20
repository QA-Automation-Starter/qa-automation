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

import static com.google.common.collect.Maps.*;
import static j2html.TagCreator.*;
import static java.util.Objects.*;
import static org.apache.commons.jxpath.JXPathContext.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.net.*;
import java.util.*;

import javax.xml.bind.annotation.*;

import com.google.common.collect.*;

/**
 * Extends {@link ExecutionSetRunResults} to provide specialized accessors.
 * {@link ExecutionSetRunResults} is generated from
 * {@code xsd/Execution_Set_Run_Results.xsd} by the JAXB2 xjc Maven plugin.
 */
@XmlRootElement(name = "Execution_Set_Run_Results")
public class ExecutionSetRunResultsEx extends ExecutionSetRunResults {

    public List<ExecutionSetRunResults.Run.TestInExecLine.Steps.Step> steps() {
        try {
            return getRun().getTestInExecLine().getSteps().getStep();
        } catch (final Throwable t) {
            throw new RuntimeException("invalid execution set", t);
        }
    }

    public ExecutionSetRunResultsEx withAdditionalFields(
        final Properties additionalFields) {
        return nonNull(additionalFields)
            ? withAdditionalFields(fromProperties(additionalFields))
            : this;
    }

    public ExecutionSetRunResultsEx withAdditionalFields(
        final Map<String, String> additionalFields) {
        if (nonNull(additionalFields))
            additionalFields
                .forEach((jxpath, value) -> newContext(this)
                    .setValue(jxpath, value));
        return this;
    }

    public ExecutionSetRunResultsEx withAttachment(
        final String key,
        final URI href) {
        steps().forEach(step -> step
            .setActual(a()
                .withHref(href.toString())
                .withText(key)
                .render()));

        return this;
    }

    public ExecutionSetRunResultsEx withDeviceInfo(
        final String deviceName,
        final String platformName,
        final String platformVersion) {
        return isNoneBlank(deviceName, platformName, platformVersion)
            ? withAdditionalFields(ImmutableMap.<String, String> builder()
                .put(
                    "/run/inputRunParams/param[name='Device brand and Model name']/value",
                    deviceName)
                .put(
                    "/run/inputRunParams/param[name='OS']/value",
                    platformName + SPACE + platformVersion)
                .build())
            : this;
    }

    public ExecutionSetRunResultsEx withStatus(final String value) {
        steps()
            .forEach(step -> step
                .setRun(new StepRunEx()
                    .withStatus(value)));

        return this;
    }
}
