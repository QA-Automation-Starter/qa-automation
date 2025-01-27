/*
 * Copyright 2024 Adrian Herscu
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

package dev.aherscu.qa.testing.example.steps.tutorial;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.rest.steps.*;
import dev.aherscu.qa.testing.example.*;
import jakarta.ws.rs.client.*;

public class BinanceFixtures<SELF extends BinanceFixtures<SELF>>
    extends RestFixtures<SELF> {

    @ProvidedScenarioState
    protected TestConfiguration configuration;

    public SELF binance(@Hidden final Client client) {
        return a_REST_client(client); // any additional configuration
    }

    @Hidden
    public SELF with(final TestConfiguration configuration) {
        this.configuration = configuration;
        return self();
    }
}
