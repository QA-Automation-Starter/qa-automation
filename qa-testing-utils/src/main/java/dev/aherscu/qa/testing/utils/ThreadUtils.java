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
package dev.aherscu.qa.testing.utils;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.experimental.*;

/**
 * Thread utilities.
 *
 * @author aherscu
 *
 */
@SuppressFBWarnings(value = "MDM_THREAD_YIELD",
    justification = "the meaning of this utility class is to make thread "
        + "utilities easier to use by converting checked exception "
        + "into unchecked execptions")
@UtilityClass
public final class ThreadUtils {
    /**
     * Safe alternative to {@link Thread#join()}.
     *
     * @param thread
     *            the thread to join
     * @param timeoutMs
     *            timeout in milliseconds
     */
    @SneakyThrows(InterruptedException.class)
    public static void join(final Thread thread, final int timeoutMs) {
        thread.join(timeoutMs);
    }

    /**
     * Safe alternative to {@link Thread#sleep(long)}
     *
     * @param millis
     *            how many milliseconds to sleep
     */
    @SneakyThrows(InterruptedException.class)
    public static void sleep(final long millis) {
        Thread.sleep(millis);
    }
}
