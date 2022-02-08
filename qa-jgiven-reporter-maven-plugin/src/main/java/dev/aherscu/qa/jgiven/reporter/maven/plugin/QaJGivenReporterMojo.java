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

package dev.aherscu.qa.jgiven.reporter.maven.plugin;

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;

import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.gson.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;

/**
 * Generates JGiven report in QA format.
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.VERIFY)
public class QaJGivenReporterMojo extends AbstractQaJgivenReporterMojo {

    /**
     * The product name.
     */
    @Parameter(defaultValue = "A Product")
    protected String productName;

    /**
     * The product version.
     */
    @Parameter(defaultValue = "A Product Version")
    protected String productVersion;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String testDocumentId;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String testDocumentRev;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String specDocumentId;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String specDocumentRev;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String planDocumentId;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String planDocumentRev;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String traceabilityDocumentId;

    /**
     * The test document identifier in document control.
     */
    @Parameter(defaultValue = "ttttt")
    protected String traceabilityDocumentRev;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("skipping");
            return;
        }

        getLog().info(
            "JGiven JSON report source directory: " + sourceDirectory);
        getLog().info(
            "JGiven HTML report output directory: " + outputDirectory);

        try {
            val aggregatedReportModel = QaJGivenReportModel.builder()
                .log(getLog())
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
        } catch (final IOException e) {
            throw new MojoExecutionException(
                "Error while trying to generate HTML reports", e);
        }
    }
}
