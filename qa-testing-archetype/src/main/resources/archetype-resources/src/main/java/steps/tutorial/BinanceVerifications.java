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

import static dev.aherscu.qa.testing.utils.ObjectMapperUtils.*;

import java.util.stream.*;

import org.hamcrest.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.rest.steps.*;
import ${package}.*;
import ${package}.model.tutorial.*;
import jakarta.ws.rs.client.*;

public class BinanceVerifications<SELF extends BinanceVerifications<SELF>>
    extends RestVerifications<SELF> {

    @ExpectedScenarioState
    protected TestConfiguration   configuration;

    @ExpectedScenarioState
    protected ThreadLocal<Client> client;

    public SELF the_exchange_info(final Matcher<Stream<Symbol>> symbolsMatcher) {
        return eventually_assert_that(
            invoke(
                configuration.binance(client.get())
                    .path("exchangeInfo")
                    .request()
                    .buildGet(),
                json -> fromJson(json, ExchangeInfoResponse.class).symbols
                    .stream()),
            symbolsMatcher);
    }
}
