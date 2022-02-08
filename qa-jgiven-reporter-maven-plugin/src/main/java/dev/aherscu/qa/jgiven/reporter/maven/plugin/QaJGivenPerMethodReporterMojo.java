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

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Multimaps.*;
import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.jooq.lambda.*;

import com.google.common.collect.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;

/**
 * Generates JGiven report in QA format per method with associated attributes.
 * Scans all JGiven report <tt>.json</tt> files for specified
 * {@link #referenceTag}. Only reports having such tag are processed. For every
 * scenario method, all its tags are written in an associated "attributes" file.
 * The association is done by file name. These "attributes" can later be used as
 * additional publishing information.
 */
@Mojo(name = "segregated-permethod-report",
    defaultPhase = LifecyclePhase.VERIFY)
public class QaJGivenPerMethodReporterMojo
    extends AbstractQaJgivenReporterMojo {

    // TODO allow mapping current Execution Set IDs to other ID

    /**
     * JGiven <tt>Reference</tt>> tag. Not linked in this module in order to
     * allow faster builds (otherwise the build of this module would depend on
     * <tt>qa-jgiven-commons</tt> module).
     */
    @Parameter(defaultValue = "Reference")
    protected String referenceTag = "Reference";

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
        getLog().info("JGiven tagging: " + referenceTag);

        if (isNull(referenceTag))
            throw new MojoExecutionException("reference tag not defined");

        try {
            forceMkdir(outputDirectory);

            val template = TemplateUtils
                .using(Mustache.compiler())
                .loadFrom("/qa-jgiven-permethod-reporter.html");

            listFiles(sourceDirectory,
                new SuffixFileFilter(".json"), null)
                    .parallelStream()
                    .peek(reportModelFile -> getLog()
                        .debug("reading " + reportModelFile.getName()))
                    .flatMap(reportModelFile -> new ReportModelFileReader()
                        .apply(reportModelFile).model
                            .getScenarios()
                            .stream()
                            .filter(scenarioModel -> scenarioModel
                                .getTagIds()
                                .stream()
                                .anyMatch(
                                    tagId -> tagId.contains(referenceTag))))
                    // DELETEME following two lines seeem redundant
                    .collect(toCollection(LinkedList::new))
                    .parallelStream()
                    .peek(scenarioModel -> getLog()
                        .debug("processing " + targetNameFor(scenarioModel)))
                    .forEach(Unchecked.consumer(
                        scenarioModel -> {
                            val reportFile = new File(outputDirectory,
                                targetNameFor(scenarioModel) + ".html");
                            try (val reportWriter = fileWriter(reportFile)) {
                                template.execute(
                                    QaJGivenReportModel.builder()
                                        .log(getLog())
                                        .jgivenReport(scenarioModel)
                                        .screenshotScale(screenshotScale)
                                        .datePattern(datePattern)
                                        .build(),
                                    reportWriter);
                                applyAttributesFor(scenarioModel, reportFile);
                            }
                        }));
        } catch (final Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(
                "Error while trying to generate HTML and/or PDF reports", e);
        }
    }

    @SneakyThrows
    private void applyAttributesFor(
        final ScenarioModel scenarioModel,
        final File reportFile) {
        getLog().info("setting attributes for " + reportFile.getName());

        try (val attributesWriter = fileWriter(
            new File(reportFile.getAbsolutePath() + ".attributes"))) {
            val p = new Properties();
            p.putAll(scenarioModel.getTagIds()
                .stream()
                // TODO apply the mapping here
                .map(tag -> immutableEntry(
                    substringBefore(tag, DASH),
                    substringAfter(tag, DASH)))
                // NOTE there might be multiple
                // DeviceName/PlatformName/PlatformVersion tags
                .collect(toMultimap(Map.Entry::getKey, Map.Entry::getValue,
                    MultimapBuilder.hashKeys().arrayListValues()::build))
                .asMap()
                .entrySet()
                .stream()
                // NOTE here we merge them all under one key
                .map(e -> immutableEntry(e.getKey(),
                    String.join(COMMA, e.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
            p.store(attributesWriter,
                "generated by qa-jgiven-reporter-maven-plugin");
        }
    }

    private String targetNameFor(final ScenarioModel scenarioModel) {
        return MessageFormat.format("{0}-{1}-{2}",
            scenarioModel.getExecutionStatus(),
            scenarioModel.getClassName(),
            scenarioModel.getTestMethodName());
    }
}
