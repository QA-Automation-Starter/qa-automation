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

package dev.aherscu.qa.jgiven.reporter;

import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.io.FileMatchers.*;

import java.io.*;

import org.testng.annotations.*;
import org.testng.xml.*;

public class QaJGivenPerMethodReporterTest {

    public static final File REPORTING_INPUT  = new File(
        "target/test-classes");
    public static final File REPORTING_OUTPUT = new File(
        "target/test-classes/reporting-output");

    @Test
    public void shouldGenerateReport() {
        QaJGivenPerMethodReporter.builder()
            .sourceDirectory(REPORTING_INPUT)
            .outputDirectory(REPORTING_OUTPUT)
            .build()
            .generateReport(
                singletonList(new XmlSuite()),
                emptyList(),
                null);

        assertThat(REPORTING_OUTPUT, anExistingDirectory());
        // TODO verify that an HTML file was generated at least
    }
}
