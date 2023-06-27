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
package dev.aherscu.qa.jgiven.webdriver.utils;

import static com.codahale.metrics.MetricRegistry.*;
import static dev.aherscu.qa.jgiven.commons.utils.MetricReporterSuiteListener.*;
import static dev.aherscu.qa.testing.utils.ImageUtils.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.lang.Long.*;
import static java.lang.System.*;
import static java.time.Duration.*;
import static java.util.Objects.*;
import static org.apache.commons.codec.binary.Base64.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.testing.utils.*;
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
public abstract class WebDriverStageEx<SELF extends StageEx<?>>
    extends StageEx<SELF> {

    /**
     * Override to force other image transformation.
     *
     * <p>
     * NOTE: Defaults to a grayed palette. Scaling down here, may lead to
     * unreadable thumbnails in the generated JGiven report. See
     * https://github.com/TNG/JGiven/issues/417
     * </p>
     */
    public static final UnaryOperator<Pipeline>    ATTACHEMENT_TRANSFORMER =
        pipeline -> pipeline.reduce(GREY_SCALE_COLOR_MODEL);
    public static final com.codahale.metrics.Timer clickTimer,
        sendKeysTimer, scrollIntoViewTimer, assertElementTimer, locateTimer;
    protected static final Duration                pollDelay, pollTimeout;

    static {
        clickTimer = METRIC_REGISTRY.timer(name("click"));
        sendKeysTimer = METRIC_REGISTRY.timer(name("sendKeys"));
        scrollIntoViewTimer = METRIC_REGISTRY.timer(name("scrollIntoView"));
        assertElementTimer = METRIC_REGISTRY.timer(name("assertElement"));
        locateTimer = METRIC_REGISTRY.timer(name("locate"));

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

    public SELF self() {
        return super.self();
    }

    public final SELF alert(
        final Consumer<Alert> action,
        final WebDriver driver) {
        return retry(() -> {
            log.debug("accepting alert");
            action.accept(driver.switchTo().alert());
            return self();
        });
    }

    /**
     * Delayed screenshot.
     *
     * @param somethingThatTakesScreenshot
     *            a WebDriver able to take screenshots
     * @param delayMs
     *            delay before screenshot in milliseconds
     */
    @SneakyThrows
    public final <T extends TakesScreenshot> void attachScreenshot(
        final T somethingThatTakesScreenshot,
        final long delayMs) {
        if (nonNull(getProperty("noscreenshots"))) {
            log.warn("screenshots disabled");
            return;
        }

        ThreadUtils.sleep(delayMs);

        currentStep.addAttachment(Attachment
            .fromBase64(
                encodeBase64String(Pipeline
                    .from(new ByteArrayInputStream(
                        somethingThatTakesScreenshot
                            .getScreenshotAs(OutputType.BYTES)))
                    .map(ATTACHEMENT_TRANSFORMER)
                    .into(new ByteArrayOutputStream(4096), "png")
                    .toByteArray()),
                MediaType.PNG));
    }

    /**
     * Attaches specified text. Useful for attaching JSON, XML, or just plain
     * text. Override to customize, e.g. to shorten or format the text.
     * 
     * @param text
     *            the text to attach
     * @return the attached text
     */
    public String attach(final String text) {
        currentStep.addAttachment(Attachment
            .fromText(prettified(text), MediaType.PLAIN_TEXT_UTF_8)
            .withTitle("actual response"));
        return text;
    }

    /**
     * Zero delay screenshot.
     *
     * @param driver
     *            a WebDriver able to take screenshots
     * @throws TestRuntimeException
     *             if the WebDriver does not support TakesScreenshot
     */
    public final void attachScreenshot(final WebDriverEx driver) {
        attachScreenshot(driver, 0);
    }

    /**
     * Delayed screenshot.
     *
     * @param driver
     *            a WebDriver able to take screenshots
     * @param delayMs
     *            delay before screenshot in milliseconds
     * @throws TestRuntimeException
     *             if the WebDriver does not support TakesScreenshot
     */
    public final void attachScreenshot(
        final WebDriverEx driver,
        final long delayMs) {
        if (driver.is(TakesScreenshot.class)) {
            attachScreenshot(driver.asRemote(), delayMs);
        } else
            throw new TestRuntimeException(
                "screenshots not supported by " + driver);
    }

    /**
     * Switches to specified Appium context.
     *
     * @param byRule
     *            naming rule of Appium context
     * @param ca
     *            a context aware driver, usually a mobile driver
     * @return {@link #self()}
     * @throws NoSuchContextException
     *             if no such context exists
     */
    public SELF context(
        final Predicate<String> byRule,
        final ContextAware ca) {
        val currentContext = ca.getContext();

        if (byRule.test(currentContext)) {
            log.debug("already in context {}", currentContext);
            return self();
        }

        return retry(() -> {
            val contexts = ca.getContextHandles();
            log.debug("available contexts {}", contexts);
            ca.context(
                contexts.stream()
                    .filter(byRule)
                    .peek(id -> log.debug("found matching context {}", id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchContextException(
                        "within " + join(contexts, COMMA))));
            // ISSUE on jdk11+ fails to compile via maven due to missing
            // (SELF) cast
            return (SELF) self();
        });
    }

    /**
     * Retrieves a DOM element by specified locator and context and brings it
     * into view, if needed. If the locator is matching multiple elements then
     * the first one is returned.
     *
     * <p>
     * {@code locateTimer} metric will be updated including the scrolling into
     * view, if needed.
     * </p>
     *
     * @param locator
     *            the locator
     * @param context
     *            the context
     * @return the DOM element
     * @throws NoSuchElementException
     *             if the element does not exist
     */
    @SneakyThrows
    public final WebElement element(
        final By locator,
        final SearchContext context) {
        try (val locateTimerContext = locateTimer.time()) {
            return scrollIntoView(context.findElement(locator));
        }
    }

    /**
     * Retrieves DOM elements by specified locator and context. If the locator
     * is matching multiple elements then the first one is returned.
     *
     * <p>
     * {@code locateTimer} metric will be updated.
     * </p>
     *
     * @param locator
     *            the locator
     * @param context
     *            the context
     * @return list of DOM elements
     */
    public final List<WebElement> elements(
        final By locator,
        final SearchContext context) {
        try (val locateTimerContext = locateTimer.time()) {
            return context.findElements(locator);
        }
    }

    /**
     * Retrieves matching DOM elements by specified locator and context. Retries
     * several time if the count of elements is zero.
     *
     * <p>
     * {@code locateTimer} metric will be updated.
     * </p>
     *
     * @param locator
     *            the locator
     * @param context
     *            the context
     * @return list of DOM elements
     */
    public final List<WebElement> ensureElements(
        final By locator,
        final SearchContext context) {
        return Failsafe
            .with(new RetryPolicy<List<WebElement>>()
                .withMaxRetries(-1)
                .withDelay(pollDelay)
                .withMaxDuration(pollTimeout)
                .onRetriesExceeded(
                    e -> log.trace("retries exceeded for {}", e.toString()))
                .handleResultIf(result -> 0 == result.size())
                .onRetry(e -> log.debug(
                    "retrying since no matching elements found {}", e)))
            .get(() -> elements(locator, context));
    }

    /**
     * Scrolls specified element into view.
     *
     * @param element
     *            the element to scroll into view
     * @return the element
     * @see #element(By, SearchContext)
     */
    @SuppressWarnings("static-method")
    public abstract WebElement scrollIntoView(
        @SuppressWarnings("unused") final WebElement element);
}
