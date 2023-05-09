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

import static dev.aherscu.qa.tester.utils.ObjectMapperUtils.*;
import static dev.aherscu.qa.tester.utils.UriUtils.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import static org.apache.commons.io.FileUtils.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.*;
import org.testng.xml.*;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.jgiven.reporter.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Per method test reporter uploading results with screenshot attachments to
 * TestRail.
 *
 * @see #with(XmlSuite) support for TestNG Listener configuration
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
@Slf4j
@ToString(callSuper = true)
public class TestRailReporter extends QaJGivenPerMethodReporter {

    private final URI    testRailUrl;
    private final String testRailRunId;

    private static TestRailClient testRailClient(final URI testRailUrl) {
        val testRailClient = new TestRailClient(testRailUrl.toString());
        testRailClient.setUser(usernameFrom(testRailUrl));
        testRailClient.setPassword(passwordFrom(testRailUrl));
        return testRailClient;
    }

    private static Collection<File> listScreenshots(final File directory) {
        return listFiles(directory, new SuffixFileFilter(".png"), null);
    }

    /**
     * Builds a new reporter configured with additional TestNG XML suite
     * parameters:
     * <dl>
     * <dt>testRailRunId</dt>
     * <dd>the TestRail Run to report to</dd>
     * <dt>testRailUrl</dt>
     * <dd>the TestRail location</dd>
     * </dl>
     * 
     * @see QaJGivenPerMethodReporter#with(XmlSuite)
     * @param xmlSuite
     *            TestNG XML suite
     * @return reporter configured
     */
    @Override
    protected TestRailReporter with(final XmlSuite xmlSuite) {
        return ((TestRailReporter) super.with(xmlSuite))
            .toBuilder()
            .testRailRunId(
                requireNonNull(xmlSuite.getParameter("testRailRunId"),
                    "testRailRunId parameter not found in current TestNG XML"))
            .testRailUrl(URI.create(
                requireNonNull(xmlSuite.getParameter("testRailUrl"),
                    "testRailUrl parameter not found in current TestNG XML")))
            .build();
    }

    @Override
    protected Mustache.Compiler compiler() {
        return Mustache.compiler().withEscaper(Escapers.NONE);
    }

    private AttachScreenshotsResponse addAttachmentToResult(
        final String resultId,
        final File screenshot) {
        return fromJson(testRailClient(testRailUrl)
            .sendPost(format("add_attachment_to_result/{0}",
                resultId),
                screenshot.toString())
            .toString(),
            AttachScreenshotsResponse.class);
    }

    private ResultForCaseResponse addResultForCase(
        final ScenarioModel scenarioModel,
        final File reportFile, final String testCaseId) throws IOException {
        return fromJson(testRailClient(testRailUrl)
            .sendPost(format("add_result_for_case/{0}/{1}",
                testRailRunId,
                testCaseId),
                ImmutableMap.builder()
                    .put("status_id",
                        Status.from(scenarioModel.getExecutionStatus()).id)
                    .put("comment",
                        IOUtils.toString(new FileReader(reportFile)))
                    .build())
            .toString(),
            ResultForCaseResponse.class);
    }

    @Override
    protected void reportGenerated(
        final ScenarioModel scenarioModel,
        final File reportFile) {
        super.reportGenerated(scenarioModel, reportFile);

        val testCaseId = readAttributesOf(reportFile)
            // TODO make it configurable
            .get("dev.aherscu.qa.jgiven.commons.tags.Reference");

        try {
            val addResultForCaseResponse =
                addResultForCase(scenarioModel, reportFile, testCaseId);
            log.debug(
                "reported result id {} for case {} on run {} to {}/index.php?/tests/view/{}",
                addResultForCaseResponse.id,
                testCaseId, testRailRunId, testRailUrl,
                addResultForCaseResponse.testId);

            // FIXME for each report only its screenshots must be attached
            // TODO the screenshots must be somehow associated with their report
            listScreenshots(
                new File(outputDirectory, targetNameFor(scenarioModel)))
                .forEach(file -> {
                    log.trace("attaching {}", file);
                    val attachScreenshotsResponse =
                        addAttachmentToResult(addResultForCaseResponse.id,
                            file);
                    log.debug("attached {}", attachScreenshotsResponse.id);
                });

        } catch (final Exception e) {
            log.error("failed to report case {} on run {} -> {}",
                testCaseId, testRailRunId, e.getMessage());
        }
    }

    @Override
    protected TestRailReportModel reportModel(File targetReportFile) {
        return TestRailReportModel.builder()
            .outputDirectory(outputDirectory)
            .targetReportFile(targetReportFile)
            .build();
    }

    @Getter
    @AllArgsConstructor
    private enum Status {
        SUCCESS(1), FAILED(5);

        final int id;

        static Status from(final ExecutionStatus status) {
            return status == ExecutionStatus.SUCCESS ? SUCCESS : FAILED;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ResultForCaseResponse {
        @JsonProperty("id")
        String id;
        @JsonProperty("test_id")
        String testId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AttachScreenshotsResponse {
        @JsonProperty("attachment_id")
        String id;
    }
}
