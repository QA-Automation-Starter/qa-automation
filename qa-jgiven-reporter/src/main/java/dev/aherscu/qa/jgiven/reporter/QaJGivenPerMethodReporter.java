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

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Multimaps.*;
import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;
import org.jooq.lambda.*;
import org.testng.*;
import org.testng.xml.*;

import com.google.common.collect.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.impl.*;
import com.tngtech.jgiven.report.json.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class QaJGivenPerMethodReporter extends AbstractQaJgivenReporter
    implements IReporter {
    public String referenceTag;

    @Builder
    public QaJGivenPerMethodReporter(
        File outputDirectory,
        File sourceDirectory,
        boolean debug,
        double screenshotScale,
        String datePattern,
        boolean pdf, String referenceTag) {
        super(outputDirectory, sourceDirectory, debug, screenshotScale,
            datePattern, pdf);
        this.referenceTag = referenceTag;
    }

    public QaJGivenPerMethodReporter() {
        // just to make testng happy
    }

    @SneakyThrows
    @Override
    public void generateReport(
        final List<XmlSuite> xmlSuites,
        final List<ISuite> suites,
        final String outputDirectory) {
        log.debug("testng output directory {}", outputDirectory);
        xmlSuites.forEach(xmlSuite -> log.debug("xml suite {}", xmlSuite));
        suites.forEach(suite -> log.debug("suite {}", suite.getName()));
        log.debug("jgiven report dir {}", Config.config().getReportDir());

        // FIXME quick and dirty solution
        // the long-run solution should read these from testng descriptor
        this.referenceTag = "Reference";
        this.sourceDirectory = Config.config().getReportDir().get();
        this.outputDirectory = new File(Config.config().getReportDir().get(),
            "qa-html");
        this.screenshotScale = 0.2;
        this.datePattern = "yyyy-MMM-dd HH:mm O";

        this.generate();
    }

    public void generate() throws IOException {
        forceMkdir(outputDirectory);

        val template = TemplateUtils
            .using(Mustache.compiler())
            .loadFrom("/qa-jgiven-permethod-reporter.html");

        listFiles(sourceDirectory,
            new SuffixFileFilter(".json"), null)
                .parallelStream()
                .peek(reportModelFile -> log
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
                .peek(scenarioModel -> log
                    .debug("processing " + targetNameFor(scenarioModel)))
                .forEach(Unchecked.consumer(
                    scenarioModel -> {
                        val reportFile = new File(outputDirectory,
                            targetNameFor(scenarioModel) + ".html");
                        try (val reportWriter = fileWriter(reportFile)) {
                            template.execute(
                                QaJGivenReportModel.builder()
                                    .jgivenReport(scenarioModel)
                                    .screenshotScale(screenshotScale)
                                    .datePattern(datePattern)
                                    .build(),
                                reportWriter);
                            applyAttributesFor(scenarioModel, reportFile);
                        }
                    }));
    }

    @SneakyThrows
    private void applyAttributesFor(
        final ScenarioModel scenarioModel,
        final File reportFile) {
        log.info("setting attributes for " + reportFile.getName());

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
