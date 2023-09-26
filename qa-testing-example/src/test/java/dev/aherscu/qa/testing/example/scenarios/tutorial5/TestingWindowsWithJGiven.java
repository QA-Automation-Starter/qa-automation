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

package dev.aherscu.qa.testing.example.scenarios.tutorial5;

import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.*;
import dev.aherscu.qa.testing.example.*;
import dev.aherscu.qa.testing.example.model.tutorial.*;
import dev.aherscu.qa.testing.example.steps.tutorial.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class TestingWindowsWithJGiven
    extends ApplicationPerClassWebSessionTest<TestConfiguration, CalculatorFixtures<?>, CalculatorActions<?>, CalculatorVerifications<?>> {

    protected TestingWindowsWithJGiven() {
        super(TestConfiguration.class);
    }

    @DataProvider
    static Object[][] data() {
        return new Object[][] {
            { Calculation.builder().expression("7+8").result("15").build() },
            { Calculation.builder().expression("2*5").result("10").build() },
            { Calculation.builder().expression("333/111").result("3").build() },
            { Calculation.builder().expression("7-8").result("-1").build() },
        };
    }

    @BeforeClass
    @SneakyThrows
    @Override
    protected void beforeClassOpenWebDriver() {
        log.debug("before class opening WinAppDriver");
        webDriver.set(WebDriverEx.from(configuration()
            .capabilitiesFor("provider.local.windows")));
    }

    @Test
    public void shouldOpenCalculator() {
        given()
            .a_calculator(webDriver.get());

        then()
            .the_title(is(equalTo("Calculator")));
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldCalculate(final Calculation calculation) {
        given()
            .a_calculator(webDriver.get());

        when()
            .typing(calculation.expression + "=");

        then()
            .the_result(is(
                stringContainsInOrder("Display is", calculation.result)));
    }
}
