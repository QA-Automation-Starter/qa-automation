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
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;

import org.apache.commons.io.*;

import com.google.gson.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;

@SuperBuilder
public class QaJGivenReporter
    extends AbstractQaJgivenReporter<QaJGivenReporter> {
    // TODO implements IReporter

    public static final String DEFAULT_TEMPLATE = "/qa-jgiven-reporter.html";

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

    public QaJGivenReporter() {
        productName = EMPTY;
        productVersion = EMPTY;
        testDocumentId = EMPTY;
        testDocumentRev = EMPTY;
        specDocumentId = EMPTY;
        specDocumentRev = EMPTY;
        planDocumentId = EMPTY;
        planDocumentRev = EMPTY;
        traceabilityDocumentId = EMPTY;
        traceabilityDocumentRev = EMPTY;
        templateResource = DEFAULT_TEMPLATE;
    }

    // TODO read the templateResource from testng.xml parameter
    // and ensure it does not collide with the one for
    // QaJGivenPerMethodReporter

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
            new File(outputDirectory,
                FilenameUtils.getName(templateResource)))) {
            TemplateUtils
                .using(Mustache.compiler())
                .loadFrom(templateResource)
                .execute(
                    aggregatedReportModel,
                    reportWriter);
        }
    }
}
