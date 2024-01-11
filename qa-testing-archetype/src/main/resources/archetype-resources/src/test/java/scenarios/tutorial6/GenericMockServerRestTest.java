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
package ${package}.scenarios.tutorial6;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static jakarta.ws.rs.core.Response.Status.Family.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.mockserver.matchers.Times.*;
import static org.mockserver.model.HttpError.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;
import static org.mockserver.model.MediaType.*;

import java.util.*;

import org.mockserver.mock.*;
import org.mockserver.model.*;
import org.mockserver.verify.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.rest.model.*;
import dev.aherscu.qa.jgiven.rest.steps.*;
import dev.aherscu.qa.jgiven.rest.tags.*;
import dev.aherscu.qa.testing.utils.assertions.*;
import dev.aherscu.qa.testing.utils.rest.*;
import ${package}.*;
import jakarta.ws.rs.client.*;
import lombok.*;

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

    private static ExpectationId[] as(final Expectation[] expectations) {
        return Arrays.stream(expectations)
            .map(Expectation::getId)
            .map(ExpectationId::expectationId)
            .toArray(ExpectationId[]::new);
    }

    @Test
    @Reference("159")
    @SneakyThrows
    public void shouldVerifyAccessByDefinition() {
        mockServer // GET is implied
            .when(request().withPath("/some-id"))
            .respond(response()
                .withBody("[{\"id\":1},{\"id\":2},{\"id\":3}]", JSON_UTF_8));

        given()
            .a_REST_client(client);

        when()
            .connecting_to(mockServerUri())
            .and().appending_path("some-id")
            .and().getting_the_response();

        then()
            .the_response_contents(asList(
                new JsonAssertion<>("$[0].id", greaterThan(0)),
                new JsonAssertion<>("$[1].id", equalTo(2))))
            .and().$("the some id request was sent",
                __ -> mockServer.verify(request().withPath("/some-id")));
    }

    @Test
    @Reference("159")
    @SneakyThrows
    public void shouldVerifyAccessByExpectionId() {
        val dummyExpectations = mockServer // GET is implied
            .when(request().withPath("/dummy"))
            .respond(response(EMPTY));

        given()
            .a_REST_client(client);

        when()
            .connecting_to(mockServerUri())
            .and().appending_path("dummy")
            .and().getting_the_response();

        then()
            .$("the dummy request was sent",
                __ -> mockServer.verify(as(dummyExpectations)));
    }

    @Test
    @Reference("159")
    @SneakyThrows
    public void shouldVerifyAccessOfDroppedConnection() {
        mockServer // GET is implied
            .when(request().withPath("/drop-connection"))
            .error(error().withDropConnection(true));

        given()
            .a_REST_client(client);

        when()
            .connecting_to(mockServerUri())
            .and().appending_path("drop-connection")
            .and().$("trying to access",
                self -> self.safely(__ -> self.getting_the_response()));

        then()
            .$("the drop connection request was sent",
                __ -> mockServer.verify(request()
                    .withPath("/drop-connection")));
    }

    @Test
    @Reference("159")
    @SneakyThrows
    public void shouldVerifyAccessedTwice() {
        mockServer // GET is implied
            .when(request().withPath("/twice"), exactly(2))
            .respond(response(EMPTY));

        given()
            .a_REST_client(client);

        when()
            .connecting_to(mockServerUri())
            .and().appending_path("twice")
            .and().getting_the_response()
            .and().getting_the_response()
            .and().getting_the_response(); // should return 404

        then()
            .the_response_status(is(CLIENT_ERROR))
            .and().$("the method was called more than twice",
                __ -> mockServer.verify(request().withPath("/twice"),
                    VerificationTimes.exactly(3)));
    }

    @Test
    @Reference("159")
    @SneakyThrows
    public void shouldVerifyAccessedTwiceByExpectationId() {
        val twiceExpectations = mockServer // GET is implied
            .when(request().withPath("/twice"), exactly(2))
            .respond(response(EMPTY));

        given()
            .a_REST_client(client);

        when()
            .connecting_to(mockServerUri())
            .and().appending_path("twice")
            .and().getting_the_response()
            .and().getting_the_response()
            .and().getting_the_response(); // should return 404

        then()
            .the_response_status(is(CLIENT_ERROR))
            // ISSUE verifies that was called twice while was three times
            .and().$("the method was called more than twice",
                __ -> mockServer.verify(as(twiceExpectations)));
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
