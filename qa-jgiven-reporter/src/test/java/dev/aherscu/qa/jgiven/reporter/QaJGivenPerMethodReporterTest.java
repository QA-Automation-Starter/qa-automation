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

import java.io.*;

import org.testng.annotations.*;
import org.testng.xml.*;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@Slf4j
public class QaJGivenPerMethodReporterTest {
    @Test
    @Ignore("too complex for now...")
    public void shouldUseOverridingModel() {
        val xmlSuite = new XmlSuite();
        val xmlTest = new XmlTest(xmlSuite);
        xmlTest.setXmlClasses(singletonList(new XmlClass("kuku")));
        new OverridingQaJGivenPerMethodReporter()
            .generateReport(singletonList(xmlSuite), emptyList(), null);
    }

    static class OverridingQaJGivenPerMethodReporter
        extends QaJGivenPerMethodReporter {
        protected void reportGenerated(
            final ScenarioModel scenarioModel,
            final File reportFile) {
            log.trace("report generated");
            super.reportGenerated(scenarioModel, reportFile);
        }

        @Override
        protected OverridingQaJGivenReportModel reportModel() {
            log.trace("using the overriding report model");
            return OverridingQaJGivenReportModel.builder().build();
        }
    }

    @SuperBuilder
    static class OverridingQaJGivenReportModel
        extends QaJGivenReportModel<ScenarioModel> {
        public final Mustache.Lambda customLambda = (frag, out) -> {
            log.trace("customLamdba called");
            out.write(frag.execute().hashCode());
        };
    }
}