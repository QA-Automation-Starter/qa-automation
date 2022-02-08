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

import static dev.aherscu.qa.tester.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.IOUtilsExtensions.*;
import static java.nio.charset.StandardCharsets.*;
import static javax.xml.bind.JAXB.*;
import static org.hamcrest.MatcherAssert.*;
import static org.xmlunit.matchers.CompareMatcher.*;

import java.io.*;

import org.testng.annotations.*;

import lombok.*;

public abstract class AbstractExecutionSetRunResultsExTest {
    protected static final String                   UPDATED = "updated";

    protected final String                          resultsXmlString;

    protected ThreadLocal<ExecutionSetRunResultsEx> results =
        new ThreadLocal<>();

    AbstractExecutionSetRunResultsExTest(
        final String executionSetRunResultsFileName) {
        resultsXmlString = fromUTF8(
            getRelativeResourceAsStream(getClass(),
                executionSetRunResultsFileName));
    }

    @SneakyThrows
    @BeforeMethod
    public void beforeMethodUnmarshalExecutionSetRunResultsEx() {
        results.set(unmarshal(new StringReader(resultsXmlString),
            ExecutionSetRunResultsEx.class));
    }

    @Test
    @SneakyThrows
    public void shouldSerializeExecutionSetRunResultsToXml() {
        try (val resultsByteArrayOutputStream = new ByteArrayOutputStream()) {
            marshal(results.get(), resultsByteArrayOutputStream);
            assertThat(resultsByteArrayOutputStream.toString(UTF_8.name()),
                isIdenticalTo(resultsXmlString)
                    .ignoreComments()
                    .ignoreWhitespace());
        }
    }
}
