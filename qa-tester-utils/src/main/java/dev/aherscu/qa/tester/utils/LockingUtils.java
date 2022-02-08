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

import lombok.*;

/**
 * Based on
 * http://tutorials.jenkov.com/java-concurrency/read-write-locks.html#simple
 *
 * <p>
 * Allows for multiple writers.
 * </p>
 */

public class LockingUtils {

    private static final ReadMultiWriteLock lock = new ReadMultiWriteLock();

    @SneakyThrows
    public static void readLocking(final Runnable c) {
        lock.readingAquire();
        try {
            c.run();
        } finally {
            lock.readingRelease();
        }
    }

    @SneakyThrows
    public static void writeLocking(final Runnable c) {
        lock.writingAcquire();
        try {
            c.run();
        } finally {
            lock.readingRelease();
        }
    }
}
