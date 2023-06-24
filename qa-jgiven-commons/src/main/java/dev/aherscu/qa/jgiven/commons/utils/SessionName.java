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

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.lang.String.*;
import static java.lang.System.*;
import static java.lang.Thread.*;
import static java.util.Objects.*;

import java.text.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Builder
@With
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
@SuppressFBWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ",
    justification = "Lombok generated builder")
public class SessionName {
    /**
     * An invalid session name; this might happen with a test which does not
     * correctly set its session name, or does not care about because it does
     * not use the WebDriver infrastructure.
     */
    public final static SessionName INVALID    = SessionName.builder().build();

    @EqualsAndHashCode.Include
    @Builder.Default
    public final String             className  = EMPTY;
    @EqualsAndHashCode.Include
    @Builder.Default
    public final String             methodName = EMPTY;
    @EqualsAndHashCode.Include
    @Builder.Default
    public final String             params     = EMPTY;
    @EqualsAndHashCode.Include
    @Builder.Default
    public final String             id         = EMPTY;
    @Builder.Default
    public final String             timestamp  = EMPTY;

    /**
     * Parses a Web Driver session name from specified string.
     *
     * @param name
     *            the session name; must have 5 parts delimited by colon,
     *            following this format:
     *            {@code class:method:paramsHash:threadId:timestamp} wherein
     *            each part is optional
     * @return the parsed , or {@link #INVALID} if the name cannot be split into
     *         five parts
     * @throws NullPointerException
     *             if the name is null
     */
    public static SessionName from(final String name) {
        val parts = requireNonNull(name, "session name cannot be missing")
            .split(COLON, -1);

        if (parts.length < 5) {
            log.warn("invalid web driver session name [{}]"
                + " -- maybe you are using a SauceLabsReporter with non-WebDriver test?",
                name);
            return INVALID;
        }

        return SessionName.builder()
            .className(parts[0])
            .methodName(parts[1])
            .params(parts[2])
            .id(parts[3])
            .timestamp(parts[4])
            .build();
    }

    public static String generateFromCurrentThreadAndTime() {
        val sessionName = from(currentThread().getName())
            .withTimestamp(valueOf(currentTimeMillis()))
            .toString();
        log.trace("generated session name {}", sessionName);
        return sessionName;
    }

    /**
     * @return a session name for a class-level test; only class-name and thread
     *         id are included
     */
    public SessionName asClassSession() {
        return SessionName.builder()
            .className(this.className)
            .id(this.id)
            .build();
    }

    @Override
    public String toString() {
        return MessageFormat
            .format("{0}:{1}:{2}:{3}:{4}",
                className, methodName, params, id, timestamp);
    }
}
