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
package dev.aherscu.qa.jgiven.rest.scenarios;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.client.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.rest.model.*;
import dev.aherscu.qa.jgiven.rest.steps.*;
import dev.aherscu.qa.jgiven.rest.tags.*;
import dev.aherscu.qa.testing.utils.assertions.*;
import dev.aherscu.qa.testing.utils.rest.*;

/**
 * Contains REST sample tests just to ensure that the testing infrastructure
 * works as required.
 *
 * @author aherscu
 *
 */
@SelfTest
@RestTest
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE",
    justification = "JGiven framework limitation")
@SuppressWarnings({ "boxing" })
public final class GenericRestTest extends AbstractWireMockTest<RestScenarioType, RestFixtures<?>, RestActions<?>, RestVerifications<?>> {

    private Client client;

    // TODO add a test that extracts data from JSON structure using JSONPath

    /**
     * Should retrieve a JSON field from a REST service
     */
    @Test
    public void shouldRetrieveJsonFieldFromFakeRestService() {
        wireMockServer.stubFor(get(urlEqualTo("/some-id"))
            .willReturn(aResponse()
                .withBody("[{id:1},{id:2}]")));

        given().a_REST_client(client);

        when()
            .connecting_to(wireMockServer.baseUrl())
            .and().appending_path("some-id")
            .and().getting_the_response();

        then().the_response_contents(asList(
            new JsonAssertion<>("$[0].id", equalTo(1)),
            new JsonAssertion<>("$[1].id", equalTo(2))));
    }

    @AfterClass
    private void afterClassCloseRestClient() {
        client.close();
    }

    @BeforeClass
    private void beforeClassOpenRestClient() {
        client = LoggingClientBuilder.newClient();
    }
}
