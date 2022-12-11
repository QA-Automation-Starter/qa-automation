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
package dev.aherscu.qa.jgiven.commons.actions;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Objects.*;

import java.net.*;
import java.util.function.*;

import javax.annotation.concurrent.*;
import javax.validation.constraints.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.tester.utils.rest.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

/**
 * Generic REST client actions. Act on previously set client. Each action closes
 * the connection.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@Slf4j
@ThreadSafe
public class RestActions<SELF extends RestActions<SELF>>
    extends GenericActions<RestScenarioType, SELF> {
    /**
     * Target ({@link WebTarget}) of Web actions in this class.
     */
    protected final ThreadLocal<WebTarget> target          =
        new ThreadLocal<>();
    /**
     * The retrieved {@link Response}'s contents. We hold this as a string
     * because reading the contents of a {@link Response} cannot be done twice.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<String>    responseContent =
        new ThreadLocal<>();
    /**
     * FIXME: this is a patch. We should hold an extended version of Response
     * that holds the contents as a string (see above responseContent field).
     * Perhaps this should be done in the AutoCloseableResponse class. This has
     * to be done in a separate version of this library in order no to break
     * older clients.
     * <p>
     * The retrieved {@link Response} in closed state.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Response>  closedResponse  =
        new ThreadLocal<>();
    /**
     * The given {@link Client}. This client is released upon scenario
     * completion.
     *
     * @see RestVerifications
     */
    @ExpectedScenarioState
    protected ThreadLocal<Client>          client;

    // TODO add validation for above fields

    /**
     * Appends a path.
     *
     * @param path
     *            the path
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF appending_path(final String path) {
        target.set(target.get().path(path));
        return self();
    }

    /**
     * Creates a {@link WebTarget} object.
     *
     * @param url
     *            the URL
     * @throws NullPointerException
     *             in case the supplied argument is null
     * @throws IllegalArgumentException
     *             in case the supplied string is not a valid URI template
     * @return {@link #self()}
     */
    public SELF connecting_to(final @NotNull URI url) {
        log.debug("connecting to {}", url);
        target.set(requireNonNull(client.get(), "must set a REST client before")
            .target(url));
        return self();
    }

    @SneakyThrows(URISyntaxException.class)
    public SELF connecting_to(final String url) {
        return connecting_to(new URI((url)));
    }

    /**
     * Deletes an entity and retrieves the {@link Response}.
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF deleting() {
        return deleting(new MultivaluedHashMap<>());
    }

    /**
     * Deletes an entity and retrieves the {@link Response}.
     *
     * @param headers
     *            headers to add
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF deleting(
        final MultivaluedMap<String, Object> headers) {
        return invoke(request -> request.headers(headers).delete());
    }

    /**
     * Retrieves the {@link Response}.
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    @NestedSteps
    public SELF getting_the_response() {
        return getting_the_response_with_headers(
            new MultivaluedHashMap<>());
    }

    /**
     * Retrieves the {@link Response}.
     *
     * @param headers
     *            the headers to be added to this request
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF getting_the_response_with_headers(
        final MultivaluedMap<String, Object> headers) {
        return invoke(request -> request.headers(headers).get());
    }

    /**
     * Posts a form and retrieves the {@link Response}.
     *
     * @param entity
     *            the entity to post
     *
     * @param headers
     *            the header to add
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF posting(
        final Entity<?> entity,
        final MultivaluedMap<String, Object> headers) {
        return invoke(request -> request
            .headers(headers).buildPost(entity).invoke());
    }

    /**
     * Posts a form and retrieves the {@link Response}.
     *
     * @param form
     *            the form to post
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF posting(@FormFormatter.Annotation final Form form) {
        return invoke(request -> request.post(Entity.form(form)));
    }

    /**
     * Puts an entity and retrieves the {@link Response}.
     *
     * @param entity
     *            the entity to put
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF putting(final Entity<?> entity) {
        return putting(entity, new MultivaluedHashMap<>());
    }

    /**
     * Puts an entity and retrieves the {@link Response}.
     *
     * @param entity
     *            the entity to put
     * @param headers
     *            headers to add
     *
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF putting(
        final Entity<?> entity,
        final MultivaluedMap<String, Object> headers) {
        return invoke(request -> request.headers(headers).put(entity));
    }

    /**
     * Fills a value into a query parameter.
     *
     * @param value
     *            the value; if the value is an {@link Iterable}, then its items
     *            are concatenated with comma delimiters
     * @param name
     *            the parameter's name
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    public SELF setting_$_into_query_param(
        final Object value,
        final String name) {
        target.set(target.get()
            .queryParam(name,
                value instanceof Iterable
                    ? join((Iterable<?>) value, COMMA)
                    : value));
        return self();
    }

    /**
     * Invokes specified REST operation.
     *
     * @param operation
     *            the REST operation to invoke
     * @throws IllegalStateException
     *             if {@link #connecting_to(URI)} has not been called before
     * @return {@link #self()}
     */
    protected final SELF invoke(
        final Function<Invocation.Builder, Response> operation) {
        return Failsafe.with(retryPolicy)
            .get(() -> {
                try (val response = new AutoCloseableResponse(operation
                    .apply(target.get().request()))) {
                    closedResponse.set(response);
                    log.trace(">>> stored closed response into {}:{}",
                        closedResponse, this);
                    responseContent.set(response.readEntity(String.class));
                    log.trace(">>> stored content into {}:{}",
                        responseContent, this);
                }
                // ISSUE on jdk11+ fails to compile via maven due to missing
                // (SELF) cast
                return (SELF) self();
            });
    }
}
