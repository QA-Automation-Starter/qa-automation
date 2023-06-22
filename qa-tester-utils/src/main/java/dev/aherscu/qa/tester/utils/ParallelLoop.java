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

import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Parallel looping with specified threads, repetitions and block of code.
 * 
 * <p>
 * NOTE: there is a library class at
 * https://github.com/pablormier/parallel-loops
 * </p>
 */
@Slf4j
@AllArgsConstructor
public final class ParallelLoop {
    /**
     * The default parallel loop; chain with threadPool and repetitions to
     * configure.
     *
     * @see #ParallelLoop()
     */
    public static final ParallelLoop      PROTOTYPE = new ParallelLoop();
    @With
    private final AbstractExecutorService threadPool;
    @With
    private final int                     repetitions;

    /**
     * Constructs a default parallel loop with one thread and one repetition.
     */
    public ParallelLoop() {
        threadPool = new ForkJoinPool(1);
        repetitions = 1;
    }

    /**
     * Runs specified function in configured loop.
     * 
     * @param function
     *            the function to run; is called with the run identifier and
     *            expected to return it
     *
     * @throws CancellationException
     *             if the computation was cancelled
     */
    @SneakyThrows
    public void run(final Function<Integer, Integer> function) {
        threadPool
            .submit(() -> {
                IntStream.range(0, repetitions)
                    .parallel()
                    .forEach(id -> log.trace("run id {}", function.apply(id)));
                return null; // just to make get() below work...
            })
            .get();

    }

    /**
     * Runs specified consumer in configured loop.
     * 
     * @param consumer
     *            the consumer to run; is called with the run identifier.
     */
    public void run(final IntConsumer consumer) {
        run(id -> {
            consumer.accept(id);
            return id;
        });
    }
}
