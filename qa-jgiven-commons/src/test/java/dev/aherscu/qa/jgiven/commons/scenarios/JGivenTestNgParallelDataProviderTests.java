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
import static org.hamcrest.MatcherAssert.*;

import java.util.*;
import java.util.stream.*;

import lombok.extern.slf4j.*;
import org.hamcrest.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.testng.*;

import dev.aherscu.qa.jgiven.commons.utils.*;

/**
 * Seems that parallel data providers are not supported.
 * <p>
 * see <a href="https://github.com/TNG/JGiven/issues/829">...</a>
 */
@Listeners({ ScenarioTestListenerEx.class })
@Slf4j
public class JGivenTestNgParallelDataProviderTests extends
    ScenarioTest<JGivenTestNgParallelDataProviderTests.SomeGiven<?>, JGivenTestNgParallelDataProviderTests.SomeWhen<?>, JGivenTestNgParallelDataProviderTests.SomeThen<?>> {

    public static class OtherGiven<SELF extends OtherGiven<SELF>>
        extends Stage<SELF> {
        @ProvidedScenarioState
        protected final ThreadLocal<Integer> aValue = new ThreadLocal<>();

        public SELF with(final Integer value) {
            aValue.set(value);
            return self();
        }
    }

    @Slf4j
    public static class OtherThen<SELF extends OtherThen<SELF>>
        extends Stage<SELF> {
        @ExpectedScenarioState
        protected ThreadLocal<Integer> aValue;

        public SELF the_value(final Matcher<Integer> rule) {
            // ISSUE: when running with enough parallelism aValue is null
            assertThat(aValue.get(), rule);
            return self();
        }

        @BeforeScenario
        protected void beforeScenario() {
            log.debug("before scenario");
        }
    }

    public static class OtherWhen<SELF extends OtherWhen<SELF>>
        extends Stage<SELF> {
        // empty
    }

    public static class SomeGiven<SELF extends SomeGiven<SELF>>
        extends Stage<SELF> {
        // empty
    }

    @Slf4j
    public static class SomeThen<SELF extends SomeThen<SELF>>
        extends Stage<SELF> {
        @BeforeScenario
        protected void beforeScenario() {
            log.debug("before scenario");
        }
    }

    public static class SomeWhen<SELF extends SomeWhen<SELF>>
        extends Stage<SELF> {
        // empty
    }

    // does not work via parallel data provider
    // @ScenarioStage
    // protected OtherGiven<?> otherGiven;
    // @ScenarioStage
    // protected OtherThen<?> otherThen;
    // @ScenarioStage
    // protected OtherWhen<?> otherWhen;
    //
    // @BeforeScenario or @BeforeMethod
    // protected void beforeMethodInitStages() {
    // does not work, something is not yet initialized
    // otherGiven.set(getScenario().addStage(OtherGiven.class));
    // otherThen.set(getScenario().addStage(OtherThen.class));
    // otherWhen.set(getScenario().addStage(OtherWhen.class));
    // }

    @DataProvider(parallel = true)
    private static Iterator<Integer> data() {
        // ISSUE up to 10 it seems working... with 100 it does not
        return IntStream.range(0, 1).iterator();
    }

    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldRunWithParallelDataProvider(final Integer aValue) {
        // stage(OtherGiven.class).with(aValue);
        // ISSUE compiler warning unchecked call as member of raw type
        // stage(OtherThen.class).the_value(is(aValue));
        log.debug("# {}", aValue);
        otherGiven().given().with(aValue);
        otherThen().then().the_value(is(aValue));
    }

    protected OtherGiven<?> otherGiven() {
        return addStage(OtherGiven.class);
    }

    protected OtherThen<?> otherThen() {
        return addStage(OtherThen.class);
    }

    protected OtherWhen<?> otherWhen() {
        return addStage(OtherWhen.class);
    }
}
