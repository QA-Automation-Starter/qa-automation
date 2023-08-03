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
package dev.aherscu.qa.testing.example.scenarios.tutorial6;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.net.*;

import javax.ws.rs.client.*;

import org.mockserver.model.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.rest.model.*;
import dev.aherscu.qa.jgiven.rest.steps.*;
import dev.aherscu.qa.jgiven.rest.tags.*;
import dev.aherscu.qa.testing.example.*;
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
public final class GenericMockServerRestTest extends
    AbstractMockServerTest<RestScenarioType, RestFixtures<?>, RestActions<?>, RestVerifications<?>> {

    private Client client;

    /**
     * Should retrieve a JSON field from a REST service
     */
    @Test
    @Reference("159")
    public void shouldRetrieveJsonFieldFromFakeRestService() {
        given()
            .a_REST_client(client);

        when()
            .connecting_to(
                URI.create("http://localhost:" + mockServer.getPort()))
            .and().appending_path("some-id")
            .and().getting_the_response();

        then()
            .the_response_contents(asList(
                new JsonAssertion<>("$[0].id", greaterThan(0)),
                new JsonAssertion<>("$[1].id", equalTo(2))));
    }

    @AfterClass
    private void afterClassCloseRestClient() {
        client.close();
    }

    @BeforeClass
    private void beforeClassOpenRestClient() {
        mockServer
            .when(request()
                .withMethod("GET").withPath("/some-id"))
            .respond(response()
                .withBody("[{id:1},{id:2},{id:3}]",
                    MediaType.JSON_UTF_8));

        client = LoggingClientBuilder.newClient();
    }
}
