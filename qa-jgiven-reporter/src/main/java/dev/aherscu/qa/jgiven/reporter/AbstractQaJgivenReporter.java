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

import static java.util.Objects.*;
import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;
import org.testng.*;
import org.testng.xml.*;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.impl.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder
@Slf4j
@ToString
public abstract class AbstractQaJgivenReporter<M, T extends AbstractQaJgivenReporter<?, ?>>
    implements IReporter {
    public static final String DEFAULT_REFERENCE_TAG    = "Reference";
    public static final String DEFAULT_SCREENSHOT_SCALE = "0.2";
    public static final String DEFAULT_DATE_PATTERN     = "yyyy-MMM-dd HH:mm O";
    protected File             outputDirectory;
    protected File             sourceDirectory;
    protected boolean          debug;
    protected String           screenshotScale;
    protected String           datePattern;
    protected boolean          pdf;
    protected String           referenceTag;
    protected String           templateResource;
    private Template           template;

    protected AbstractQaJgivenReporter() {
        screenshotScale = DEFAULT_SCREENSHOT_SCALE;
        datePattern = DEFAULT_DATE_PATTERN;
        referenceTag = DEFAULT_REFERENCE_TAG;
        sourceDirectory = Config.config().getReportDir().get();
        outputDirectory = new File(sourceDirectory, "qa-html");
    }

    protected T from(XmlSuite xmlSuite) {
        referenceTag =
            defaultIfBlank(xmlSuite.getParameter("referenceTag"),
                referenceTag);
        screenshotScale =
            defaultIfBlank(xmlSuite.getParameter("screenshotScale"),
                screenshotScale);
        datePattern =
            defaultIfBlank(xmlSuite.getParameter("datePattern"),
                datePattern);
        templateResource =
            defaultIfBlank(xmlSuite.getParameter(
                "templateResource" + this.getClass().getSimpleName()),
                templateResource);
        return (T) this;
    }

    protected Mustache.Compiler compiler() {
        return Mustache.compiler();
    }

    protected QaJGivenReportModel<M> reportModel() {
        return QaJGivenReportModel.<M> builder()
            .screenshotScale(screenshotScale)
            .datePattern(datePattern)
            .sourceDirectory(sourceDirectory)
            .outputDirectory(outputDirectory)
            .build();
    }

    public void generateReport(
        final List<XmlSuite> xmlSuites,
        final List<ISuite> suites,
        final String outputDirectory) {
        xmlSuites.forEach(
            xmlSuite -> log.info("xml suite {}", xmlSuite));
        // ISSUE: should be empty for xml driven invocations (?)
        // if yes, then should throw an unsupported exception
        suites.forEach(
            suite -> log.info("suite {}", suite.getName()));

        xmlSuites.forEach(xmlSuite -> from(xmlSuite).prepare().generate());
    }

    @SneakyThrows
    public T prepare() {
        log.info("configuration {}", this);

        forceMkdir(outputDirectory);

        return (T) this;
    }

    abstract public void generate();

    protected Template template() {
        return isNull(template)
            ? template = TemplateUtils
                .using(compiler())
                .loadFrom(templateResource)
            : template;
    }

    protected File reportFile(
        final File reportModelFile,
        final String extension) {
        return new File(outputDirectory,
            reportModelFile.getName() + extension);
    }

    protected Collection<File> listJGivenReports() {
        return listFiles(sourceDirectory, new SuffixFileFilter(".json"), null);
    }
}
