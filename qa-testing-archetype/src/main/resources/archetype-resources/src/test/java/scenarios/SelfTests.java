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

package ${package}.scenarios;

import static dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest.*;
import static dev.aherscu.qa.testing.utils.StreamMatchers.allMatch;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.*;

import org.testng.annotations.*;

import lombok.extern.slf4j.*;

@Slf4j
public class SelfTests extends AbstractSelfTests {
    // try here your hamcrest assertions

    @Test
    public void shouldMatchEitherString() {
        assertThat(
            Stream.of("adf testng", "testng fadf", "Try again", "More results",
                "gwrr testngrggr"),
            allMatch(either(containsStringIgnoringCase("testng"))
                .or(containsStringIgnoringCase("More results"))
                .or(containsStringIgnoringCase("Try again"))));
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldRun(final int id) {
        log.debug("runs with {}", id);
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][] { { 1 }, { 2 } };
    }

}
