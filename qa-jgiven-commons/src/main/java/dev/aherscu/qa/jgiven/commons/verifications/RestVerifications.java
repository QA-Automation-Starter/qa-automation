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
package dev.aherscu.qa.jgiven.commons.verifications;

import java.util.function.*;

import javax.annotation.concurrent.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import org.hamcrest.*;
import org.json.*;
import org.skyscreamer.jsonassert.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.tester.utils.assertions.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic REST client verifications. Verifications on the response retrieved by
 * previous action.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@ThreadSafe
@Slf4j
public class RestVerifications<SELF extends RestVerifications<SELF>>
    extends
    GenericVerifications<RestScenarioType, SELF> {

    /**
     * The retrieved response contents.
     */
    @ExpectedScenarioState
    protected ThreadLocal<String>   responseContent;

    /**
     * The retrieved response in closed state.
     *
     * @see RestActions
     */
    @ExpectedScenarioState
    protected ThreadLocal<Response> closedResponse;

    // TODO add validation for above fields

    /**
     * Verifies response has expected JSON contents.
     *
     * @param expectedJson
     *            the expected JSON contents
     * @return {@link #self()}
     */
    @SneakyThrows(JSONException.class)
    public SELF the_response_contains_JSON(final String expectedJson) {
        log.trace(">>> retrieving contents from {}:{}",
            responseContent, this);
        JSONAssert.assertEquals(expectedJson, responseContent.get(), false);
        return self();
    }

    /**
     * Verifies that the JSON data structure contained by {@link Response}
     * matches several JSON key:matcher pairs.
     *
     * @param expectedContents
     *            the expected contents, where each key is a
     *            <a href="https://github.com/jayway/JsonPath">JSON Path</a>
     *            expression and the value is a Hamcrest matcher
     *
     * @return {@link #self()}
     */
    public SELF the_response_contents(
        @JsonAssertionsFormatter.Annotation final Iterable<? extends JsonAssertion<?>> expectedContents) {
        log.trace(">>> retrieving contents from {}:{}",
            responseContent, this);
        JsonAssertEx.with(responseContent.get()).assertHas(expectedContents);
        return self();
    }

    /**
     * Verifies that the {@link Response} matches specified Hamcrest expression.
     *
     * @param expected
     *            the expected expression
     * @return {@link #self()}
     */
    public SELF the_response_contents(final Matcher<String> expected) {
        log.trace(">>> retrieving contents from {}:{}",
            responseContent, this);
        MatcherAssert.assertThat(responseContent.get(), expected);
        return self();
    }

    /**
     * Verifies the response status family.
     *
     * @param familyMatcher
     *            matching expression
     * @return {@link #self()}
     */
    public SELF the_response_status(
        final Matcher<Response.Status.Family> familyMatcher) {
        // ISSUE when running with TestNG in parallel methods mode
        // fails with NPE on closedResponse
        log.trace(">>> retrieving closed response from {}:{}", closedResponse,
            this);

        MatcherAssert.assertThat(
            closedResponse.get().getStatusInfo().getFamily(),
            familyMatcher);

        return self();

    }

    /**
     * Creates a supplier which when called, executes specified invocation,
     * stores the closed response and response content, and calls the specified
     * converter on response content.
     * <p>
     * Attaches the response.
     *
     * @param invocation
     *            a JAX-RS invocation
     * @param converter
     *            something to execute on response content
     * @return supplier that executes the specified invocation
     * @param <T>
     *            the type expected from the converter
     */
    protected final <T> Supplier<T> invoke(
        final Invocation invocation,
        final Function<String, T> converter) {
        return () -> {
            try (val response = invocation.invoke()) {
                log.trace("invoking {}", invocation);
                closedResponse.set(response);
                responseContent.set(attach(response.readEntity(String.class)));
                return converter.apply(responseContent.get());
            }
        };
    }

    /**
     * Attaches the actual response content, after first verification completed.
     * <p>
     * This is suitable when a synchronous verification is performed, i.e. when
     * assuming that actions are always stable and the response might be
     * verified multiple times.
     * <p>
     * Override with empty implementation to disable.
     */
    @AfterStage
    protected void attachActualResponse() {
        attach(null != responseContent ? responseContent.get() : null);
    }
}
