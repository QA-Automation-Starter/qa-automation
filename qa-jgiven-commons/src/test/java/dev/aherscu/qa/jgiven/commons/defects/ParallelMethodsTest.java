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

package dev.aherscu.qa.jgiven.commons.defects;

import static dev.aherscu.qa.tester.utils.ThreadUtils.*;
import static java.lang.Double.*;
import static java.lang.Math.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.testng.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ParallelMethodsTest extends
    ScenarioTest<ParallelMethodsTest.Fixtures, ParallelMethodsTest.Actions, ParallelMethodsTest.Verifications> {

    @Test
    public void test1() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test10() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test2() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test3() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test4() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test5() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test6() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test7() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test8() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    @Test
    public void test9() {
        given().nothing();
        when().setting_state("kuku");
        then().the_state(is("kuku"))
            .and().the_state_ex(is("kukuEx"));
    }

    static class Actions extends Stage<Actions> {
        @ExpectedScenarioState
        private ThreadLocal<String> state;
        @ExpectedScenarioState
        private ThreadLocal<String> stateEx;

        Actions setting_state(final String string) {
            sleep(valueOf(random() * 10_000).longValue());
            log.debug("setting {}", string);
            state.set(string);
            stateEx.set(string + "Ex");
            return self();
        }
    }

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD",
        justification = "read by jgiven framework")
    static class Fixtures extends Stage<Fixtures> {
        @ProvidedScenarioState
        private final ThreadLocal<String> state   = new ThreadLocal<>();
        @ProvidedScenarioState
        private final ThreadLocal<String> stateEx = new ThreadLocal<>();

        Fixtures nothing() {
            return self();
        }
    }

    static class Verifications extends Stage<Verifications> {
        @ExpectedScenarioState
        private ThreadLocal<String> state;
        @ExpectedScenarioState
        private ThreadLocal<String> stateEx;

        Verifications the_state(final Matcher<String> stateMatcher) {
            log.debug("asserting on {}", state.get());
            assertThat(state.get(), stateMatcher);
            return self();
        }

        Verifications the_state_ex(final Matcher<String> stateMatcher) {
            log.debug("asserting on {}", state.get());
            assertThat(stateEx.get(), stateMatcher);
            return self();
        }
    }
}
