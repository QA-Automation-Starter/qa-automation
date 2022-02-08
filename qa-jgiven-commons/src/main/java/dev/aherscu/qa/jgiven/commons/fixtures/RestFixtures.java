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
package dev.aherscu.qa.jgiven.commons.fixtures;

import static java.util.Objects.*;

import javax.annotation.concurrent.*;
import javax.ws.rs.client.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import lombok.extern.slf4j.*;

/**
 * Generic REST client fixtures. Sets up a client.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@ThreadSafe
@Slf4j
@SuppressWarnings("boxing")
public class RestFixtures<SELF extends RestFixtures<SELF>>
    extends GenericFixtures<RestScenarioType, SELF> {

    /**
     * The given REST client.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Client> client = new ThreadLocal<>();

    /**
     * Creates a {@link Client}.
     *
     * @return {@link #self()}
     */
    public SELF a_REST_client(@Hidden final Client aClient) {
        log.debug("setting REST client {}", aClient);
        client.set(requireNonNull(aClient, "must provide a REST client"));
        return self();
    }

    protected final Client thisClient() {
        return requireNonNull(client.get(), "REST client not initialized");
    }
}
