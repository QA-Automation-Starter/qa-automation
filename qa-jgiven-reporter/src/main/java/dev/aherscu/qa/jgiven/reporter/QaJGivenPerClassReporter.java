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

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static org.apache.commons.io.FilenameUtils.*;
import static org.xhtmlrenderer.simple.PDFRenderer.*;

import com.tngtech.jgiven.report.json.*;
import com.tngtech.jgiven.report.model.*;

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Per test class reporter.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
@Slf4j
@ToString(callSuper = true)
public class QaJGivenPerClassReporter
    extends
    AbstractQaJgivenReporter<ReportModelFile, QaJGivenPerClassReporter> {

    public static final String DEFAULT_TEMPLATE_RESOURCE =
        "/qa-jgiven-perclass-reporter.html";

    /**
     * Generates a report for each test class. It is assumed that JGiven
     * generates a JSON file for each test class.
     */
    @Override
    @SneakyThrows
    public void generate() {
        for (val reportModelFile : listJGivenReports()) {

            log.debug("reading " + reportModelFile);
            val targetReportFile = reportFile(reportModelFile,
                EXTENSION_SEPARATOR_STR + getExtension(templateResource));
            try (val reportWriter = fileWriter(targetReportFile)) {
                template()
                    .execute(reportModel(targetReportFile)
                        .toBuilder()
                        .jgivenReport(new ReportModelFileReader()
                            .apply(reportModelFile))
                        .screenshotScale(screenshotScale)
                        .datePattern(datePattern)
                        .build(),
                        reportWriter);
            }

            // FIXME should work only with html reports
            if (pdf) {
                renderToPDF(
                    reportFile(reportModelFile, ".html"),
                    reportFile(reportModelFile, ".pdf")
                        .getAbsolutePath());
            }
        }
    }
}
