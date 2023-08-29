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

package dev.aherscu.qa.testing.extra;

import static dev.aherscu.qa.testing.utils.ThreadUtils.*;

import java.util.concurrent.*;

import org.testng.annotations.*;
import org.testng.internal.thread.*;

import lombok.*;

public class ParallelLoopTest {
    // NOTE: with less interval will spend more time switching threads
    private static final int INTERVAL_MS = 10;
    // NOTE: with fewer repetitions will be more likely to fail due to thread
    // setup time
    private static final int REPETITIONS = 1_000;
    private static final int TIMEOUT_MS  = REPETITIONS * INTERVAL_MS * 3 / 10;

    @Test(timeOut = TIMEOUT_MS,
        expectedExceptions = ThreadTimeoutException.class)
    public void selfTest() {
        for (var i = 0; i < REPETITIONS; i++)
            sleep(INTERVAL_MS);
    }

    @Test(timeOut = TIMEOUT_MS,
        groups = { "time-sensitive" })
    @Ignore // ISSUE not stable when running with 2 processors
    public void shouldLoopFasterInParallel() {
        ParallelLoop.PROTOTYPE
            .withThreadPool(new ForkJoinPool(10))
            .withRepetitions(REPETITIONS)
            .run(__ -> {
                sleep(INTERVAL_MS);
            });
    }

    @Test(timeOut = TIMEOUT_MS,
        expectedExceptions = ThreadTimeoutException.class)
    public void shouldLoopSlowly() {
        ParallelLoop.PROTOTYPE
            .withRepetitions(REPETITIONS)
            .run(__ -> {
                sleep(INTERVAL_MS);
            });
    }
}
