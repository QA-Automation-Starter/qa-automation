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

import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;
import java.util.*;

import org.apache.commons.io.filefilter.*;
import org.testng.*;
import org.testng.xml.*;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.impl.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Base functionality and defaults for all kinds of reporters.
 * <p>
 * Can be invoked as a TestNG Reporter, hence supports {@link IReporter} by
 * implementing its {@link #generateReport(List, List, String)} and provides a
 * no-args constructor. It also can be invoked from other workflow engine, such
 * as from a Maven plugin, which should just call {@link #generate()} -- see the
 * qa-testrail-reporter and qa-jgiven-reporter-maven-plugin sibling modules.
 * </p>
 * <p>
 * Implementors are required to specify the {@link #generate()} method.
 * </p>
 *
 * @see #with(XmlSuite) TestNG Reporter configuration
 *
 * @param <M>
 *            one of JGiven's report models: {@link CompleteReportModel}, *
 *            {@link ScenarioModel}, or {@link ReportModelFile}
 * @param <T>
 *            specific type of reporter
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
@Slf4j
@ToString
public abstract class AbstractQaJgivenReporter<M, T extends AbstractQaJgivenReporter<?, ?>>
    implements IReporter {

    public static final String DEFAULT_REFERENCE_TAG    = "Reference";
    public static final String DEFAULT_SCREENSHOT_SCALE = "0.2";
    public static final String DEFAULT_DATE_PATTERN     = "yyyy-MMM-dd HH:mm O";

    @Builder.Default
    protected final File       sourceDirectory          =
        // FIXME Warning:(72, 40) 'Optional.get()' without 'isPresent()' check
        Config.config().getReportDir().get();
    @Builder.Default
    protected final File       outputDirectory          =
        new File(Config.config().getReportDir().get(), "qa-html");
    @Builder.Default
    protected final boolean    debug                    = false;
    @Builder.Default
    protected final String     screenshotScale          =
        DEFAULT_SCREENSHOT_SCALE;
    @Builder.Default
    protected final String     datePattern              = DEFAULT_DATE_PATTERN;
    @Builder.Default
    protected final boolean    pdf                      = false;
    @Builder.Default
    protected final String     referenceTag             = DEFAULT_REFERENCE_TAG;
    protected final String     templateResource;

    // see
    // https://stackoverflow.com/questions/61633821/using-lombok-superbuilder-annotation-with-tobuilder-on-an-abstract-class
    public abstract AbstractQaJgivenReporterBuilder<M, T, ?, ?> toBuilder();

    protected Mustache.Compiler compiler() {
        return Mustache.compiler();
    }

    protected QaJGivenReportModel<M> reportModel(final File targetReportFile) {
        return QaJGivenReportModel.<M> builder()
            .targetReportFile(targetReportFile)
            .build();
    }

    /**
     * @param xmlSuites
     *            The list of {@code XmlSuite}
     * @param suites
     *            The list of {@code ISuite}
     * @param outputDirectory
     *            The output directory is ignored, since it is specified by
     *            JGiven reporter infrastructure
     */
    @Override
    public void generateReport(
        final List<XmlSuite> xmlSuites,
        final List<ISuite> suites,
        @SuppressWarnings("hiding") final String outputDirectory) {
        xmlSuites.forEach(xmlSuite -> log.info("xml suite {}", xmlSuite));
        // ISSUE: should be empty for xml driven invocations (?)
        // if yes, then should throw an unsupported exception
        suites.forEach(suite -> log.info("suite {}", suite.getName()));

        xmlSuites.forEach(xmlSuite -> with(xmlSuite).prepare().generate());
    }

    /**
     * Builds a new reporter configured per following TestNG XML suite
     * parameters:
     * <dl>
     * <dt>referenceTag</dt>
     * <dd>the reference tag identifier, or {@link #DEFAULT_REFERENCE_TAG}</dd>
     * <dt>screenshotScale</dt>
     * <dd>the screenshot scale to apply when embedding files into reports, or
     * {@link #DEFAULT_SCREENSHOT_SCALE}</dd>
     * <dt>datePattern</dt>
     * <dd>the date pattern to use for presenting dates, or
     * {@link #DEFAULT_DATE_PATTERN}</dd>
     * <dt>templateResourceXXX</dt>
     * <dd>the template resource file name to apply; the {@code XXX} is the
     * concrete reporter implementation name (class)</dd>
     * </dl>
     *
     * @param xmlSuite
     *            TestNG XML suite
     * @return reporter configured
     */
    protected AbstractQaJgivenReporter<M, T> with(final XmlSuite xmlSuite) {
        return this
            // NOTE see
            // https://stackoverflow.com/questions/56761054/lombok-wither-with-inheritance-super-sub-classes
            .toBuilder()
            .referenceTag(defaultIfBlank(xmlSuite.getParameter("referenceTag"),
                referenceTag))
            .screenshotScale(defaultIfBlank(
                xmlSuite.getParameter("screenshotScale"), screenshotScale))
            .datePattern(defaultIfBlank(xmlSuite.getParameter("datePattern"),
                datePattern))
            .templateResource(templateResourceParamFrom(xmlSuite,
                templateResource))
            .build();
    }

    @SneakyThrows
    public AbstractQaJgivenReporter<M, T> prepare() {
        log.info("configuration {}", this);

        forceMkdir(outputDirectory);

        return this;
    }

    protected String templateResourceParamFrom(
        final XmlSuite xmlSuite,
        final String defaultTemplateResource) {
        return defaultIfBlank(
            xmlSuite.getParameter(
                "templateResource" + this.getClass().getSimpleName()),
            defaultTemplateResource);
    }

    abstract public void generate();

    protected File reportFile(final File reportModelFile,
        final String extension) {
        return new File(outputDirectory, reportModelFile.getName() + extension);
    }

    protected Collection<File> listJGivenReports() {
        return listFiles(sourceDirectory, new SuffixFileFilter(".json"), null);
    }

    protected Template template() {
        return TemplateUtils.using(compiler()).loadFrom(templateResource);
    }
}
