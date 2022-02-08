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

import static com.google.common.io.Files.*;
import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;

import lombok.*;
import lombok.experimental.*;

@UtilityClass
public class IOUtilsExtensions extends IOUtils {
    @SneakyThrows(IOException.class)
    public static String fromUTF8(final InputStream in) {
        return IOUtils.toString(in, UTF_8);
    }

    @SneakyThrows(IOException.class)
    public static Properties propertiesFrom(final File file) {
        val properties = new Properties();
        properties.load(newReader(file, UTF_8));
        return properties;
    }

    @SneakyThrows(IOException.class)
    public static List<String> readUTF8Lines(final InputStream in) {
        return IOUtils.readLines(in, UTF_8);
    }
}
