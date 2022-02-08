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

import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;

public abstract class AbstractQaJgivenReporterMojo extends AbstractMojo {
    @Parameter(property = "jgivenreporter.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * Directory where the reports are generated to. Defaults to
     * <tt>${project.build.directory}/jgiven-reports/qa-html</tt>.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/jgiven-reports/qa-html")
    protected File    outputDirectory;

    /**
     * Directory to read the JSON report files from. Defaults to
     * <tt>${project.build.directory}/jgiven-reports/json</tt>.
     */
    @Parameter(defaultValue = "${project.build.directory}/jgiven-reports/json")
    protected File    sourceDirectory;

    /**
     * Whether to generate an aggregated JSON report model for debugging
     * purposes. Defaults to <tt>false</tt>.
     */
    @Parameter(defaultValue = "false")
    protected boolean debug;

    @Parameter(defaultValue = "0.25")
    protected double  screenshotScale;

    @Parameter(defaultValue = "yyyy-MMM-dd HH:mm O")
    protected String  datePattern;

    /**
     * Whether to generate PDF reports. Defaults to <tt>true</tt>.
     */
    @Parameter(defaultValue = "true")
    protected boolean pdf;
}
