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

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static org.xhtmlrenderer.simple.PDFRenderer.*;

import java.io.*;

import org.apache.commons.io.filefilter.*;

import com.itextpdf.text.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.json.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder
@Slf4j
public class QaJGivenPerClassReporter
    extends AbstractQaJgivenReporter<QaJGivenPerClassReporter> {

    public void generate() throws IOException, DocumentException {
        forceMkdir(outputDirectory);

        val template = TemplateUtils
            .using(Mustache.compiler())
            .loadFrom("/qa-jgiven-perclass-reporter.html");

        for (val reportModelFile : listFiles(
            sourceDirectory, new SuffixFileFilter(".json"), null)) {

            log.debug("reading " + reportModelFile);
            try (val reportWriter = fileWriter(
                reportFile(reportModelFile, ".html"))) {
                template.execute(QaJGivenReportModel.builder()
                    .jgivenReport(new ReportModelFileReader()
                        .apply(reportModelFile))
                    .screenshotScale(screenshotScale)
                    .datePattern(datePattern)
                    .build(),
                    reportWriter);
            }

            if (pdf) {
                renderToPDF(
                    reportFile(reportModelFile, ".html"),
                    reportFile(reportModelFile, ".pdf")
                        .getAbsolutePath());
            }
        }
    }

    private File reportFile(
        final File reportModelFile,
        final String extension) {
        return new File(outputDirectory,
            reportModelFile.getName() + extension);
    }
}
