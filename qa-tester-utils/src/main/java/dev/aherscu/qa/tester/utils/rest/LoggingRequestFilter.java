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
package dev.aherscu.qa.tester.utils.rest;

import java.text.*;
import java.util.function.*;

import javax.ws.rs.client.*;
import javax.ws.rs.ext.*;

/**
 * Logs client JAX-RS requests.
 *
 * <p>
 * Usage:
 * </p>
 * {@code ClientBuilder.newClient().register(new LoggingRequestFilter(logger))}
 *
 * @author aherscu
 *
 */
@Provider
public class LoggingRequestFilter extends LoggingHelper
    implements ClientRequestFilter {

    /**
     * @param logger
     *            the logging function to use
     */
    public LoggingRequestFilter(final Consumer<String> logger) {
        super(logger);
    }

    @Override
    public void filter(final ClientRequestContext requestContext) {
        logger.accept(MessageFormat
            .format("{0} {1} with headers {2}",
                requestContext.getMethod(),
                requestContext.getUri(),
                requestContext.getHeaders()));
    }
}
