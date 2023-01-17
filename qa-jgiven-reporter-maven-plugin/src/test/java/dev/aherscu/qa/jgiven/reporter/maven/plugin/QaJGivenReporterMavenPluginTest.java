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

import static dev.aherscu.qa.tester.utils.IOUtilsExtensions.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.io.*;

import org.assertj.core.api.*;

import lombok.*;

/**
 * Simulates a real Maven plugin running the "report" and "segregated-report"
 * goals. The generated reports should be verified manually.
 * 
 * <p>
 * This is a JUnit test, not a TestNG, hence cannot be configured via the *
 * <tt>testng.xml</tt> file.
 * </p>
 * 
 * @author Adrian Herscu
 *
 */
public class QaJGivenReporterMavenPluginTest
    extends BetterAbstractMojoTestCase {

    // ISSUE these tests are sensible to formatting changes
    // One way to deal with this is to tidy these HTML files before comparing
    // them.
    // Tidying can be achieved with JSOUP library.
    // However, AssertJ does not support line-by-line comparison, yet;
    // see https://github.com/assertj/assertj/issues/2922

    private static File actualReports(final String relativePath) {
        return new File(outdir("target/test-classes/jgiven-reports/qa-html"),
            relativePath);
    }

    private static String attributesOf(final String fileName) {
        return fileName + ".attributes";
    }

    private static File expectedReports(final String relativePath) {
        return new File(outdir("expected-output"), relativePath);
    }

    /**
     * Tests existence of plugin configuration manifest.
     */
    @SuppressWarnings("static-method")
    public void testConfigurationExistence() {
        assertTrue(outdir(PLUGIN_CONFIG).exists());
    }

    /**
     * Tests execution of report goal.
     */
    @SneakyThrows
    public void testReportGoal() {
        lookupConfiguredMojo("report")
            .execute();

        assertThat(actualReports("qa-jgiven-reporter.html"))
            .usingCharset(UTF_8)
            .hasSameContentAs(expectedReports("qa-jgiven-reporter.html"),
                UTF_8);
    }

    /**
     * Tests execution of report goal.
     */
    @SneakyThrows
    public void testSegregatedPerMethodReportGoal() {
        lookupConfiguredMojo("segregated-permethod-report")
            .execute();

        val softly = new SoftAssertions();
        for (val fileName : asList(
            "SUCCESS-dev.aherscu.qa.testing.scenarios.experimental.JGivenSelfTests-shouldBeOnLoginScreen.html",
            "SUCCESS-dev.aherscu.qa.testing.scenarios.experimental.JGivenSelfTests-shouldLaunch.html")) {
            softly.assertThat(actualReports(fileName))
                .usingCharset(UTF_8)
                .hasSameContentAs(expectedReports(fileName), UTF_8);

            softly.assertThat(propertiesFrom(
                actualReports(attributesOf(fileName))))
                .isEqualTo(propertiesFrom(
                    expectedReports(attributesOf(fileName))));
        }
        softly.assertAll();
    }

    /**
     * Tests execution of report goal.
     */
    @SneakyThrows
    public void testSegregatedReportGoal() {
        lookupConfiguredMojo("segregated-report")
            .execute();

        val softly = new SoftAssertions();
        for (val fileName : singletonList(
            "dev.aherscu.qa.testing.scenarios.experimental.JGivenSelfTests.json.html")) {
            softly.assertThat(actualReports(fileName))
                .usingCharset(UTF_8)
                .hasSameContentAs(expectedReports(fileName), UTF_8);
        }
        softly.assertAll();
    }

    @Override
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // required per
        // https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html
        super.tearDown();
    }
}
