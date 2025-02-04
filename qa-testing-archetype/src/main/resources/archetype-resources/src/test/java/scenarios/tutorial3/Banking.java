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

package ${package}.scenarios.tutorial3;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.hasSpecificItems;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.webdriver.*;
import ${package}.*;
import ${package}.model.tutorial.*;
import ${package}.steps.tutorial.*;
import lombok.*;

public class Banking
    extends
    ApplicationPerMethodWebSessionTest<TestConfiguration, BankFixtures<?>, BankActions<?>, BankVerifications<?>> {

    protected Banking() {
        super(TestConfiguration.class);
    }

    @BeforeMethod
    @SneakyThrows
    @Override
    public void beforeMethodOpenWebDriver() {
        super.beforeMethodOpenWebDriver();
        webDriver.get().asRemote().manage().window().maximize();
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldCreateCustomer(final Customer customer) {
        given()
            .a_bank(webDriver.get());

        when()
            .adding(customer);

        then()
            .the_customers(hasSpecificItems(customer));
    }

    @DataProvider//(parallel = true)
    private Customer[] data() {
        return new Customer[] {
            Customer.builder()
                .firstName("kuku")
                .lastName("bar")
                .postalCode("12345")
                .build()
        };
    }
}
