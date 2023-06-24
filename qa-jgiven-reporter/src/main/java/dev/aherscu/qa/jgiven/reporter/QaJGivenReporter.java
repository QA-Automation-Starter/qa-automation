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

import static dev.aherscu.qa.testing.utils.FileUtilsExtensions.*;

import java.io.*;

import org.apache.commons.io.*;
import org.testng.xml.*;

import com.google.gson.*;
import com.tngtech.jgiven.report.json.*;
import com.tngtech.jgiven.report.model.*;

import lombok.*;
import lombok.experimental.*;

/**
 * All in one report.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class QaJGivenReporter
    extends AbstractQaJgivenReporter<CompleteReportModel, QaJGivenReporter> {
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

    /**
     * Builds a new reporter configured with additional TestNG XML suite
     * parameters. Currently, only {@code templateResource} is recognized.
     *
     * @param xmlSuite
     *            TestNG XML suite
     * @return reporter configured
     */
    @Override
    protected QaJGivenReporter with(final XmlSuite xmlSuite) {
        return ((QaJGivenReporter) super.with(xmlSuite))
            .toBuilder()
            .templateResource(templateResourceParamFrom(xmlSuite,
                DEFAULT_TEMPLATE_RESOURCE))
            .build();
    }

    /**
     * Generates a report including all test classes by aggregating all JGiven
     * generated JSON files.
     */
    @Override
    @SneakyThrows
    public void generate() {
        val targetReportFile = new File(outputDirectory,
            // FIXME Warning:(80, 13) Argument
            // 'FilenameUtils.getName(templateResource)' might be null
            FilenameUtils.getName(templateResource));
        // FIXME should supply a reportModelFile (a .json file)
        val aggregatedReportModel = reportModel(targetReportFile)
            .toBuilder()
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

        if (debug) {
            try (val debugReportWriter = fileWriter(
                new File(outputDirectory, "debug-report.json"))) {
                new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(aggregatedReportModel, debugReportWriter);
            }
        }

        try (val reportWriter = fileWriter(targetReportFile)) {
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
