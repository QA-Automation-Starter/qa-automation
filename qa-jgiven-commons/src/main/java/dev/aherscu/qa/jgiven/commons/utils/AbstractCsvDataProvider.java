/*
 * Copyright 2024 Adrian Herscu
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
package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.testing.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.testng.annotations.*;

import com.opencsv.bean.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic CSV data provider; you should specialize it for your type by
 * overriding {@link #type()}.
 *
 * <pre>
 * public static final class FooCsvDataProvider
 *     extends AbstractCsvDataProvider {
 *     &#64;Override
 *     protected Class<?> type() {
 *         return Foo.class;
 *     }
 * }
 * </pre>
 *
 * then you can use it like this:
 * 
 * <pre>
 * &#64;Test(dataProviderClass = FooCsvDataProvider.class, dataProvider = AbstractCsvDataProvider.DATA)
 * public void shouldReadFromCsv(final Foo value) {...}
 * </pre>
 *
 * @author aherscu
 */
@NoArgsConstructor // data provider classes must have a no-args ctor
@Slf4j
// NOTE: cannot use generic types due to TestNG restriction
public abstract class AbstractCsvDataProvider {
    public static final String DATA = "data";

    static String csvFileFor(final Method method) {
        return method.getDeclaringClass().getSimpleName()
            + DOT + method.getName()
            + ".csv";
    }

    @DataProvider(name = DATA)
    @SneakyThrows
    public Iterator<Object> data(final Method method) {
        try (val csvReader = new InputStreamReader(
            getRelativeResourceAsStream(method.getDeclaringClass(),
                csvFileFor(method)))) {
            return csvBuilderFor(csvReader)
                .build()
                .iterator();
        }
    }

    /**
     * @param csvReader
     *            the reader for associated CSV file
     * @return iterator over deserialized CSV data
     */
    protected CsvToBeanBuilder<Object> csvBuilderFor(final Reader csvReader) {
        return new CsvToBeanBuilder<>(csvReader)
            .withType(type());
    }

    /**
     * @return the type of data that should be deserialized
     */
    abstract protected Class<?> type();
}
