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
package dev.aherscu.qa.testing.utils.yaml;

import java.io.*;

import com.esotericsoftware.yamlbeans.*;

import dev.aherscu.qa.testing.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Wraps the {@link com.esotericsoftware.yamlbeans.YamlReader} in order to
 * implement {@link java.io.Closeable}.
 * 
 * @param <T>
 *            the type to read
 *
 * @author aherscu
 */
@Slf4j
public class YamlReader<T>
    extends AbstractYamlStream<com.esotericsoftware.yamlbeans.YamlReader> {

    /**
     * Creates a file-based YAML reader; you should call {@link #close()} in
     * order to release it.
     * 
     * @param from
     *            the file to read from
     * @throws IOException
     *             if the file cannot be open for reading
     * 
     * @see com.esotericsoftware.yamlbeans.YamlReader#YamlReader(Reader)
     */
    public YamlReader(final File from) throws IOException {
        // NOTE: the reader created below should be closed by calling
        // YamlReader.close
        this(FileUtilsExtensions.fileReader(from));
        log.debug("creating YAML reader {} for {}", this, from.toString()); //$NON-NLS-1$
    }

    /**
     * @param reader
     *            the reader
     * 
     * @see com.esotericsoftware.yamlbeans.YamlReader#YamlReader(Reader)
     */
    public YamlReader(final Reader reader) {
        super(new com.esotericsoftware.yamlbeans.YamlReader(reader));
    }

    /**
     * @param type
     *            the type to read
     * @return object read
     * @throws YamlException
     *             if anything fails
     * 
     * @see com.esotericsoftware.yamlbeans.YamlReader#read(Class)
     */
    public T read(final Class<T> type)
        throws com.esotericsoftware.yamlbeans.YamlException {
        log.debug("using YAML reader {} on type {}", this, type.getName()); //$NON-NLS-1$
        val object = yamlStream.read(type);

        if (null != object)
            return object;

        throw new YamlException(L10N.MESSAGES.badYamlFormat());
    }
}
