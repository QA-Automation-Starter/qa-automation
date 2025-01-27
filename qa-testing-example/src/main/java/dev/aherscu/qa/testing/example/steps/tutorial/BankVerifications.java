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

package dev.aherscu.qa.testing.example.steps.tutorial;

import java.util.stream.*;

import org.hamcrest.*;
import org.openqa.selenium.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;
import dev.aherscu.qa.testing.example.model.tutorial.*;

public class BankVerifications<SELF extends BankVerifications<SELF>>
    extends WebDriverVerifications<SELF> {

    @AttachesScreenshot
    public SELF the_customers(final Matcher<Stream<Customer>> matcher) {
        return eventually_assert_that(
            () -> elements(By.xpath("//tbody/tr"))
                .stream()
                .map(element -> Customer.builder()
                    .firstName(element(By.xpath("td[1]"), element).getText())
                    .lastName(element(By.xpath("td[2]"), element).getText())
                    .postalCode(element(By.xpath("td[3]"), element).getText())
                    .build()),
            matcher);
    }
}
