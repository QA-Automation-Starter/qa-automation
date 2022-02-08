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
package dev.aherscu.qa.tester.utils.rest;

import javax.ws.rs.client.*;

import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Builds a logging REST client.
 *
 * @author aherscu
 */
@UtilityClass
@Slf4j
public final class LoggingClientBuilder {
    /**
     * Replacement for {@link ClientBuilder#newClient()}.
     *
     * @return a client having a {@link LoggingRequestFilter} and
     *         {@link LoggingResponseFilter}
     */
    public static Client newClient() {
        return newClient(ClientBuilder.newClient());
    }

    /**
     * Adorns a provided client with logging.
     *
     * @param client
     *            the provided client
     *
     * @return a client like the provided client plus registered
     *         {@link LoggingRequestFilter}, {@link LoggingResponseFilter},
     *         {@link LoggingWriterInterceptor} and
     *         {@link LoggingReaderInterceptor}
     */
    public static Client newClient(final Client client) {
        log.trace("enhancing client {} with logging capabilities", client);
        return ClientBuilder
            .newClient(client
                .register(
                    new LoggingRequestFilter(log::debug))
                .register(
                    new LoggingResponseFilter(log::debug))
                .register(
                    new LoggingWriterInterceptor(
                        message -> log.trace("writing {}", message)))
                .register(
                    new LoggingReaderInterceptor(
                        message -> log.trace("reading {}", message)))
                .getConfiguration());

    }
}
