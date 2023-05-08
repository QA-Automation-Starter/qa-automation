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

package dev.aherscu.qa.testrail.reporter;

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static java.lang.Long.*;
import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.io.FilenameUtils.*;

import java.io.*;
import java.util.*;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.jgiven.reporter.*;
import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Adds screenshot manipulation functionality.
 *
 * @see #saveScreenshot
 */
@SuperBuilder(toBuilder = true)
@Slf4j
public class TestRailReportModel extends QaJGivenReportModel<ScenarioModel> {
    public final File            outputDirectory;

    // NOTE: unfortunately cannot attach the screenshot during report generation
    // That would prevent polluting the disk with screenshot files.
    // The TestRail add_attachment_to_result API requires an id to make
    // the attachment. That id is returned by add_result_for_case API, which
    // implies that the report is fully generated...
    // Another unfortunate limitation... Cannot add links to these attachments
    // inside the report, since an attachment_id is made available only after
    // calling add_attachment_to_result, which implies the report is generated.
    public final Mustache.Lambda saveScreenshot = this::saveScreenshot;

    /**
     * To be called from Mustache template for saving screenshots as separate
     * PNG files, in a directory matching the name of the test report file.
     * 
     * <p>
     * These are later picked up by the {@link TestRailReporter} and attached to
     * the test result in TestRail.
     * </p>
     * 
     * @param frag
     *            Mustache fragment to process, assumed to be a screenshot in
     *            Base64 PNG format
     * @param out
     *            Mustache output stream; will be written the screenshot file
     *            name (currently, a hex representation of its contents hash)
     */
    @SneakyThrows
    public void saveScreenshot(final Template.Fragment frag, final Writer out) {
        val screenshotsDirectory = new File(outputDirectory,
            removeExtension(targetReportFile.getName()));
        if (!screenshotsDirectory.exists()) {
            log.trace("creating screenshots directory {}",
                screenshotsDirectory);
            forceMkdir(screenshotsDirectory);
        }
        val screenshotHash = toHexString(frag.execute().hashCode());
        out.write(screenshotHash);
        val screenshotFile =
            new File(screenshotsDirectory, screenshotHash + ".png");
        log.trace("saving screenshot to {}", screenshotFile);
        try (
            val screenshotOutputStream = new FileOutputStream(screenshotFile)) {
            ImageUtils.Pipeline
                .from(new ByteArrayInputStream(Base64
                    .getMimeDecoder()
                    .decode(frag.execute().getBytes(UTF_8))))
                .into(screenshotOutputStream, "png");
        }
    }
}
