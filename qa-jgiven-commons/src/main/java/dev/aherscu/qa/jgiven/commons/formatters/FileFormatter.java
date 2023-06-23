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
package dev.aherscu.qa.jgiven.commons.formatters;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.apache.commons.io.FileUtils.*;

import java.io.*;
import java.lang.annotation.*;
import java.nio.charset.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

import lombok.*;

/**
 * Formatter for {@link File}s.
 * 
 * @author aherscu
 *
 */
@ThreadSafe
@SuppressWarnings("boxing")
public class FileFormatter implements
    AnnotationArgumentFormatter<FileFormatter.Annotation> {

    private static String contentsOf(
        final File file,
        final int maxFileSizeToRead) throws IOException {
        return file.exists() && isFileSmallEnough(file, maxFileSizeToRead)
            ? readFileToString(file, StandardCharsets.UTF_8)
            : String.format("> %s bytes", //$NON-NLS-1$
                maxFileSizeToRead);
    }

    private static boolean isFileSmallEnough(
        final File file,
        final int maxFileSizeToRead) {
        return sizeOf(file) <= maxFileSizeToRead || -1 == maxFileSizeToRead;
    }

    @SneakyThrows(IOException.class)
    @Override
    public String format(final Object argumentToFormat,
        final FileFormatter.Annotation annotation) {
        val contentsOfFile = contentsOf((File) argumentToFormat,
            annotation.maxFileSizeToRead());
        return String.format("%s (<-- %s)", //$NON-NLS-1$
            -1 == annotation.value()
                ? contentsOfFile
                : left(contentsOfFile, annotation.value()),
            argumentToFormat);
    }

    /**
     * Formatter annotation for {@link File}s. If the file exists and its size
     * are below specified {@link Annotation#maxFileSizeToRead} then its
     * contents will be written to the report, otherwise the file name itself
     * will be written to the report.
     *
     * @author aherscu
     *
     */
    @AnnotationFormat(FileFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        /**
         * @return the maximum file size to read, larger files will not be read;
         *         defaults to -1, meaning no maximum will be imposed
         */
        int maxFileSizeToRead() default -1;

        /**
         * @return amount of file text to output in the report; defaults to -1,
         *         meaning the entire file will be rendered in the report
         */
        int value() default -1;
    }
}
