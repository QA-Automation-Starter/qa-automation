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

import static org.apache.commons.lang3.Range.*;

import java.util.*;
import java.util.function.*;

import org.apache.commons.lang3.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Abstract spliterator over a numeric range of non-nulls.
 *
 * @param <C>
 *            the concrete spliterator
 * @param <T>
 *            the type over which the concrete spliterator runs
 */
@Slf4j
public abstract class RangeSpliterator<C extends Spliterator<T>, T>
    implements Spliterator<T> {
    private final long  atom;
    private Range<Long> range;
    private long        index = -1;

    /**
     * Spliterator running over specified range, splitting to atomic units.
     *
     * @param range
     *            range for this spliterator
     */
    public RangeSpliterator(final Range<Long> range) {
        this(range, 1);
    }

    /**
     * Spliterator running over specified range, splitting until specified unit
     * size.
     *
     * @param range
     *            range for this spliterator
     * @param atom
     *            never split beyond
     */
    public RangeSpliterator(
        final Range<Long> range,
        final long atom) {
        this.range = range;
        this.atom = atom;
        log.debug("inited with size {}, atom {}, spanning {}",
            estimateSize(), atom, range);
    }

    @Override
    public final boolean tryAdvance(
        final Consumer<? super T> action) {
        log.debug("before advance: current item {} within {}",
            index, range);
        if (hasNext()) {
            action.accept(item(next()));
            return true;
        }
        log.debug("no more items");
        return false;
    }

    @Override
    public final C trySplit() {
        if (estimateSize() <= atom) {
            log.debug(
                "chunk size {} is less/equal than atom {}; can no longer split",
                estimateSize(), atom);
            return null;
        }

        log.debug("before split: chunk size {} spanning {}",
            estimateSize(), range);
        val oldStartIndexForChunk = range.getMinimum();
        val chunkSize = estimateSize();
        range = between(
            range.getMinimum() + chunkSize / 2,
            range.getMaximum());

        return subSpliterator(between(
            oldStartIndexForChunk,
            oldStartIndexForChunk + chunkSize / 2 - 1));
    }

    @Override
    public final long estimateSize() {
        return range.getMaximum() - range.getMinimum() + 1;
    }

    @Override
    public final int characteristics() {
        return NONNULL | SIZED;
    }

    /**
     * Override to supply requested item per specified index.
     *
     * @param index
     *            the index of new requested item
     * @return the item
     */
    protected abstract T item(@SuppressWarnings("hiding") long index);

    /**
     * Override to create a new sub-spliterator per specified range.
     *
     * @param range
     *            the sub-range
     * @return the concrete sub-spliterator
     */
    protected abstract C subSpliterator(
        @SuppressWarnings("hiding") Range<Long> range);

    private boolean hasNext() {
        return index < range.getMaximum();
    }

    private long next() {
        return index < range.getMinimum()
            ? index = range.getMinimum()
            : ++index;
    }
}
