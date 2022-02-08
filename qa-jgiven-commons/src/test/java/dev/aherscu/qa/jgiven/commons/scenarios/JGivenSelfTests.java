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

package dev.aherscu.qa.jgiven.commons.scenarios;

import org.testng.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.testng.*;

import dev.aherscu.qa.jgiven.commons.utils.*;

/**
 * Demonstrates that the TestNG retry mechanism is interfered by JGiven's
 * instrumentation; see https://github.com/TNG/JGiven/issues/312
 *
 * Appears to be fixed in 0.18.1 according to
 * https://github.com/TNG/JGiven/pull/422
 */
@SuppressWarnings({ "static-method", "boxing" })
@Listeners({ ScenarioTestListenerEx.class })
public class JGivenSelfTests extends
    ScenarioTest<JGivenSelfTests.SomeGiven<?>, JGivenSelfTests.SomeWhen<?>, JGivenSelfTests.SomeThen<?>> {

    @Test(retryAnalyzer = TestRetryAnalyzer.class)
    @TestRetryAnalyzer.Config(retries = 129, intervalMs = 13)
    public void shouldFinallySucceed() {
        then().should_fail_sometimes();
    }

    @Ignore // fails after retries as expected
    @Test(retryAnalyzer = TestRetryAnalyzer.class)
    @TestRetryAnalyzer.Config(retries = 129, intervalMs = 13)
    public void shouldNeverSucceed() {
        then().should_fail();
    }

    public static class SomeGiven<SELF extends SomeGiven<SELF>>
        extends Stage<SELF> {
        // empty
    }

    public static class SomeThen<SELF extends SomeThen<SELF>>
        extends Stage<SELF> {

        public SELF should_fail() {
            throw new AssertionError("constant failure");
        }

        public SELF should_fail_sometimes() {
            Assert.assertEquals(System.currentTimeMillis() % 17, 0);
            return self();
        }
    }

    public static class SomeWhen<SELF extends SomeWhen<SELF>>
        extends Stage<SELF> {
        // empty
    }
}
