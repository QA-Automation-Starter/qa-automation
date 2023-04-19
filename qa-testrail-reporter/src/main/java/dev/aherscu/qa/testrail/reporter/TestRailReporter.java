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

import static com.jayway.jsonpath.JsonPath.*;
import static dev.aherscu.qa.tester.utils.UriUtils.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;

import java.io.*;
import java.net.*;

import org.apache.commons.io.*;
import org.testng.xml.*;

import com.google.common.collect.*;
import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.jgiven.reporter.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder(toBuilder = true)
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

    @Override
    protected void reportGenerated(
        final ScenarioModel scenarioModel,
        final File reportFile) {
        super.reportGenerated(scenarioModel, reportFile);

        val testCaseId = readAttributesOf(reportFile)
            // TODO make it configurable
            .get("dev.aherscu.qa.jgiven.commons.tags.Reference");

        try {
            val response = testRailClient(testRailUrl)
                .sendPost(format("add_result_for_case/{0}/{1}",
                    testRailRunId,
                    testCaseId),
                    ImmutableMap.builder()
                        .put("status_id",
                            Status.from(scenarioModel.getExecutionStatus()).id)
                        .put("comment",
                            IOUtils.toString(new FileReader(reportFile)))
                        .build());

            val parsedResponse = parse(response);
            log.debug(
                "reported result id {} for case {} on run {} to {}/index.php?/tests/view/{}",
                parsedResponse.read("$.id").toString(),
                testCaseId, testRailRunId, testRailUrl,
                parsedResponse.read("$.test_id").toString());

        } catch (final Exception e) {
            log.error(
                "failed to report case {} on run {} -> {}",
                testCaseId, testRailRunId, e.getMessage());
        }
    }

    @Override
    protected TestRailReportModel reportModel() {
        log.trace("using the TestRail report model");
        return TestRailReportModel.builder().build();
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
}
