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
import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.testing.*;
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
public class QaJGivenReporterMavenPluginTest extends AbstractMojoTestCase {

    private static final File   OUT_DIR       =
        new File(getBasedir(), "target/test-classes");
    private static final String PLUGIN_CONFIG = "plugin-config.xml";

    private static File actualReports(final String relativePath) {
        return new File(outdir("jgiven-reports/qa-html"), relativePath);
    }

    private static String attributesOf(final String fileName) {
        return fileName + ".attributes";
    }

    private static File expectedReports(final String relativePath) {
        return new File(outdir("expected-output"), relativePath);
    }

    private static File outdir(final String relativePath) {
        return new File(OUT_DIR, relativePath);
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
        @SuppressWarnings("CastToConcreteClass")
        val mojo = (QaJGivenReporterMojo) lookupMojo(
            "report",
            outdir(PLUGIN_CONFIG));

        mojo.execute();

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
        @SuppressWarnings("CastToConcreteClass")
        val mojo =
            (QaJGivenPerMethodReporterMojo) lookupMojo(
                "segregated-permethod-report",
                outdir(PLUGIN_CONFIG));

        mojo.execute();

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
        @SuppressWarnings("CastToConcreteClass")
        val mojo =
            (QaJGivenPerClassReporterMojo) lookupMojo(
                "segregated-report",
                outdir(PLUGIN_CONFIG));

        mojo.execute();

        val softly = new SoftAssertions();
        for (val fileName : Collections.singletonList(
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
