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
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import lombok.*;

@Ignore // not ready for prime time
public class RangeSpliteratorTest {

    private final ConcreteRangeSpliterator spliterator =
        new ConcreteRangeSpliterator(between(0L, 10L));

    @Test
    public void shouldIterate() {
        // TODO automate this
        spliterator.tryAdvance(System.out::println);
        spliterator.tryAdvance(System.out::println);
        spliterator.tryAdvance(System.out::println);
        spliterator.tryAdvance(System.out::println);
    }

    @Test
    public void shouldSplit() {
        // TODO automate this
        val subspliterator = spliterator.trySplit();

        subspliterator.tryAdvance(System.out::println);
        subspliterator.tryAdvance(System.out::println);
        subspliterator.tryAdvance(System.out::println);
        subspliterator.tryAdvance(System.out::println);
        subspliterator.tryAdvance(System.out::println);
        subspliterator.tryAdvance(System.out::println); // no more
    }

    @Test
    public void shouldStream() {
        StreamSupport.stream(spliterator, false)
            .forEach(System.out::println);
    }

    @Test
    public void shouldStreamParallel() {
        StreamSupport.stream(spliterator, true)
            .forEach(System.out::println);
    }

    private static class ConcreteRangeSpliterator
        extends RangeSpliterator<ConcreteRangeSpliterator, Integer> {

        private final ArrayList<Integer> ints = new ArrayList<>();

        public ConcreteRangeSpliterator(
            final Range<Long> range) {
            super(range);
            for (int i = range.getMinimum().intValue(); i <= range
                .getMaximum(); i++)
                ints.add(i);
        }

        @Override
        protected Integer item(final long index) {
            return ints.get((int) index);
        }

        @Override
        protected ConcreteRangeSpliterator subSpliterator(
            final Range<Long> range) {
            return new ConcreteRangeSpliterator(range);
        }
    }
}
