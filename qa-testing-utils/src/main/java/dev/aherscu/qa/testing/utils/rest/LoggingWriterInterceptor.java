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

package dev.aherscu.qa.testing.utils.rest;

import java.io.*;
import java.util.function.*;

import javax.ws.rs.ext.*;

import dev.aherscu.qa.testing.utils.logging.*;

/**
 * Use to log the outbound stream.
 *
 * @author Adrian Herscu
 *
 */
public class LoggingWriterInterceptor
    extends LoggingHelper
    implements WriterInterceptor {

    /**
     * @param logger
     *            the logging function to use
     */
    public LoggingWriterInterceptor(final Consumer<String> logger) {
        super(logger);
    }

    @SuppressWarnings("resource")
    @Override
    // NOTE: should not close the intercepted stream
    public void aroundWriteTo(final WriterInterceptorContext context)
        throws IOException {
        context.setOutputStream(
            new LoggingOutputStream(context.getOutputStream(), logger));
        context.proceed();
    }

}
