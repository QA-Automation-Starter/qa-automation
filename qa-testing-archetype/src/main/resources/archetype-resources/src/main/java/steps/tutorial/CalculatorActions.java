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

import static io.appium.java_client.MobileBy.*;

public class CalculatorActions<SELF extends CalculatorActions<SELF>>
    extends WebDriverActions<SELF> {
    public SELF typing(final String expression) {
        return typing_$_into(expression, AccessibilityId("CalculatorResults"));
    }
}
