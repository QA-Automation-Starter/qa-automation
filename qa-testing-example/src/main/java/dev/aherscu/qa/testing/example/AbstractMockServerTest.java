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

import java.io.*;
import java.net.*;

import javax.ws.rs.core.*;

import org.mockserver.client.*;
import org.mockserver.integration.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.config.*;
import lombok.*;
import lombok.extern.slf4j.*;

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
@Slf4j
abstract public class AbstractMockServerTest<T extends AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN extends GenericActions<T, ?> & ScenarioType<T>, THEN extends GenericVerifications<T, ?> & ScenarioType<T>>
    extends
    UnitilsScenarioTest<BaseConfiguration, T, GIVEN, WHEN, THEN> {

    public static final int          DEFAULT_PORT = 1080;

    protected final MockServerClient mockServer;

    private final boolean            usingOutOfProcessMockServer;

    /**
     * If port 1080 is free will initiate an in-process MockServer, otherwise
     * will try connecting to port 1080.
     */
    protected AbstractMockServerTest() {
        super(BaseConfiguration.class);

        usingOutOfProcessMockServer = canUseOutOfProcessMockServer();

        log.debug("using out-of-process MockServer: {}",
            usingOutOfProcessMockServer);

        mockServer = usingOutOfProcessMockServer
            ? new MockServerClient("localhost", outOfProcessPort())
            : ClientAndServer.startClientAndServer(0);
    }

    private boolean canUseOutOfProcessMockServer() {
        try (val socket = new ServerSocket(outOfProcessPort())) {
            socket.close();
            return false;
        } catch (final IOException ioe) {
            return true;
        }
    }

    protected int outOfProcessPort() {
        return DEFAULT_PORT;
    }

    @AfterClass(alwaysRun = true)
    protected void stopMockRestServer() {
        if (!usingOutOfProcessMockServer)
            mockServer.stop();
    }

    protected URI mockServerUri() {
        return UriBuilder.fromUri("http://{host}:{port}")
            .build("localhost", mockServer.getPort());
    }
}
