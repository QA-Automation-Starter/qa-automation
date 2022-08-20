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

import static dev.aherscu.qa.tester.utils.StreamMatchersExtensions.*;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.testing.example.*;
import dev.aherscu.qa.testing.example.steps.tutorial.*;
import io.github.bonigarcia.wdm.*;
import lombok.*;

public class _9_TestingGoogleWithJGiven
    extends
    ApplicationPerClassWebSessionTest<TestConfiguration, GoogleFixtures<?>, GoogleActions<?>, GoogleVerifications<?>> {

    protected _9_TestingGoogleWithJGiven() {
        super(TestConfiguration.class);
    }

    @Test
    public void shouldFind() {
        given()
            .google(webDriver.get());

        when()
            .searching_for(new Text("something"));

        then()
            .the_results(
                adaptedStream(googleResult -> googleResult.title.value,
                    hasItemsMatching(
                        containsStringIgnoringCase("something"))));
    }

    @Test
    public void shouldNotFind() {
        given()
            .google(webDriver.get());

        when()
            .searching_for(new Text(randomAlphanumeric(40)));

        then()
            .the_results(counts(equalTo(0L)));
    }

    @BeforeClass
    @SneakyThrows
    @Override
    public void beforeClassOpenWebDriver() {
        WebDriverManager.chromedriver().setup();
        super.beforeClassOpenWebDriver();
        webDriver.get().asRemote().manage().window().maximize();
    }
}
