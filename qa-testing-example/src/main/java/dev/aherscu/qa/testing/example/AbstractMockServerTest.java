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
package dev.aherscu.qa.testing.example;

import java.net.*;

import javax.ws.rs.core.*;

import org.mockserver.integration.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.config.*;

/**
 * Contains REST sample tests just to ensure that the testing infrastructure
 * works as required.
 *
 * @author aherscu
 * @param <T>
 *            type of scenario
 * @param <GIVEN>
 *            type of fixtures
 * @param <WHEN>
 *            type of actions
 * @param <THEN>
 *            type of verifications
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE",
    justification = "JGiven framework limitation")
abstract public class AbstractMockServerTest<T extends AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN extends GenericActions<T, ?> & ScenarioType<T>, THEN extends GenericVerifications<T, ?> & ScenarioType<T>>
    extends
    UnitilsScenarioTest<BaseConfiguration, T, GIVEN, WHEN, THEN> {

    protected final ClientAndServer mockServer;

    /**
     * Initializes with {@link BaseConfiguration}.
     */
    protected AbstractMockServerTest() {
        super(BaseConfiguration.class);
        // NOTE port 0 means any free port
        mockServer = ClientAndServer.startClientAndServer(0);
    }

    @AfterClass(alwaysRun = true)
    protected void stopMockRestServer() {
        mockServer.stop();
    }

    protected URI mockServerUri() {
        return UriBuilder.fromUri("http://{host}:{port}")
            .build("localhost", mockServer.getPort());
    }
}
