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

package dev.aherscu.qa.jgiven.commons.utils;

import org.testng.*;

public final class ExceptionPerThreadListener
    extends AbstractTestListener {

    private static final ThreadLocal<Throwable> throwable =
        new ThreadLocal<>();

    /**
     * @return the throwable that was thrown while running the test method, or
     *         null if no exception was thrown
     */
    public static Throwable throwable() {
        return throwable.get();
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        throwable.set(result.getThrowable());
    }
}
