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
import static java.lang.Long.*;
import static java.lang.System.*;
import static java.time.Duration.*;

import java.time.*;
import java.util.concurrent.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.*;

import dev.aherscu.qa.jgiven.commons.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

/**
 * Provides a reference to current step in order to allow attachments.
 *
 * @param <SELF>
 *            the type of the extending class to realize the fluent interface
 *
 * @author aherscu
 */
@Slf4j
public class StageEx<SELF extends StageEx<?>> extends Stage<SELF> {
    protected static final Duration pollDelay, pollTimeout;

    static {
        pollDelay = ofSeconds(parseLong(getProperty("poll.delay", "1")));
        pollTimeout = ofSeconds(parseLong(getProperty("poll.timeout", "10")));

        log.debug("poll delay {} seconds; timeout {} seconds",
            pollDelay.getSeconds(),
            pollTimeout.getSeconds());
    }

    /**
     * The configured retry policy.
     *
     * @see #configurePolling()
     */
    protected final RetryPolicy<SELF> retryPolicy =
        new RetryPolicy<>();

    /**
     * Provides programmatic access to current step. Use to add attachments.
     */
    @ExpectedScenarioState
    public CurrentStep                currentStep;

    @Override
    @SneakyThrows
    @Deprecated // the description no longer appears in report
    public final SELF $(final String description,
        @Hidden final StepFunction<? super SELF> function) {
        return super.$(description, function);
    }

    /**
     * Attaches specified text. Useful for attaching JSON, XML, or just plain
     * text. Override to customize, e.g. to shorten or format the text.
     *
     * @param text
     *            the text to attach
     * @return the attached text
     */
    protected String attach(final String text) {
        currentStep.addAttachment(Attachment
            .fromText(prettified(text), MediaType.PLAIN_TEXT_UTF_8)
            .withTitle("actual response"));
        return text;
    }

    /**
     * Configures the retry policy. Configurable via:
     * <ul>
     * <li>poll.delay -- defaults to 1 second</li>
     * <li>poll.timeout -- defaults to 10 seconds</li>
     * </ul>
     * <p>
     * <strong>IMPORTANT:</strong> if you override this method, you should
     * ensure that other policy properties are setup properly, either by calling
     * this implementation via {@code super.configureRetryPolicy()}, or by other
     * means.
     * </p>
     */
    @BeforeScenario
    protected void configurePolling() {
        retryPolicy
            .withMaxRetries(-1)
            .withDelay(pollDelay)
            .withMaxDuration(pollTimeout)
            .onRetry(
                e -> log.trace("retrying due to {}", e.toString()))
            .onRetriesExceeded(
                e -> log.trace("retries exceeded for {}", e.toString()))
            .handleIf(TestRuntimeException::isRecoverableException);
    }

    /**
     * Repeatedly executes specified callable, per {@link #retryPolicy}.
     *
     * @param callable
     *            the callable
     * @return the value returned by the callable
     */
    // TODO add an optional recovery procedure as an extra parameter
    // to be used in cases where application popups, or other hiccups occur
    @Hidden
    protected final SELF retry(final Callable<SELF> callable) {
        return Failsafe
            .with(retryPolicy)
            .get(callable::call);
    }

    /**
     * Safely executes specified callable, swallowing all exceptions.
     *
     * <p>
     * <strong>IMPORTANT:</strong>must not be called from step method that is
     * annotated with {@link NestedSteps} since this interferes with exception
     * handling.
     * </p>
     *
     * @param callable
     *            the callable
     * @return {@link #self()}
     */
    @Hidden
    protected final SELF safely(final Callable<SELF> callable) {
        try {
            return callable.call();
        } catch (final Throwable t) {
            log.debug("swallowed {} -> {}", t.getClass(), t.getMessage());
            return self();
        }
    }
}
