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

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.testing.utils.ThreadUtils.*;
import static java.util.Objects.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.testng.*;
import org.testng.annotations.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Implements TestNG test retry mechanism. You have to ensure proper reset
 * mechanism otherwise the test will be reattempted while the application is in
 * same state as previous attempt left it.
 *
 * <p>
 * Had an issue with JGiven: https://github.com/TNG/JGiven/issues/312
 * </p>
 *
 * <p>
 * Can be added as a listener, in this case the retry mechanism will get applied
 * to all test methods covered by.
 * </p>
 */
@SuppressWarnings("boxing")
@Slf4j
public class TestRetryAnalyzer
    implements IRetryAnalyzer, IAnnotationTransformer {
    /**
     * The default retry interval in milliseconds.
     */
    public static final int                  DEFAULT_INTERVAL_MS = 1_000;
    /**
     * The default retries.
     */
    public static final int                  DEFAULT_RETRIES     = 3;
    /**
     * Retries per qualified method name {@code <class>.<method>}.
     */
    public static final Map<String, Integer> retryCounters       =
        new ConcurrentHashMap<>();

    @Override
    public boolean retry(final ITestResult result) {
        val config = new TestMethodConfig(result);
        val methodName = result.getMethod().getQualifiedName();
        val retryCounter = retryCounters.getOrDefault(methodName, 0);

        log.trace("retried {} of {}",
            retryCounter,
            config.retries);

        if (retryCounter >= config.retries) {
            return false;
        }

        log.debug("{} failed, retrying count {}/{} after waiting {} ms",
            methodName,
            retryCounter + 1,
            config.retries,
            config.intervalMs);

        sleep(config.intervalMs);
        retryCounters.put(methodName, retryCounter + 1);
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void transform(
        final ITestAnnotation annotation,
        final Class testClass,
        final Constructor testConstructor,
        final Method testMethod) {
        log.info("{}:{} will be retried in case of failure",
            nonNull(testClass) ? testClass.getSimpleName() : "*",
            nonNull(testMethod) ? testMethod.getName() : "*");
        annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
    }

    /**
     * Configures the test retry mechanism.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Config {
        /**
         * @return interval between retries in milliseconds
         */
        int intervalMs() default DEFAULT_INTERVAL_MS;

        /**
         * @return how many times to retry the test
         */
        int retries() default DEFAULT_RETRIES;
    }

    private static final class TestMethodConfig {
        final int retries, intervalMs;

        TestMethodConfig(final ITestResult result) {
            this(result
                .getMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getAnnotation(Config.class));

        }

        TestMethodConfig(final TestRetryAnalyzer.Config config) {
            retries = nonNull(config)
                ? config.retries()
                : DEFAULT_RETRIES;
            intervalMs = nonNull(config)
                ? config.intervalMs()
                : DEFAULT_INTERVAL_MS;
        }
    }
}
