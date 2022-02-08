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

import static com.jayway.jsonpath.JsonPath.*;
import static java.lang.String.*;
import static javax.ws.rs.core.HttpHeaders.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import javax.annotation.concurrent.*;
import javax.ws.rs.client.*;

import org.apache.commons.io.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Adds an OAuth token to each request.
 *
 * <p>
 * NOTE: this is a naive implementation; the token is not saved for subsequent
 * requests. May have performance implications.
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749">RFC6749</a>
 * @author aherscu
 *
 */
@ThreadSafe
@Slf4j
@SuppressWarnings({ "static-method", })
public class OAuthRequestFilter implements ClientRequestFilter {

    private final URI refreshTokenUri;

    public OAuthRequestFilter(final URI refreshTokenUri) {
        this.refreshTokenUri = refreshTokenUri.normalize();
    }

    @Override
    public final void filter(final ClientRequestContext requestContext)
        throws IOException {
        val retrievedTokenBlock = retrieveTokenBlockFrom(refreshTokenUri);
        log.trace("retrieved token block {}", retrievedTokenBlock);
        val parsedAccessToken = parseAccessTokenFrom(retrievedTokenBlock);
        log.trace("parsed access token {}", parsedAccessToken);
        requestContext.getHeaders().add(AUTHORIZATION,
            format("Bearer %s", parsedAccessToken));
    }

    /**
     * The default OAuth2 token parsing implementation. Assumes JSON token
     * block, and extracts from the {@code access_token} field. Override with
     * your own.
     *
     * @param token
     *            the token block
     *
     * @return the token string
     */
    protected String parseAccessTokenFrom(final String token) {
        return parse(token).read("$.access_token"); //$NON-NLS-1$
    }

    /**
     * Default OAuth2 token retrieval implementation. Uses GET method to
     * retrieve token block from specified URL. Override with your own.
     *
     * @param url
     *            token URL
     * @return token block
     * @throws IOException
     *             as thrown by server
     */
    protected String retrieveTokenBlockFrom(final URI url)
        throws IOException {
        return IOUtils.toString(url, StandardCharsets.UTF_8);
    }

}
