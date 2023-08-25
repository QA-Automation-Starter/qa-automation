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
package dev.aherscu.qa.testing.extra.yaml;

import java.io.*;

import com.esotericsoftware.yamlbeans.*;

import dev.aherscu.qa.testing.utils.*;
import lombok.extern.slf4j.*;

/**
 * Wraps the {@link com.esotericsoftware.yamlbeans.YamlWriter} in order to
 * implement {@link java.io.Closeable}.
 * 
 * @param <T>
 *            the type to write
 *
 * @author aherscu
 */
@Slf4j
public class YamlWriter<T>
    extends AbstractYamlStream<com.esotericsoftware.yamlbeans.YamlWriter> {

    /**
     * Creates a YAML writer based on a supplied file; you should call
     * {@link #close()} in order to release it.
     * 
     * @param file
     *            the file
     * @throws IOException
     *             if failed to write to file
     */
    public YamlWriter(final File file) throws IOException {
        // NOTE: the reader created below should be closed by calling
        // YamlWriter.close
        this(FileUtilsExtensions.fileWriter(file));
        log.debug("created YAML writer {} for {}", this, file.toString()); //$NON-NLS-1$
    }

    /**
     * @param writer
     *            the writer
     * 
     * @see com.esotericsoftware.yamlbeans.YamlWriter#YamlWriter(Writer)
     */
    public YamlWriter(final Writer writer) {
        super(new com.esotericsoftware.yamlbeans.YamlWriter(writer));
    }

    /**
     * @param object
     *            the type to read
     * @throws YamlException
     *             if anything fails
     * 
     * @see com.esotericsoftware.yamlbeans.YamlWriter#write(Object)
     */
    public void write(final T object) throws YamlException {
        log.debug("using YAML writer {} on object {}", this, object); //$NON-NLS-1$
        yamlStream.write(object);
    }
}
