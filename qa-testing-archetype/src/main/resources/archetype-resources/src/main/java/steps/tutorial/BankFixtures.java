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

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.steps.*;

public class BankFixtures<SELF extends BankFixtures<SELF>>
    extends WebDriverFixtures<SELF> {
    public SELF a_bank(final WebDriverEx driver) {
        return a_web_driver(driver)
            .at("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }
}
