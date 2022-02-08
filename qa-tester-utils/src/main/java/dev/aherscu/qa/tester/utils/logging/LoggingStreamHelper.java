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
import java.io.ByteArrayOutputStream;
import java.lang.SuppressWarnings;
import java.util.function.*;

import org.apache.commons.io.input.*;
import org.apache.commons.io.output.*;

import edu.umd.cs.findbugs.annotations.*;

@SuppressFBWarnings("LO_SUSPECT_LOG_PARAMETER")
@SuppressWarnings("ALL")
class LoggingStreamHelper<S extends Closeable>
    implements Closeable {
    protected final S              tee;
    private final OutputStream     logOutputStream;
    private final Consumer<String> logger;

    @SuppressWarnings("unchecked")
    @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
    LoggingStreamHelper(
        final S stream,
        final Consumer<String> logger) {
        this.logOutputStream = new ByteArrayOutputStream();
        // noinspection ChainOfInstanceofChecks
        if (stream instanceof OutputStream) {
            this.tee = (S) new TeeOutputStream((OutputStream) stream,
                logOutputStream);
        } else if (stream instanceof InputStream) {
            this.tee = (S) new TeeInputStream((InputStream) stream,
                logOutputStream);
        } else
            throw new ClassCastException(
                "OutputStream or InputStream derivative expected"); //$NON-NLS-1$
        this.logger = logger;
    }

    @Override
    public void close()
        throws IOException {
        logger.accept(logOutputStream.toString());

        logOutputStream.close();
        tee.close();
    }
}
