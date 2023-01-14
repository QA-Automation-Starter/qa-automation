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

import static dev.aherscu.qa.jgiven.reporter.QaJGivenPerClassReporter.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import dev.aherscu.qa.jgiven.reporter.*;

/**
 * Generates JGiven report in QA format per class (segregated).
 */
@Mojo(name = "segregated-report", defaultPhase = LifecyclePhase.VERIFY)
public class QaJGivenPerClassReporterMojo extends AbstractQaJgivenReporterMojo {

    @Parameter(defaultValue = DEFAULT_TEMPLATE_RESOURCE)
    protected String templateResource;

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
            QaJGivenPerClassReporter.builder()
                .outputDirectory(outputDirectory)
                .sourceDirectory(sourceDirectory)
                .screenshotScale(screenshotScale)
                .templateResource(templateResource)
                .pdf(pdf)
                .datePattern(datePattern)
                .build()
                .generate();
        } catch (final Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(
                "Error while trying to generate HTML and/or PDF reports", e);
        }
    }

}
