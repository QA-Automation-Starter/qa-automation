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
package dev.aherscu.qa.tester.utils.logging;

import java.io.*;
import java.util.function.*;

import org.slf4j.*;

import edu.umd.cs.findbugs.annotations.*;

/**
 * Logs the information passing through an input stream at
 * <strong>trace</strong> level. The information will be logged upon
 * {@link #close()}.
 * 
 * @see Logger
 * 
 * @author aherscu
 * 
 */
@SuppressFBWarnings("LO_SUSPECT_LOG_PARAMETER")
public class LoggingInputStream
    extends InputStream {
    private final LoggingStreamHelper<InputStream> loggingStream;

    /**
     * Initializes a logging input stream.
     * 
     * @param input
     *            the input stream to log
     * @param logger
     *            the logger to use
     */
    public LoggingInputStream(
        final InputStream input,
        final Consumer<String> logger) {
        super();
        loggingStream = new LoggingStreamHelper<>(input, logger);
    }

    @Override
    public int read()
        throws IOException {
        return loggingStream.tee.read();
    }

    /**
     * Logs the input stream.
     */
    @Override
    public void close()
        throws IOException {
        super.close();
        loggingStream.close();
    }
}
