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

import static java.util.Objects.*;

import java.io.*;
import java.nio.charset.*;
import java.util.stream.*;

import org.apache.commons.io.input.*;

import com.opencsv.*;

import lombok.experimental.*;

@UtilityClass
public final class CsvUtils {

    /**
     * Provides a Java 8 stream wrapper around a CSV I/O stream.
     * 
     * @param inputStream
     *            input stream for CSV data
     * @return non parallel stream of CSV lines parsed as String array
     */
    public static Stream<String[]> stream(final InputStream inputStream) {
        return StreamSupport.stream(
            new CSVReaderBuilder(
                new InputStreamReader(
                    // NOTE: M$-Excel encodes CSV files as UTF-8-BOM !!!
                    new BOMInputStream(requireNonNull(inputStream)),
                    StandardCharsets.UTF_8))
                        .build()
                        .spliterator(),
            false); // non parallel stream
    }
}
