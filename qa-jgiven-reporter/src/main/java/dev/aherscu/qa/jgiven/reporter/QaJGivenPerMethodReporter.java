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

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Multimaps.*;
import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.TemplateUtils.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.FilenameUtils.*;

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

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder
@Slf4j
public class QaJGivenPerMethodReporter
    extends AbstractQaJgivenReporter<QaJGivenPerMethodReporter>
    implements IReporter {

    public static final String DEFAULT_TEMPLATE =
        "/qa-jgiven-permethod-reporter.html";

    /**
     * Meant to called by TestNG during listener construction. Presets default
     * configuration values which might be overridden via TestNG parameters.
     *
     * @see #generateReport(List, List, String)
     */
    public QaJGivenPerMethodReporter() {
        super();
        referenceTag = DEFAULT_REFERENCE_TAG;
        sourceDirectory = Config.config().getReportDir().get();
        outputDirectory = new File(sourceDirectory, "qa-html");
        screenshotScale = DEFAULT_SCREENSHOT_SCALE;
        datePattern = DEFAULT_DATE_PATTERN;
        templateResource = DEFAULT_TEMPLATE;
    }

    @SneakyThrows
    public void generate() {
        log.info("source directory {}", sourceDirectory);
        log.info("output directory {}", outputDirectory);
        log.info("screenshot scale {}", screenshotScale);

        forceMkdir(outputDirectory);

        val template = using(Mustache.compiler()).loadFrom(templateResource);

        listFiles(sourceDirectory, new SuffixFileFilter(".json"), null)
            .parallelStream()
            .peek(reportModelFile -> log
                .debug("reading " + reportModelFile.getName()))
            .flatMap(reportModelFile -> new ReportModelFileReader()
                .apply(reportModelFile).model
                    .getScenarios().stream()
                    .filter(scenarioModel -> scenarioModel.getTagIds().stream()
                        .anyMatch(tagId -> tagId.contains(referenceTag))))
            // DELETEME following two lines seeem redundant
            .collect(toCollection(LinkedList::new)).parallelStream()
            .peek(scenarioModel -> log
                .debug("processing " + targetNameFor(scenarioModel)))
            .forEach(Unchecked.consumer(scenarioModel -> {
                val reportFile = new File(outputDirectory,
                    targetNameFor(scenarioModel)
                        + getExtension(templateResource));
                try (val reportWriter = fileWriter(reportFile)) {
                    template.execute(QaJGivenReportModel.builder()
                        .jgivenReport(scenarioModel)
                        .screenshotScale(screenshotScale)
                        .datePattern(datePattern).build(), reportWriter);
                    applyAttributesFor(scenarioModel, reportFile);
                }
            }));
    }

    @Override
    public void generateReport(final List<XmlSuite> xmlSuites,
        final List<ISuite> suites, final String outputDirectory) {
        xmlSuites.forEach(xmlSuite -> log.info("xml suite {}", xmlSuite));
        // ISSUE: should be empty for xml driven invocations (?)
        // if yes, then should throw an unsupported exception
        suites.forEach(suite -> log.info("suite {}", suite.getName()));

        xmlSuites.forEach(xmlSuite -> from(xmlSuite).generate());
    }

    @SneakyThrows
    private void applyAttributesFor(
        final ScenarioModel scenarioModel,
        final File reportFile) {
        log.info("setting attributes for " + reportFile.getName());

        try (val attributesWriter = fileWriter(
            new File(reportFile.getAbsolutePath() + ".attributes"))) {
            val p = new Properties();
            p.putAll(scenarioModel.getTagIds().stream()
                // TODO apply the mapping here
                .map(tag -> immutableEntry(substringBefore(tag, DASH),
                    substringAfter(tag, DASH)))
                // NOTE there might be multiple
                // DeviceName/PlatformName/PlatformVersion tags
                .collect(toMultimap(Map.Entry::getKey, Map.Entry::getValue,
                    MultimapBuilder.hashKeys().arrayListValues()::build))
                .asMap().entrySet().stream()
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
            scenarioModel.getExecutionStatus(), scenarioModel.getClassName(),
            scenarioModel.getTestMethodName());
    }
}
