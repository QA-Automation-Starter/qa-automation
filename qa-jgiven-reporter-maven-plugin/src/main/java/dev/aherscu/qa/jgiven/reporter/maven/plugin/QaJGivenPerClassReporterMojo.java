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
import static org.xhtmlrenderer.simple.PDFRenderer.*;

import java.io.*;

import org.apache.commons.io.filefilter.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import com.itextpdf.text.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;

/**
 * Generates JGiven report in QA format per class (segregated).
 */
@Mojo(name = "segregated-report", defaultPhase = LifecyclePhase.VERIFY)
public class QaJGivenPerClassReporterMojo extends AbstractQaJgivenReporterMojo {

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
            forceMkdir(outputDirectory);

            val template = TemplateUtils
                .using(Mustache.compiler())
                .loadFrom("/qa-jgiven-perclass-reporter.html");

            for (val reportModelFile : listFiles(
                sourceDirectory, new SuffixFileFilter(".json"), null)) {

                getLog().debug("reading " + reportModelFile);
                try (val reportWriter = fileWriter(
                    reportFile(reportModelFile, ".html"))) {
                    template.execute(QaJGivenReportModel.builder()
                        .log(getLog())
                        .jgivenReport(new ReportModelFileReader()
                            .apply(reportModelFile))
                        .screenshotScale(screenshotScale)
                        .datePattern(datePattern)
                        .build(),
                        reportWriter);
                }

                if (pdf) {
                    renderToPDF(
                        reportFile(reportModelFile, ".html"),
                        reportFile(reportModelFile, ".pdf")
                            .getAbsolutePath());
                }
            }
        } catch (final IOException | DocumentException e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(
                "Error while trying to generate HTML and/or PDF reports", e);
        }
    }

    private File reportFile(
        final File reportModelFile,
        final String extension) {
        return new File(outputDirectory,
            reportModelFile.getName() + extension);
    }
}
