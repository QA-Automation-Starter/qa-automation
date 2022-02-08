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
package dev.aherscu.qa.tester.utils.yaml;

import java.io.*;
import java.text.*;

import lombok.extern.slf4j.*;

@Slf4j
abstract class AbstractYamlStream<T> implements Closeable {
    protected final T yamlStream;

    AbstractYamlStream(final T yamlStream) {
        if (isLegal(yamlStream)) {
            this.yamlStream = yamlStream;
        } else
            throw new IllegalArgumentException(
                MessageFormat.format("{0}", yamlStream)); //$NON-NLS-1$
    }

    private static boolean isLegal(final Object yamlStream) {
        return isReader(yamlStream) || isWriter(yamlStream);
    }

    private static boolean isReader(final Object yamlStream) {
        return yamlStream instanceof com.esotericsoftware.yamlbeans.YamlReader;
    }

    private static boolean isWriter(final Object yamlStream) {
        return yamlStream instanceof com.esotericsoftware.yamlbeans.YamlWriter;
    }

    private static <T> T self(final Class<T> type, final Object obj) {
        return type.cast(obj);
    }

    @Override
    public final void close() throws IOException {
        log.debug("closing YAML stream {}", yamlStream); //$NON-NLS-1$

        if (isReader(yamlStream)) {
            self(com.esotericsoftware.yamlbeans.YamlReader.class, yamlStream)
                .close();
            return;
        }

        if (isWriter(yamlStream)) {
            self(com.esotericsoftware.yamlbeans.YamlWriter.class, yamlStream)
                .close();
            return;
        }

        // should not happen
        throw new InternalError();
    }
}
