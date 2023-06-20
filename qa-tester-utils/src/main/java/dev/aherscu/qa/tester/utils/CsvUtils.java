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

import static java.util.Objects.*;

import java.io.*;
import java.nio.charset.*;
import java.util.stream.*;

import org.apache.commons.io.input.*;

import com.opencsv.*;

import lombok.*;
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
    @SneakyThrows
    public static Stream<String[]> stream(final InputStream inputStream) {
        // NOTE: M$-Excel encodes CSV files as UTF-8-BOM !!!
        try (
            val bomInputStream =
                new BOMInputStream(requireNonNull(inputStream));
            val streamReader =
                new InputStreamReader(bomInputStream, StandardCharsets.UTF_8);
            val csvReader = new CSVReaderBuilder(streamReader).build()) {
            return StreamSupport.stream(csvReader.spliterator(), false);
        }
    }
}
