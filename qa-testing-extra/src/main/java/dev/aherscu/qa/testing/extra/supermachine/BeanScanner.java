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

import java.util.*;
import java.util.stream.*;

import dev.aherscu.qa.testing.extra.supermachine.traverser.*;

/**
 * A scanner traversing an object graph.
 *
 * @param <T>
 *            the type of the traversed items.
 */
public final class BeanScanner<T> extends Scanner<T> {

    private BeanScanner(Stream<T> stream) {
        super(stream);
    }

    /**
     * Creates a bean scanner.
     *
     * @param root
     *            the root object from which the graph will be traversed.
     * @param <X>
     *            the type of the root object.
     * @return a scanner for the root object.
     */
    public static <X> Scanner<X> from(X root) {
        return new BeanScanner<>(Stream.of(root));
    }

    /**
     * Traverses the object graph for each items of this scanner and returns a
     * scanner emitting the first item of the given type found in each branch.
     *
     * @param clazz
     *            the type selector.
     * @param <X>
     *            type of the selector and resulting items.
     * @return a scanner emitting the selected items.
     */
    @Override
    public <X> Scanner<X> find(Class<X> clazz) {
        return new BeanScanner<>(stream().flatMap(x -> traverse(x, clazz)))
            .superFind(clazz);
    }

    /**
     * Traverses the object graph for each items of this scanner and returns a
     * scanner emitting all found items of the given type.
     *
     * @param clazz
     *            the type selector.
     * @param <X>
     *            type of the selector and resulting items.
     * @return a scanner emitting the selected items.
     */
    @Override
    public <X> Scanner<X> walk(Class<X> clazz) {
        return new BeanScanner<>(stream().flatMap(x -> traverse(x, null)))
            .superFind(clazz);
    }

    @Override
    protected <X> Scanner<X> create(Stream<X> source) {
        return new BeanScanner<>(source);
    }

    private <X> Scanner<X> superFind(Class<X> clazz) {
        return super.walk(clazz);
    }

    private <X> Stream<Object> traverse(T root, Class<X> stopClass) {
        LinkedList<Object> list = new LinkedList<>();
        Traverser.traverse(root, null, stopClass, list::add);
        return list.stream();
    }
}
