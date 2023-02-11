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

package ${package}.steps.tutorial;

import java.util.stream.*;

import org.hamcrest.*;
import org.openqa.selenium.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import ${package}.model.tutorial.*;
import lombok.extern.slf4j.*;

@Slf4j
public class GoogleVerifications<SELF extends GoogleVerifications<SELF>>
    extends WebDriverVerifications<SELF> {

    @AttachesScreenshot
    public SELF the_results(final Matcher<Stream<GoogleResult>> matcher) {
        return eventually_assert_that(
            () -> elements(By.xpath("//a/h3"))
                .stream()
                .filter(WebElement::isDisplayed)
                .map(resultElement -> GoogleResult.builder()
                    .title(new Text(resultElement
                        .getAttribute("textContent")))
                    // populate other fields per need
                    .build()),
            matcher);
    }
}
