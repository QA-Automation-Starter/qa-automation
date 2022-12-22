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
package dev.aherscu.qa.jgiven.commons.scenarios;

import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.client.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.tags.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.tester.utils.config.*;
import dev.aherscu.qa.tester.utils.rest.*;
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
public final class GenericRestClientTest
    extends
    UnitilsScenarioTest<BaseConfiguration, RestScenarioType, RestFixtures<?>, RestActions<?>, RestVerifications<?>> {

    private Client client;

    private GenericRestClientTest() {
        super(BaseConfiguration.class);
    }

    @Test(expectedExceptions = RuntimeException.class,
        expectedExceptionsMessageRegExp = ".*message with.*")
    public void selfTest() {
        throw new RuntimeException("some message with a cause");
    }

    /**
     * Should fail with {@link NullPointerException} if trying to connect to
     * {@code null} URL.
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void shouldFail() {
        given().a_REST_client(LoggingClientBuilder.newClient());
        when().connecting_to((URI) null);
    }

    @Ignore // ISSUE when running via VPN the proxy interferes
    @Test(expectedExceptions = ProcessingException.class,
        expectedExceptionsMessageRegExp = ".*Connection timed out.*")
    @SneakyThrows
    public void shouldFailConnecting() {
        given().a_REST_client(client);

        when()
            // see https://datatracker.ietf.org/doc/html/rfc5737
            .connecting_to(new URI("http://192.0.2.1"))
            .and().getting_the_response();
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
