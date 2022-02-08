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
package dev.aherscu.qa.testing.example.steps;

import javax.annotation.concurrent.*;

import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.extern.slf4j.*;

/**
 * Application specific UI fixtures.
 *
 * @param <SELF>
 *            the type of the subclass
 * @author aherscu
 */
@Slf4j
@ThreadSafe
public class ApplicationUIFixtures<SELF extends ApplicationUIFixtures<SELF>>
    extends WebDriverFixtures<SELF> {

    @Override
    public SELF a_web_driver(
        @WebDriverFormatter.Annotation final WebDriverEx webDriver) {
        return super.a_web_driver(webDriver)
            .and().at(webDriver.originalCapabilities
                .getCapability("target")
                .toString());
    }
}
