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

import java.nio.charset.*;

import javax.annotation.*;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import org.apache.commons.codec.binary.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Handles basic authentication.
 *
 * @author aherscu
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@AllArgsConstructor
@ToString
@Slf4j
public class AuthenticationRequestFilter
    implements ClientRequestFilter {

    private final String user;
    private final String password;

    @Override
    public void filter(final ClientRequestContext requestContext) {
        log.debug("adding basic authentication {}",
            basicAuthentication());
        requestContext.getHeaders().add(
            HttpHeaders.AUTHORIZATION,
            basicAuthentication());
    }

    /**
     * @return Basic Authentication string based on user name and password
     *
     * @see #AuthenticationRequestFilter(String, String)
     */
    protected String basicAuthentication() {
        return "Basic " //$NON-NLS-1$
            + Base64.encodeBase64String(
                String.format("%s:%s", user, password) //$NON-NLS-1$
                    .getBytes(StandardCharsets.UTF_8));
    }
}
