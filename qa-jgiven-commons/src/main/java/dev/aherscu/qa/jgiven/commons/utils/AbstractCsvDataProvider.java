/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
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
 * Generic CSV data provider.
 *
 * @author aherscu
 */
@NoArgsConstructor // data provider classes must have a no-args ctor
@Slf4j
public abstract class AbstractCsvDataProvider {
    static String csvFileFor(final Method method) {
        return method.getDeclaringClass().getSimpleName()
            + DOT + method.getName()
            + ".csv";
    }

    @DataProvider
    @SneakyThrows
    public Iterator<Object> data(final Method method) {
        try (val csvReader = new InputStreamReader(
            getRelativeResourceAsStream(method.getDeclaringClass(),
                csvFileFor(method)))) {
            return iteratorFor(csvReader);
        }
    }

    protected Iterator<Object> iteratorFor(final Reader csvReader) {
        return new CsvToBeanBuilder<>(csvReader)
            .withType(type())
            .build()
            .iterator();
    }

    abstract protected Class<?> type();
}
