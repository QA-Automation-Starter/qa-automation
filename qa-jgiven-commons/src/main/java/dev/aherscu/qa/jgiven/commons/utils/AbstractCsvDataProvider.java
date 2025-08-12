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

import org.testng.annotations.*;

import com.opencsv.bean.*;

import dev.aherscu.qa.testing.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic CSV data provider; you should specialize it for your type by
 * overriding {@link #type()}, like this:
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
 * Only a single object parameter is supported. If your test method requires
 * multiple parameters, then consider grouping them in a class instead.
 *
 * @author aherscu
 */
@NoArgsConstructor // data provider classes must have a no-args ctor
@Slf4j
// NOTE: cannot use generic types due to TestNG restriction
// java.lang.ClassCastException: class
// sun.reflect.generics.reflectiveObjects.TypeVariableImpl cannot be cast to
// class java.lang.Class
// (sun.reflect.generics.reflectiveObjects.TypeVariableImpl and java.lang.Class
// are in module java.base of loader 'bootstrap')
public abstract class AbstractCsvDataProvider {
    public static final String DATA = "data";

    /**
     * Default implementation for mapping an instance method to a CSV file.
     * Override this method if you want to use a different mapping.
     *
     * @param method the method for which the CSV file should be
     * @return the name of the CSV file that should be used for the given
     *         method; the file is expected to be in the same package as the
     *         method's declaring class, and to have the same name as the method
     *         with a ".csv" extension; for example, if the method is
     *         {@code com.example.MyClass#myMethod()}, then the expected file is
     *         {@code com/example/MyClass/myMethod.csv}
     * @throws NullPointerException if the method is {@code null}
     * @throws IllegalArgumentException if the method's declaring class is
     *         {@code null} or if the method's name is {@code null} or empty
     * @see #csvBuilderFor(Reader)
     * @see #data(Method)
     * @see #type()
     * @see ClassUtilsExtensions#getRelativeResourceAsStream(Class, String)
     */
    protected String csvFileFor(final Method method) {
        return method.getDeclaringClass().getSimpleName()
            + DOT + method.getName()
            + ".csv";
    }

    @DataProvider(name = DATA)
    @SneakyThrows
    public Object[] data(final Method method) {
        try (val csvReader = new InputStreamReader(
            getRelativeResourceAsStream(method.getDeclaringClass(),
                csvFileFor(method)))) {
            return csvBuilderFor(csvReader)
                .build()
                .parse()
                .toArray();
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
