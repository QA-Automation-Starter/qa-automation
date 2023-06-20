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

package dev.aherscu.qa.tester.utils;

import static java.util.concurrent.Executors.*;

import java.util.concurrent.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Based on
 * http://tutorials.jenkov.com/java-concurrency/read-write-locks.html#simple
 * 
 * <p>
 * Allows for multiple writers.
 * </p>
 */
@Slf4j
public class ReadMultiWriteLock {

    private static final ReadMultiWriteLock lock    = new ReadMultiWriteLock();
    private int                             readers = 0;
    private int                             writers = 0;

    /**
     * Guards specified critical section for reading purposes.
     *
     * @param criticalSection
     *            the critical section
     * @return value returned by critical section
     */
    @SneakyThrows
    public static <T> T readLocking(final Callable<T> criticalSection) {
        lock.readingAquire();
        try {
            return criticalSection.call();
        } finally {
            lock.readingRelease();
        }
    }

    /**
     * Guards specified critical section for reading purposes.
     *
     * @param criticalSection
     *            the critical section
     */
    public static void readLocking(final Runnable criticalSection) {
        readLocking(callable(criticalSection));
    }

    /**
     * Guards specified critical section for writing purposes.
     *
     * @param criticalSection
     *            the critical section
     * @return value returned by critical section
     */
    @SneakyThrows
    public static <T> T writeLocking(final Callable<T> criticalSection) {
        lock.writingAcquire();
        try {
            return criticalSection.call();
        } finally {
            lock.writingRelease();
        }
    }

    /**
     * Guards specified critical section for writing purposes.
     *
     * @param criticalSection
     *            the critical section
     */
    public static void writeLocking(final Runnable criticalSection) {
        writeLocking(callable(criticalSection));
    }

    /**
     * Waits for writers to finish and accounts for another reader lock.
     * 
     * @throws InterruptedException
     *             if this thread was interrupted
     */
    public synchronized void readingAquire() throws InterruptedException {
        while (writers > 0) {
            log.trace("blocking read -- {} writers running", writers);
            wait();
        }
        readers++;
        log.trace("aquired {} reading locks", readers);
    }

    /**
     * Accounts for one less reader lock.
     */
    public synchronized void readingRelease() {
        readers--;
        notifyAll();
    }

    /**
     * Waits for readers to finish and accounts for another writer lock.
     * 
     * @throws InterruptedException
     *             if this thread was interrupted
     */
    public synchronized void writingAcquire() throws InterruptedException {
        while (readers > 0) {
            log.trace("blocking write -- {} readers running", readers);
            wait();
        }
        writers++;
        log.trace("aquired {} writing locks", writers);
    }

    /**
     * Accounts for one less writer lock.
     */
    public synchronized void writingRelease() {
        writers--;
        notifyAll();
    }
}
