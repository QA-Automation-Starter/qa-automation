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

package ${package}.scenarios.tutorial6;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.hasSpecificItems;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.rest.model.*;
import dev.aherscu.qa.jgiven.rest.tags.*;
import dev.aherscu.qa.testing.utils.rest.*;
import ${package}.*;
import ${package}.model.tutorial.*;
import ${package}.steps.tutorial.*;
import jakarta.ws.rs.client.*;

@RestTest
@Ignore("does not work from github")
public class Binance extends
    ConfigurableScenarioTest<TestConfiguration, RestScenarioType, BinanceFixtures<?>, BinanceActions<?>, BinanceVerifications<?>> {

    protected Binance() {
        super(TestConfiguration.class);
    }

    protected Client client;

    @AfterClass
    protected void afterClassCloseRestClient() {
        client.close();
    }

    @BeforeClass
    protected void beforeClassOpenRestClient() {
        client = LoggingClientBuilder.newClient();
    }

    @Test
    public void shouldGetExchangeInfo() {
        given()
            .binance(client)
            .with(configuration());

        when()
            .doing_nothing();

        // ISSUE does not work from github's runners... returns with 451
        // Service unavailable from a restricted location according to
        // 'b. Eligibility' in https://www.binance.com/en/terms.
        // Please contact customer service if you believe you received this message in
        // error.
        then()
            .the_exchange_info(hasSpecificItems(Symbol.builder()
                .symbol("BTCUSDT")
                .baseAsset("BTC")
                .contractType("PERPETUAL")
                .build()));
    }
}
