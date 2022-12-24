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

import static java.util.Objects.*;
import static javax.ws.rs.core.HttpHeaders.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.time.*;

import javax.ws.rs.client.*;

import com.fasterxml.jackson.annotation.*;
import com.google.common.cache.*;

import lombok.*;
import lombok.experimental.*;
import lombok.extern.jackson.*;

/**
 * Generic implementation for OAuth 2.0 JAX-RS authentication filters.
 * <p>
 * Your concrete implementation may look like this:
 * 
 * <pre>
 * protected TokenBlock retrieveTokenBlockFor(
 *     final ClientRequestContext context) {
 *     log.debug("retrieving token block for {}", this);
 *     try (val response = context
 *         .getClient()
 *         .target(refreshTokenUri)
 *         .register(basic(userName, password))
 *         .request()
 *         .buildPost(form(new Form()
 *             .param("scope", scope)
 *             .param("grant_type", "client_credentials")))
 *         .invoke()) {
 *         return response.readEntity(TokenBlock.class);
 *     }
 * }
 * </pre>
 * </p>
 */
@SuperBuilder
public abstract class OAuthRequestFilter
    implements ClientRequestFilter {
    protected final String                          refreshTokenUri;
    protected Cache<OAuthRequestFilter, TokenBlock> customTokenBlockCache;

    // for testing purposes only
    public OAuthRequestFilter customTokenBlockCache(
        final Cache<OAuthRequestFilter, TokenBlock> customTokenBlockCache) {
        this.customTokenBlockCache = customTokenBlockCache;
        return this;
    }

    @Override
    @SneakyThrows
    public final void filter(final ClientRequestContext requestContext) {
        val tokenBlock = tokenBlockCache()
            .get(this, () -> retrieveTokenBlockFor(requestContext));
        requestContext
            .getHeaders()
            .add(AUTHORIZATION,
                tokenBlock.tokenType + SPACE + tokenBlock.accessToken);
    }

    protected final Cache<OAuthRequestFilter, TokenBlock> tokenBlockCache() {
        return nonNull(customTokenBlockCache)
            ? customTokenBlockCache
            : CacheSingleton.INSTANCE.tokenBlockCache;
    }

    protected abstract TokenBlock retrieveTokenBlockFor(
        final ClientRequestContext context);

    @RequiredArgsConstructor
    protected enum CacheSingleton {
        // ISSUE Guava does not support per entry expiration
        // see https://github.com/google/guava/issues/1203
        // Hence, we use here a fixed time of 50 minutes which might break
        // sometime; see other cache implementations:
        // https://stackoverflow.com/questions/13979376/designing-a-guava-loadingcache-with-variable-entry-expiry
        INSTANCE(CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(50))
            .build());

        final Cache<OAuthRequestFilter, TokenBlock> tokenBlockCache;
    }

    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public final static class TokenBlock {
        @JsonAlias("token_type")
        public final String tokenType;
        @JsonAlias("access_token")
        public final String accessToken;
        @JsonAlias("expires_in")
        public final long   expiresInSeconds;
    }
}
