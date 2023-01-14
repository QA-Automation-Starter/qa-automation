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

package dev.aherscu.qa.jgiven.reporter.maven.plugin;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import dev.aherscu.qa.jgiven.reporter.*;
import lombok.extern.slf4j.*;

/**
 * Generates JGiven report in QA format.
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.VERIFY)
@Slf4j
public class QaJGivenReporterMojo extends AbstractQaJgivenReporterMojo {

    @Parameter(defaultValue = QaJGivenReporter.DEFAULT_TEMPLATE_RESOURCE)
    protected String templateResource;
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

        try {
            QaJGivenReporter.builder()
                .templateResource(templateResource)
                .outputDirectory(outputDirectory)
                .sourceDirectory(sourceDirectory)
                .screenshotScale(screenshotScale)
                .pdf(pdf)
                .datePattern(datePattern)
                .productName(productName)
                .productVersion(productVersion)
                .testDocumentId(testDocumentId)
                .testDocumentRev(testDocumentRev)
                .specDocumentId(specDocumentId)
                .specDocumentRev(specDocumentRev)
                .planDocumentId(planDocumentId)
                .planDocumentRev(planDocumentRev)
                .traceabilityDocumentId(traceabilityDocumentId)
                .traceabilityDocumentRev(traceabilityDocumentRev)
                .build()
                .prepare()
                .generate();
        } catch (final Exception e) {
            throw new MojoExecutionException(
                "Error while trying to generate HTML reports", e);
        }
    }
}
