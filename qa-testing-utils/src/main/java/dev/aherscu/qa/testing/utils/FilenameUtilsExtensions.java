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

import java.io.*;

import javax.annotation.concurrent.*;

import org.apache.commons.io.*;

import lombok.experimental.*;

/**
 * Provides enhanced file-name utilities.
 *
 * @author aherscu
 *
 */
@UtilityClass
@ThreadSafe
public class FilenameUtilsExtensions extends FilenameUtils {
    /**
     * Gets the base name, minus the full path and extension, from a full
     * filename.
     *
     * @param file
     *            the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none
     *         exists
     *
     * @see FilenameUtils#getBaseName(String)
     */
    public String getBaseName(final File file) {
        return FilenameUtils.getBaseName(file.toString());
    }
}
