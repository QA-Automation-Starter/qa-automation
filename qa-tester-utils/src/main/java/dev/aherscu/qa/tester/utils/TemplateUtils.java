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

import java.io.*;
import java.nio.charset.*;

import org.apache.commons.lang3.*;

import com.samskivert.mustache.*;

import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Template utilities.
 * 
 * @author aherscu
 *
 */
@UtilityClass
public final class TemplateUtils {
    /**
     * @param compiler
     *            the {@link com.samskivert.mustache.Mustache.Compiler} to use
     * @return the loader
     */
    public static Loader using(
        final Mustache.Compiler compiler) {
        return new TemplateUtils.Loader(compiler);
    }

    /**
     * Loads <a href="https://github.com/samskivert/jmustache">Mustache
     * Templates</a>.
     *
     * @author aherscu
     *
     */
    @Slf4j
    public static class Loader {
        final Mustache.Compiler compiler;

        /**
         * @param compiler
         *            the {@link com.samskivert.mustache.Mustache.Compiler} to
         *            use
         */
        public Loader(final Mustache.Compiler compiler) {
            this.compiler = compiler;
        }

        /**
         * Loads a template from a file.
         *
         * @param file
         *            the file to load from
         * @return the template
         * @throws IOException
         *             if no such file
         */
        public Template loadFrom(final File file) throws IOException {
            log.debug("loading template from {}", file.toString()); //$NON-NLS-1$
            try (final Reader fileReader =
                FileUtilsExtensions.fileReader(file)) {
                return loadFrom(fileReader);
            }
        }

        /**
         * Loads a template from a stream reader.
         *
         * @param reader
         *            the stream reader
         * @return the template
         */
        public Template loadFrom(final Reader reader) {
            return compiler.defaultValue(StringUtils.EMPTY)
                .compile(reader);
        }

        /**
         * Loads a template from a resource.
         *
         * @param name
         *            the name of the resource
         * @return the template
         */
        public Template loadFrom(final String name) {
            log.debug("loading template resource from {}", name); //$NON-NLS-1$
            return loadFrom(new InputStreamReader(
                TemplateUtils.class.getResourceAsStream(name),
                StandardCharsets.UTF_8));
        }
    }
}
