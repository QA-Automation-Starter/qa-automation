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

package dev.aherscu.qa.tester.utils;

import static com.google.common.util.concurrent.SimpleTimeLimiter.*;
import static java.util.concurrent.TimeUnit.*;

import java.time.*;
import java.util.concurrent.*;

import lombok.*;
import lombok.experimental.*;

@UtilityClass
public class ExecutorUtils {
    public final static ExecutorService EXECUTOR_SERVICE = Executors
        .newCachedThreadPool(runnable -> {
            val thread = new Thread(runnable);
            // IMPORTANT otherwise the JVM may get stuck waiting for
            // RemoteWebDriver calls to complete
            thread.setDaemon(true);
            return thread;
        });

    public static <T> T timeout(
        final Callable<T> callable,
        final Duration duration)
        throws Throwable {
        return create(EXECUTOR_SERVICE)
            .callWithTimeout(callable, duration.getSeconds(), SECONDS);
    }
}
