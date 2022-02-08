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

package dev.aherscu.qa.tester.utils.supermachine;

import java.util.stream.*;

/**
 * A scanner iterating over items of a stream. Nothing really fancy.
 * 
 * @param <X>
 *            the types of the stream items.
 */
public class StreamScanner<X> extends Scanner<X> {

    private StreamScanner(Stream<X> stream) {
        super(stream);
    }

    /**
     * Create a scanner iterating over a stream.
     * 
     * @param stream
     *            the stream to scan.
     * @param <X>
     *            the type of stream items.
     * @return
     */
    public static <X> Scanner<X> scan(Stream<X> stream) {
        return new StreamScanner<>(stream);
    }

    @Override
    protected <X> Scanner<X> create(Stream<X> source) {
        return new StreamScanner<X>(source);
    }
}
