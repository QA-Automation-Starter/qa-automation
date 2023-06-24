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
package dev.aherscu.qa.testing.utils;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;

import java.io.*;
import java.nio.charset.*;

import org.apache.commons.collections.*;
import org.apache.commons.io.*;

import lombok.*;
import lombok.experimental.*;

/**
 * Provides encoding-safe file readers and writers.
 * 
 * @author aherscu
 *
 */
@UtilityClass
public final class FileUtilsExtensions extends FileUtils {
    /**
     * Reads lines from source file and appends them into target file, checking
     * whether the parent directory exists, and creating it if it does not
     * exist.
     * 
     * @param sourceFile
     *            the source file
     * @param targetFile
     *            the target file
     * 
     * @throws IOException
     *             in case of an I/O error
     */
    public static void append(final File sourceFile, final File targetFile)
        throws IOException {
        // NOTE LineIterator does not implement AutoClosable hence cannot be
        // used in a try-with-resource statement
        // see
        // http://stackoverflow.com/questions/6889697/close-resource-quietly-using-try-with-resources
        // for a generic utility to solve this kind of limitation
        @SuppressWarnings("resource")
        val it = lineIterator(sourceFile, UTF_8.toString());
        // noinspection TryFinallyCanBeTryWithResources
        try {
            writeLines(targetFile, IteratorUtils.toList(it), true);
        } finally {
            it.close();
        }
    }

    /**
     * Constructs a {@link File} by specified name relative to specified class.
     * 
     * @param clazz
     *            the class from which package to compute
     * @param name
     *            the name
     * @return the file refered by specified name relative to specified class
     */
    public static File file(final Class<?> clazz, final String name) {
        return new File(requireNonNull(clazz.getResource(name),
            name + "resource missing")
            .getPath());
    }

    /**
     * Constructs a {@link FileReader} using {@link StandardCharsets#UTF_8}.
     * 
     * @param from
     *            the file from which to read
     * @return the reader
     * @throws IOException
     *             in case of I/O failure
     */
    public static Reader fileReader(final File from) throws IOException {
        return new InputStreamReader(new FileInputStream(from), UTF_8);
    }

    /**
     * Constructs a {@link FileWriter} using {@link StandardCharsets#UTF_8}.
     * 
     * @param to
     *            the file to which to write
     * @return the writer
     * @throws IOException
     *             in case of I/O failure
     */
    public static Writer fileWriter(final File to) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(to), UTF_8);
    }

    /**
     * Converts an array of file extensions to suffixes for use with
     * IOFileFilters.
     *
     * @param extensions
     *            an array of extensions. Format: {"java", "xml"}
     * @return an array of suffixes. Format: {".java", ".xml"}
     */
    public static String[] toSuffixes(final String... extensions) {
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = DOT + extensions[i];
        }
        return suffixes;
    }
}
