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

package ${package}.steps.tutorial;

import static jakarta.ws.rs.client.Entity.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.rest.steps.*;
import ${package}.*;
import ${package}.model.tutorial.*;

public class SwaggerPetstoreActions<SELF extends SwaggerPetstoreActions<SELF>>
    extends RestActions<SELF> {

    @ProvidedScenarioState
    protected final ThreadLocal<SwaggerLoginResponse> loginResponse =
        new ThreadLocal<>();
    @ExpectedScenarioState
    protected TestConfiguration                       configuration;

    public SELF adding(final Pet pet) {
        return retrying(invoke(configuration.petStore(client.get())
            .path("pet")
            .request()
            .buildPost(json(pet))));
    }

    public SELF logging_in_with(final Credentials credentials) {
        return retrying(invoke(configuration.petStore(client.get())
            .path("user/login")
            .queryParam("username", credentials.userName)
            .queryParam("password", credentials.password)
            .request()
            .buildGet(),
            json -> loginResponse.set(SwaggerLoginResponse.from(json))));
    }
}
