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

package dev.aherscu.qa.testing.utils.testng;

import org.testng.*;

import lombok.extern.slf4j.*;

@Slf4j
public class TestRunLogger implements ITestListener {
    @Override
    public void onTestStart(final ITestResult result) {
        log.info("started {}", result);
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        log.info("succeeded {}", result);
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        log.info("failed {}", result);
    }

    @Override
    public void onTestSkipped(final ITestResult result) {
        log.info("skipped {}", result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(
        final ITestResult result) {
        log.info("failed withing success percentage {}", result);
    }

    @Override
    public void onTestFailedWithTimeout(final ITestResult result) {
        log.info("failed with timeout {}", result);
    }
}
