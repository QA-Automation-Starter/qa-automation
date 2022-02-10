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

package dev.aherscu.qa.testing.example.scenarios.tutorial;

import static io.appium.java_client.MobileBy.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.testing.example.*;

public class _8_TestingWithConfiguration
    // NOTE: just to get the configuration from the framework
    extends ApplicationPerClassWebSessionTest {

    @ScenarioStage
    protected CalculatorFixtures<?>      calculatorFixtures;

    @ScenarioStage
    protected CalculatorActions<?>       calculatorActions;

    @ScenarioStage
    protected CalculatorVerifications<?> calculatorVerifications;

    static public class CalculatorFixtures<SELF extends CalculatorFixtures<SELF>>
        extends WebDriverFixtures<SELF> {
        SELF a_calculator(final WebDriverEx driver) {
            return a_web_driver(driver);
        }
    }

    static public class CalculatorActions<SELF extends CalculatorActions<SELF>>
        extends WebDriverActions<SELF> {
        SELF typing(final String value) {
            return typing_$_into(value, AccessibilityId("CalculatorResults"));
        }
    }

    static public class CalculatorVerifications<SELF extends CalculatorVerifications<SELF>>
        extends WebDriverVerifications<SELF> {
        SELF the_title(final Matcher<String> matcher) {
            assertThat(webDriver.get().asWindows().getTitle(), matcher);
            return self();
        }

        SELF the_result(final Matcher<String> matcher) {
            return eventually_assert_that(
                () -> element(AccessibilityId("CalculatorResults")).getText(),
                matcher);
        }
    }

    @Test
    public void shouldOpenCalculator() {
        calculatorFixtures
            .given().a_calculator(webDriver.get());

        calculatorVerifications
            .then().the_title(is(equalTo("Calculator")));
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldCalculate(final String expression, final String result) {
        calculatorFixtures
            .given().a_calculator(webDriver.get());

        calculatorActions
            .when().typing(expression + "=");

        calculatorVerifications
            .then().the_result(is(stringContainsInOrder("Display is", result)));
    }

    @DataProvider
    static Object[][] data() {
        return new Object[][] {
            { "7+8", "15" },
            { "7-8", "-1" },
            { "2*5", "11" },
            { "6/2", "3" },
        };
    }
}
