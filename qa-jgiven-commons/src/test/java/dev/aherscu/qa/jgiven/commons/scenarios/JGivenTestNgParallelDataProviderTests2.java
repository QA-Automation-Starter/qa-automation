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

package dev.aherscu.qa.jgiven.commons.scenarios;

import static dev.aherscu.qa.jgiven.commons.utils.ConfigurableScenarioTest.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.*;
import java.util.stream.*;

import org.testng.annotations.*;

import com.tngtech.jgiven.testng.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;

/**
 * Seems that parallel data providers are not supported.
 * <p>
 * see https://github.com/TNG/JGiven/issues/829
 */
@Listeners({ ScenarioTestListenerEx.class })
public class JGivenTestNgParallelDataProviderTests2 extends
    ScenarioTest<GenericFixtures<AnyScenarioType, ?>, GenericActions<AnyScenarioType, ?>, GenericVerifications<AnyScenarioType, ?>> {

    @DataProvider(parallel = true)
    private static Iterator<Integer> data() {
        // ISSUE up to 10 it seems working... with 100 it does not
        return IntStream.range(0, 100).iterator();
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldRunWithParallelDataProvider(final Integer aValue) {
        given().nothing();
        when().doing_nothing();
        then().should_succeed(is(true));
    }
}
