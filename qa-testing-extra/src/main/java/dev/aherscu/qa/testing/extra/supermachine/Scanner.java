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

package dev.aherscu.qa.testing.extra.supermachine;

import java.util.function.*;
import java.util.stream.*;

/**
 * Abstraction wrapping a data structure that can be walked through to produce
 * streams.
 *
 * @param <T>
 *            the type of elements composing the data structure.
 */
public abstract class Scanner<T> {

    private final Stream<T> stream;

    /**
     * Constructs a scanner out of a source stream.
     *
     * @param source
     *            the wrapped stream, will contain only one element when it is a
     *            root.
     */
    protected Scanner(Stream<T> source) {
        this.stream = source;
    }

    /**
     * Apply a function to every items of this scanner and returns a scanner
     * emitting the non-null results.
     *
     * @param extractor
     *            the function applied to each item, can return null.
     * @param <X>
     *            the returned type.
     * @return a scanner emitting non-null results of the function applied to
     *         items.
     */
    public <X> Scanner<X> extract(Function<T, X> extractor) {
        return create(stream.flatMap(x -> {
            X result = extractor.apply(x);
            return result != null ? Stream.of(result) : Stream.empty();
        }));
    }

    /**
     * Filters out items according to predicate.
     *
     * @param predicate
     *            the condition to emit the items.
     * @return a scanner emitting the items matching the predicate.
     */
    public Scanner<T> filter(Predicate<T> predicate) {
        return create(stream.filter(predicate));
    }

    /**
     * Scans the elements of the source and returns a scanner emitting relevant
     * items of the given type. This method can return fewer items according to
     * subclass logic. Subclasses implement the actual scanning algorithm.
     *
     * @param clazz
     *            the type selector.
     * @param <X>
     *            type of the selector and resulting items.
     * @return a scanner emitting the selected items.
     */
    public <X> Scanner<X> find(Class<X> clazz) {
        return walk(clazz);
    }

    /**
     * Extracts the items of this scanner as a stream.
     *
     * @return a stream of items.
     */
    public Stream<T> stream() {
        return stream;
    }

    /**
     * Transforms this scanner into one or more derived scanners and merges
     * their emitted items.
     *
     * @param fns
     *            the functions that will be applied to this scanner and produce
     *            the scanners to merge.
     * @param <X>
     *            the common type of items resulting of the scanners to merge.
     * @return a scanner merging the result of the composed scanners.
     */
    @SafeVarargs
    final public <X> Scanner<X> then(Function<Scanner<T>, Scanner<X>>... fns) {
        return create(stream.flatMap(x -> Stream.of(fns)
            .map(fn -> fn.apply(create(Stream.of(x))).stream())
            .reduce(Stream.empty(), Stream::concat)));
    }

    /**
     * Scans the elements of the source and returns a scanner emitting all
     * resulting items of the given type. Subclasses implement the actual
     * scanning algorithm.
     *
     * @param clazz
     *            the type selector.
     * @param <X>
     *            type of the selector and resulting items.
     * @return a scanner emitting the selected items.
     */
    public <X> Scanner<X> walk(Class<X> clazz) {
        @SuppressWarnings("unchecked")
        Stream<X> filteredStream = (Stream<X>) stream
            .filter(x -> clazz.isAssignableFrom(x.getClass()));
        return create(filteredStream);
    }

    /**
     * Constructs a scanner out of a source stream.
     *
     * @param source
     *            the wrapped stream, will contain only one element when it is a
     *            root.
     * @param <X>
     *            the type of elements in the source stream.
     * @return a scanner based on the given source.
     */
    protected abstract <X> Scanner<X> create(Stream<X> source);
}
