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

import static org.hamcrest.Matchers.*;

import com.tngtech.jgiven.annotation.*;
import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JGivenMultipleStages extends
    ScenarioTestEx<GenericFixtures<AnyScenarioType, ?>, GenericActions<AnyScenarioType, ?>, GenericVerifications<AnyScenarioType, ?>> {
    public static class MActions<SELF extends MActions<SELF>>
        extends GenericActions<AnyScenarioType, SELF> {

        public SELF m_action() {
            return self();
        }
    }

    public static class MFixtures<SELF extends MFixtures<SELF>>
        extends GenericFixtures<AnyScenarioType, SELF> {
        // ISSUE super given(), and(), all intro words do not return this type
        @Override
        @IntroWord
        public SELF and() {
            return super.and();
        }

        public SELF m_fixture() {
            return self();
        }
    }

    public static class MVerifications<SELF extends MVerifications<SELF>>
        extends GenericVerifications<AnyScenarioType, SELF> {

        public SELF m_verification() {
            log.info(">>> m_verification");
            return self();
        }
    }

    public static class NActions<SELF extends NActions<SELF>>
        extends GenericActions<AnyScenarioType, SELF> {

        public SELF n_action() {
            return self();
        }
    }

    public static class NFixtures<SELF extends NFixtures<SELF>>
        extends GenericFixtures<AnyScenarioType, SELF> {
        public SELF n_fixture() {
            return self();
        }
    }

    public static class NVerifications<SELF extends NVerifications<SELF>>
        extends GenericVerifications<AnyScenarioType, SELF> {

        public SELF n_verification() {
            return self();
        }
    }

    @Test
    public void shouldWorkWithMultipleStages() {
        given().nothing();
        // ISSUE cannot add intro words
        addStage(MFixtures.class)
            .and().m_fixture();
        addStage(NFixtures.class)
            // must define it in NFixtures like in MFixtures
            /*.and()*/.n_fixture();

        when().doing_nothing();
        addStage(MActions.class).m_action();
        addStage(NActions.class).n_action();

        then().should_succeed(is(true));
        addStage(MVerifications.class).m_verification();
        addStage(NVerifications.class).n_verification();
    }
}
