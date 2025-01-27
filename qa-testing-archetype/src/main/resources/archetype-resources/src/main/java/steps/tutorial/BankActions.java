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

package ${package}.steps.tutorial;

import org.openqa.selenium.*;

import dev.aherscu.qa.jgiven.webdriver.steps.*;
import ${package}.model.tutorial.*;

public class BankActions<SELF extends BankActions<SELF>>
    extends WebDriverActions<SELF> {
    public SELF clicking_bank_manager_login() {
        return clicking(By
            .xpath("//button[contains(text(),'Bank Manager Login')]"));
    }

    public SELF clicking_add_customer() {
        return clicking(By
            .xpath("//button[contains(text(),'Add Customer')]"));
    }

    public SELF clicking_customers() {
        return clicking(By
            .xpath("//button[contains(text(),'Customers')]"));
    }

    public SELF dismissing_alert() {
        webDriver.get().asRemote().switchTo().alert().dismiss();
        return self();
    }

    public SELF adding(final Customer customer) {
        return clicking_bank_manager_login()
            .and().clicking_add_customer()
            .and().typing_$_into(customer.firstName,
                By.xpath("//input[@ng-model='fName']"))
            .and().typing_$_into(customer.lastName,
                By.xpath("//input[@ng-model='lName']"))
            .and().typing_$_into(customer.postalCode,
                By.xpath("//input[@ng-model='postCd']"))
            .and().clicking(By
                .xpath(
                    "//button[contains(text(), 'Add Customer') and @class='btn btn-default']"))
            .and().dismissing_alert()
            .and().clicking_customers();
    }
}
