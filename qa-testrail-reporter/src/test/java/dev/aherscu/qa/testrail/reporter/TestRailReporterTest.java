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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.aherscu.qa.testing.utils.WireMockServerUtils.wireMockServerOnDynamicPort;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.io.FileMatchers.*;

import java.io.*;

import jakarta.ws.rs.core.*;

import org.testng.annotations.*;
import org.testng.xml.*;

import com.github.tomakehurst.wiremock.*;
import com.google.common.collect.*;

import dev.aherscu.qa.testing.utils.rest.*;
import lombok.*;

public class TestRailReporterTest {
    public static final File                  REPORTING_INPUT      = new File(
        "target/test-classes");
    public static final File                  REPORTING_OUTPUT     = new File(
        "target/test-classes/reporting-output");
    public static final File                  REPORTING_ALT_OUTPUT = new File(
        "target/test-classes/reporting-alt-output");
    private final ThreadLocal<WireMockServer> wireMockServer       =
        new ThreadLocal<>();

    @BeforeMethod
    protected void beforeMethodInitiateWireMockServer() {
        // TODO maybe should move into some base class
        // -- see AbstractMockedServiceTest
        // then, should depend on qa-jgiven-commons, making the build slower
        wireMockServer.set(wireMockServerOnDynamicPort());
        wireMockServer.get().start();
        wireMockServer.get().stubFor(
            get(urlEqualTo("/self-test"))
                .willReturn(ok()));
        wireMockServer.get().stubFor(
            post(urlEqualTo("/index.php?/api/v2/add_result_for_case/123/68"))
                .willReturn(okJson("{\"id\":321,\"test_id\":321}")));
        wireMockServer.get().stubFor(
            post(urlEqualTo("/index.php?/api/v2/add_attachment_to_result/321"))
                .willReturn(okJson("{\"attachment_id\":444}")));
    }

    @AfterMethod(alwaysRun = true)
    protected void afterMethodStopWireMockServer() {
        wireMockServer.get().stop();
    }

    @Test
    public void selfTest() {
        try (val request = LoggingClientBuilder
            .newClient()
            .target(wireMockServer.get().baseUrl())
            .path("self-test")
            .request()
            .get()) {
            assertThat(request.getStatusInfo().getFamily(),
                is(Response.Status.Family.SUCCESSFUL));
        }
        wireMockServer.get().verify(getRequestedFor(urlEqualTo("/self-test")));
    }

    @Test
    @SneakyThrows
    public void shouldGenerateAltReport() {
        val xmlSuite = new XmlSuite();
        xmlSuite.setParameters(ImmutableMap.<String, String> builder()
            .put("templateResourceTestRailReporter",
                "/alt-permethod-reporter.testrail")
            .put("testRailUrl", wireMockServer.get().baseUrl())
            .put("testRailRunId", "123")
            .build());
        new TestRailReporter()
            .toBuilder()
            .sourceDirectory(REPORTING_INPUT)
            .outputDirectory(REPORTING_ALT_OUTPUT)
            .build()
            // .with(xmlSuite) this is already called by generateReport
            .generateReport(
                singletonList(xmlSuite),
                emptyList(),
                null);

        // ISSUE Hamcrest failure description does not include the verified file
        assertThat(REPORTING_ALT_OUTPUT, is(anExistingDirectory()));
        assertThat(new File(REPORTING_ALT_OUTPUT,
            "SUCCESS-dev.aherscu.qa.testing.example.scenarios.tutorial3.TestingWebWithJGiven-shouldFind.testrail"),
            is(anExistingFile()));

        wireMockServer.get()
            .verify(postRequestedFor(
                urlEqualTo("/index.php?/api/v2/add_result_for_case/123/68")));
        wireMockServer.get()
            .verify(2, postRequestedFor(
                urlEqualTo(
                    "/index.php?/api/v2/add_attachment_to_result/321")));
    }

    @Test
    @SneakyThrows
    public void shouldGenerateReport() {
        val xmlSuite = new XmlSuite();
        xmlSuite.setParameters(ImmutableMap.<String, String> builder()
            // default is used if not specified
            // .put("templateResourceTestRailReporter",
            // "/permethod-reporter.testrail")
            .put("testRailUrl", wireMockServer.get().baseUrl())
            .put("testRailRunId", "123")
            .build());
        new TestRailReporter()
            .toBuilder()
            .sourceDirectory(REPORTING_INPUT)
            .outputDirectory(REPORTING_OUTPUT)
            .build()
            // .with(xmlSuite) this is already called by generateReport
            .generateReport(
                singletonList(xmlSuite),
                emptyList(),
                null);

        // ISSUE Hamcrest failure description does not include the verified file
        assertThat(REPORTING_OUTPUT, is(anExistingDirectory()));
        assertThat(new File(REPORTING_OUTPUT,
            "SUCCESS-dev.aherscu.qa.testing.example.scenarios.tutorial3.TestingWebWithJGiven-shouldFind.testrail"),
            is(anExistingFile()));

        wireMockServer.get()
            .verify(postRequestedFor(
                urlEqualTo("/index.php?/api/v2/add_result_for_case/123/68")));
        wireMockServer.get()
            .verify(2, postRequestedFor(
                urlEqualTo(
                    "/index.php?/api/v2/add_attachment_to_result/321")));
    }
}
