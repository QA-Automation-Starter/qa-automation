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

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;

import java.io.*;

import org.apache.commons.io.*;
import org.testng.*;

import com.google.gson.*;
import com.tngtech.jgiven.report.json.*;
import com.tngtech.jgiven.report.model.*;

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder(toBuilder = true)
@Slf4j
@ToString(callSuper = true)
public class QaJGivenReporter
    extends AbstractQaJgivenReporter<CompleteReportModel, QaJGivenReporter>
    implements IReporter {
    public static final String DEFAULT_TEMPLATE_RESOURCE =
        "/qa-jgiven-reporter.html";

    public final String        productName;
    public final String        productVersion;
    public final String        testDocumentId;
    public final String        testDocumentRev;
    public final String        specDocumentId;
    public final String        specDocumentRev;
    public final String        planDocumentId;
    public final String        planDocumentRev;
    public final String        traceabilityDocumentId;
    public final String        traceabilityDocumentRev;

    // TODO read the templateResource from testng.xml parameter
    // and ensure it does not collide with the one for
    // QaJGivenPerMethodReporter

    @Override
    @SneakyThrows
    public void generate() {
        val aggregatedReportModel = reportModel()
            .withJgivenReport(
                new ReportModelReader(
                    QaJGivenReportConfig.builder()
                        .sourceDir(sourceDirectory)
                        .targetDir(outputDirectory)
                        .build())
                    .readDirectory())
            .withScreenshotScale(screenshotScale)
            .withDatePattern(datePattern)
            .withTestDocumentId(testDocumentId)
            .withTestDocumentRev(testDocumentRev)
            .withSpecDocumentId(specDocumentId)
            .withSpecDocumentRev(specDocumentRev)
            .withPlanDocumentId(planDocumentId)
            .withPlanDocumentRev(planDocumentRev)
            .withTraceabilityDocumentId(traceabilityDocumentId)
            .withTraceabilityDocumentRev(traceabilityDocumentRev)
            .withProductName(productName)
            .withProductVersion(productVersion);

        if (debug) {
            try (val debugReportWriter = fileWriter(
                new File(outputDirectory, "debug-report.json"))) {
                new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(aggregatedReportModel, debugReportWriter);
            }
        }

        try (val reportWriter = fileWriter(
            new File(outputDirectory,
                FilenameUtils.getName(templateResource)))) {
            template()
                .execute(aggregatedReportModel, reportWriter);
        }

        // FIXME should work only with html reports
        // if (pdf) {
        // renderToPDF(
        // reportFile(reportModelFile, ".html"),
        // reportFile(reportModelFile, ".pdf")
        // .getAbsolutePath());
        // }
    }

}
