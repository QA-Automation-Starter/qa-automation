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

package dev.aherscu.qa.jgiven.commons.defects;

import static dev.aherscu.qa.testing.utils.ThreadUtils.*;

import org.testng.annotations.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.testng.*;

// ISSUE https://github.com/TNG/JGiven/issues/755
public class DurationReporting extends
    ScenarioTest<DurationReporting.Fixtures<?>, DurationReporting.Actions<?>, DurationReporting.Verifications<?>> {

    @Test
    public void shouldNotMissDurationReport() {
        given().nothing();
        when().doing_nothing(1_000)
            .and().doing_nothing(1_000)
            .and().doing_nothing(1_000);
        then().succeeds();
    }

    @Test
    public void shouldReportDurationOnEveryStep() {
        given().nothing();
        when().doing_nothing(1_000)
            .doing_nothing(1_000)
            .doing_nothing(1_000);
        then().succeeds();
    }

    static class Actions<SELF extends Actions<SELF>> extends Stage<SELF> {
        public SELF doing_nothing(final long millis) {
            sleep(millis);
            return self();
        }
    }

    static class Fixtures<SELF extends Fixtures<SELF>> extends Stage<SELF> {
        public SELF nothing() {
            return self();
        }
    }

    static class Verifications<SELF extends Verifications<SELF>>
        extends Stage<SELF> {
        public SELF succeeds() {
            return self();
        }
    }
}
