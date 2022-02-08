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
import static dev.aherscu.qa.tester.utils.WireMockServerUtils.*;
import static javax.ws.rs.client.Entity.*;
import static javax.ws.rs.core.HttpHeaders.*;
import static javax.ws.rs.core.MediaType.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.ws.rs.client.*;

import org.testng.annotations.*;

import com.fasterxml.jackson.databind.node.*;
import com.github.tomakehurst.wiremock.*;

import dev.aherscu.qa.tester.utils.rest.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SelfTest {

    private final WireMockServer wireMockServer = wireMockServerOnDynamicPort();
    private Client               client;

    @Test
    public void shouldReadJsonFromRest() {

        wireMockServer.stubFor(get(urlEqualTo("/some-json"))
            .willReturn(aResponse()
                .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                .withJsonBody(new POJONode(
                    AnObject.builder()
                        .foo("blah")
                        .build()))));

        assertThat(
            client.target(wireMockServer.baseUrl())
                .path("some-json")
                .request()
                .get(AnObject.class),
            is(AnObject.builder()
                .foo("blah")
                .build()));
    }

    @Test
    public void shouldReadTextFromRest() {
        wireMockServer.stubFor(get(urlEqualTo("/some-text"))
            .willReturn(aResponse()
                .withBody("foo")));

        assertThat(
            client.target(wireMockServer.baseUrl())
                .path("some-text")
                .request()
                .get(String.class),
            is("foo"));
    }

    @Test
    public void shouldReadXmlFromRest() {

        wireMockServer.stubFor(get(urlEqualTo("/some-xml"))
            .willReturn(aResponse()
                .withHeader(CONTENT_TYPE, APPLICATION_XML)
                .withBody("<anobject><foo>blah</foo></anobject>")));

        assertThat(
            client.target(wireMockServer.baseUrl())
                .register(AnObjectProvider.class)
                .path("some-xml")
                .request()
                .get(AnObject.class),
            is(AnObject.builder()
                .foo("blah")
                .build()));
    }

    @Test
    public void shouldWriteJsonToRest() {

        wireMockServer.stubFor(post(urlEqualTo("/some-json"))
            .withRequestBody(matchingJsonPath("$.foo"))
            .willReturn(aResponse()
                .withBody("ok")));

        assertThat(
            client.target(wireMockServer.baseUrl())
                .path("some-json")
                .request()
                .post(
                    json(AnObject.builder()
                        .foo("blah")
                        .build()),
                    String.class),
            is("ok"));
    }

    @AfterClass
    protected void afterClassCloseRestClient() {
        client.close();
    }

    @AfterClass(alwaysRun = true)
    protected void afterClassStopMockRestServer() {
        wireMockServer.stop();
        log.debug("stopped wire mock server");
    }

    @BeforeClass
    protected void beforeClassOpenRestClient() {
        client = LoggingClientBuilder.newClient();
    }

    @BeforeClass
    protected void beforeClassStartMockRestServer() {
        wireMockServer.start();
        log.debug("wire mock uri set to {}", wireMockServer.baseUrl());
    }

    @BeforeMethod
    protected void beforeMethodResetWireMock() {
        wireMockServer.resetAll(); // otherwise stubs will accumulate over test
                                   // methods
    }

    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private static final class AnObject {
        public String foo;
    }

    private static final class AnObjectProvider
        extends AbstractJaxbReadableProvider<AnObject> {
        // nothing special
    }
}
