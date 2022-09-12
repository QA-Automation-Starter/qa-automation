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

import static dev.aherscu.qa.tester.utils.ReadMultiWriteLock.*;
import static dev.aherscu.qa.tester.utils.ThreadUtils.*;

import java.util.concurrent.*;

import org.testng.annotations.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ReadMultiWriteLockTest {
    private static final int   MAX_DELAY_MS  = 10;
    private final ParallelLoop PARALLEL_LOOP = ParallelLoop.PROTOTYPE
        .withThreadPool(new ForkJoinPool(100))
        // NOTE: when running with trace may take long time
        .withRepetitions(200_000);

    @Test(groups = { "time-sensitive" })
    public void shouldExclusivelyLockForReadsAndWrites() {
        val sharedState = new State(MAX_DELAY_MS);
        PARALLEL_LOOP
            .run(id -> State.randomReadWrite()
                ? writeLocking(() -> sharedState.write(id))
                : readLocking(() -> sharedState.read(id)));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldExecuteReadingCriticalSectionWithoutValue() {
        readLocking((Runnable) () -> {
            throw new RuntimeException("reading critical section executed");
        });
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldExecuteWritingCriticalSectionWithoutValue() {
        writeLocking((Runnable) () -> {
            throw new RuntimeException("writing critical section executed");
        });
    }

    @Test(expectedExceptions = ExecutionException.class,
            groups = { "time-sensitive" })
    public void shouldFailIfNeitherLocked() {
        val sharedState = new State(MAX_DELAY_MS);
        PARALLEL_LOOP
            .run(id -> State.randomReadWrite()
                ? sharedState.write(id)
                : sharedState.read(id));
    }

    @Test(expectedExceptions = ExecutionException.class,
            groups = { "time-sensitive" })
    public void shouldFailIfNotReadLocked() {
        val sharedState = new State(MAX_DELAY_MS);
        PARALLEL_LOOP
            .run(id -> State.randomReadWrite()
                ? writeLocking(() -> sharedState.write(id))
                : sharedState.read(id));
    }

    @Test(expectedExceptions = ExecutionException.class,
            groups = { "time-sensitive" })
    public void shouldFailIfNotWriteLocked() {
        val sharedState = new State(MAX_DELAY_MS);
        PARALLEL_LOOP
            .run(id -> State.randomReadWrite()
                ? sharedState.write(id)
                : readLocking(() -> sharedState.read(id)));
    }

    @RequiredArgsConstructor
    private static final class State {
        private final int maxDelay;
        private Status    readStatus, writeStatus;

        static int randomDelay(final int maxMs) {
            return ThreadLocalRandom.current().nextInt(0, maxMs);
        }

        static boolean randomReadWrite() {
            return ThreadLocalRandom.current().nextBoolean();
        }

        int read(final int id) {
            if (Status.STARTED == writeStatus)
                throw new IllegalStateException("other thread is writing");
            readStatus = Status.STARTED;
            log.trace(">>> start reading {}", id);
            sleep(randomDelay(maxDelay));
            log.trace("<<< end reading {}", id);
            readStatus = Status.ENDED;
            return id;
        }

        int write(final int id) {
            if (Status.STARTED == readStatus)
                throw new IllegalStateException("other thread is reading");
            writeStatus = Status.STARTED;
            log.trace(">>> start writing {}", id);
            sleep(randomDelay(maxDelay));
            log.trace("<<< end writing {}", id);
            writeStatus = Status.ENDED;
            return id;
        }

        private enum Status {
            STARTED, ENDED
        }
    }
}
