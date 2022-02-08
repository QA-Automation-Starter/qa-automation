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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static dev.aherscu.qa.orcanos.publisher.maven.plugin.OrcanosPublisherMojo.*;
import static dev.aherscu.qa.orcanos.publisher.maven.plugin.model.ExecutionSet.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.WireMockServerUtils.*;
import static dev.aherscu.qa.tester.utils.rest.LoggingClientBuilder.*;

import java.io.*;
import java.net.*;

import javax.ws.rs.client.*;

import org.apache.maven.plugin.testing.*;

import com.github.tomakehurst.wiremock.*;
import com.github.tomakehurst.wiremock.matching.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Simulates a Maven plugin running the "publish" goal against a stubbed Orcanos
 * instance.
 */
@Slf4j
public class OrcanosPublisherMojoTest extends AbstractMojoTestCase {
    private static final String  DEFAULT_EXECUTION_SET_ID = "1";
    private static final String  EXPECTED_PROJECT_ID      = "12345";
    private static final String  IOS_EXECUTION_SET_ID     = "2";

    private static final File    PLUGIN_CONFIG            =
        new File(getBasedir(), "target/test-classes/plugin-config.xml");

    private final WireMockServer wireMockServer;
    private final Client         restClient;
    private WebTarget            wireMockedTarget;
    private OrcanosPublisherMojo mojo;

    public OrcanosPublisherMojoTest() {
        wireMockServer = wireMockServerOnDynamicPort();
        restClient = newClient();
    }

    /**
     * Tests existence of plugin configuration manifest.
     */
    @SuppressWarnings("static-method")
    public void testConfigurationExistence() {
        assertTrue(PLUGIN_CONFIG.exists());
    }

    public void testSelfRequestOk() {
        try (val r = wireMockedTarget.path("/self-test").request().get()) {
            wireMockServer.verify(getRequestedFor(urlEqualTo("/self-test")));
        }
    }

    public void testSelfResponseOk() {
        assertEquals(wireMockedTarget
            .path("/self-test")
            .request()
            .get()
            .readEntity(String.class),
            "ok");
    }

    @SneakyThrows
    public void testPublishGoal() {
        mojo.execute();
        assertRequestedExecutionRunDetailsForExecutionId(equalTo(
            DEFAULT_EXECUTION_SET_ID));
        wireMockServer.verify(postRequestedFor(
            urlPathEqualTo(SLASH + UPLOAD_ATTACHMENT))
                .withQueryParam(PROJECT_ID, equalTo(EXPECTED_PROJECT_ID)));
        wireMockServer.verify(postRequestedFor(
            urlEqualTo(SLASH + RECORD_EXECUTION_RESULT)));

        // TODO ensure ordering
        // https://stackoverflow.com/questions/55476553/request-order-verification-with-wiremock
        // TODO add verifications for other attributes/parameters/fields/etc.
    }

    @SneakyThrows
    public void testPublishGoalWithMissingTag() {
        mojo.tag = "no-such-tag";
        mojo.execute();
        assertRequestedExecutionRunDetailsForExecutionId(equalTo(
            DEFAULT_EXECUTION_SET_ID));
    }

    @SneakyThrows
    public void testPublishGoalWithTaggedReference() {
        mojo.tag = "ios";
        mojo.execute();
        assertRequestedExecutionRunDetailsForExecutionId(equalTo(
            IOS_EXECUTION_SET_ID));
    }

    @SuppressWarnings("CastToConcreteClass")
    protected void setUp() throws Exception {
        super.setUp();
        wireMockServer.start();
        wireMockedTarget = restClient.target(wireMockServer.baseUrl());
        wireMockServer.listAllStubMappings().getMappings()
            .forEach(mapping -> log.trace(mapping.toString()));

        mojo = (OrcanosPublisherMojo) lookupMojo("publish", PLUGIN_CONFIG);
        mojo.orcanosUrl = new URI(wireMockServer.baseUrl());
        mojo.reportsDirectory =
            new File("target/test-classes/jgiven-report-samples/stubs-qa-html");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        wireMockServer.stop();
    }

    private void assertRequestedExecutionRunDetailsForExecutionId(
        final StringValuePattern withPattern) {
        wireMockServer.verify(
            postRequestedFor(urlEqualTo(SLASH + GET_EXECUTION_RUN_DETAILS))
                .withRequestBody(
                    matchingJsonPath("$." + EXECUTION_SET_ID,
                        withPattern)));
    }
}
