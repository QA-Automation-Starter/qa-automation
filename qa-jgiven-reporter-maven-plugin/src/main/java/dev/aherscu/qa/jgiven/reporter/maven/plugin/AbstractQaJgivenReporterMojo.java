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

import static dev.aherscu.qa.jgiven.reporter.AbstractQaJgivenReporter.*;

import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;

public abstract class AbstractQaJgivenReporterMojo extends AbstractMojo {
    @Parameter(property = "jgivenreporter.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * Directory where the reports are generated to. Defaults to
     * {@code ${project.build.directory}/jgiven-reports/qa-html}.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/jgiven-reports/qa-html")
    protected File    outputDirectory;

    /**
     * Directory to read the JSON report files from. Defaults to
     * {@code ${project.build.directory}/jgiven-reports/json}.
     */
    @Parameter(defaultValue = "${project.build.directory}/jgiven-reports/json")
    protected File    sourceDirectory;

    /**
     * Whether to generate an aggregated JSON report model for debugging
     * purposes. Defaults to {@code false}.
     */
    @Parameter(defaultValue = "false")
    protected boolean debug;

    @Parameter(defaultValue = DEFAULT_SCREENSHOT_SCALE)
    protected String  screenshotScale;

    @Parameter(defaultValue = DEFAULT_DATE_PATTERN)
    protected String  datePattern;

    /**
     * Whether to generate PDF reports. Defaults to {@code true}.
     */
    @Parameter(defaultValue = "true")
    protected boolean pdf;
}
