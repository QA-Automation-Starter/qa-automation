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

import static dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter.*;
import static java.util.Objects.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

import dev.aherscu.qa.jgiven.reporter.*;

/**
 * Generates JGiven report in QA format per method with associated attributes.
 * Scans all JGiven report {@code .json} files for specified
 * {@link #referenceTag}. Only reports having such tag are processed. For every
 * scenario method, all its tags are written in an associated "attributes" file.
 * The association is done by file name. These "attributes" can later be used as
 * additional publishing information.
 */
@Mojo(name = "segregated-permethod-report",
    defaultPhase = LifecyclePhase.VERIFY)
public class QaJGivenPerMethodReporterMojo
    extends AbstractQaJgivenReporterMojo {

    @Parameter(defaultValue = DEFAULT_TEMPLATE_RESOURCE)
    protected String templateResource;
    // TODO allow mapping current Execution Set IDs to other ID

    /**
     * JGiven {@code Reference}> tag. Not linked in this module in order to
     * allow faster builds (otherwise the build of this module would depend on
     * {@code qa-jgiven-commons} module).
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
            QaJGivenPerMethodReporter.builder()
                .outputDirectory(outputDirectory)
                .sourceDirectory(sourceDirectory)
                .screenshotScale(screenshotScale)
                .pdf(pdf)
                .datePattern(datePattern)
                .referenceTag(referenceTag)
                .templateResource(templateResource)
                .build()
                .prepare()
                .generate();
        } catch (final Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(
                "Error while trying to generate HTML and/or PDF reports", e);
        }
    }
}
