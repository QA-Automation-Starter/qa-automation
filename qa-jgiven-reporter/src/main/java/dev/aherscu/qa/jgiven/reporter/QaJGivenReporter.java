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

package dev.aherscu.qa.jgiven.reporter;

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;

import java.io.*;

import com.google.gson.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;

public class QaJGivenReporter extends AbstractQaJgivenReporter {

    public final String productName;
    public final String productVersion;
    public final String testDocumentId;
    public final String testDocumentRev;
    public final String specDocumentId;
    public final String specDocumentRev;
    public final String planDocumentId;
    public final String planDocumentRev;
    public final String traceabilityDocumentId;
    public final String traceabilityDocumentRev;

    @Builder
    public QaJGivenReporter(File outputDirectory, File sourceDirectory,
        boolean debug, double screenshotScale, String datePattern, boolean pdf,
        String productName, String productVersion, String testDocumentId,
        String testDocumentRev, String specDocumentId, String specDocumentRev,
        String planDocumentId, String planDocumentRev,
        String traceabilityDocumentId, String traceabilityDocumentRev) {
        super(outputDirectory, sourceDirectory, debug, screenshotScale,
            datePattern, pdf);
        this.productName = productName;
        this.productVersion = productVersion;
        this.testDocumentId = testDocumentId;
        this.testDocumentRev = testDocumentRev;
        this.specDocumentId = specDocumentId;
        this.specDocumentRev = specDocumentRev;
        this.planDocumentId = planDocumentId;
        this.planDocumentRev = planDocumentRev;
        this.traceabilityDocumentId = traceabilityDocumentId;
        this.traceabilityDocumentRev = traceabilityDocumentRev;
    }

    public void generate() throws IOException {
        val aggregatedReportModel = QaJGivenReportModel.builder()
            .jgivenReport(
                new ReportModelReader(
                    QaJGivenReportConfig.builder()
                        .sourceDir(sourceDirectory)
                        .targetDir(outputDirectory)
                        .build())
                            .readDirectory())
            .screenshotScale(screenshotScale)
            .datePattern(datePattern)
            .testDocumentId(testDocumentId)
            .testDocumentRev(testDocumentRev)
            .specDocumentId(specDocumentId)
            .specDocumentRev(specDocumentRev)
            .planDocumentId(planDocumentId)
            .planDocumentRev(planDocumentRev)
            .traceabilityDocumentId(traceabilityDocumentId)
            .traceabilityDocumentRev(traceabilityDocumentRev)
            .productName(productName)
            .productVersion(productVersion)
            .build();

        forceMkdir(outputDirectory);

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
            new File(outputDirectory, "qa-report.html"))) {
            TemplateUtils
                .using(Mustache.compiler())
                .loadFrom("/qa-jgiven-reporter.html")
                .execute(
                    aggregatedReportModel,
                    reportWriter);
        }
    }
}
