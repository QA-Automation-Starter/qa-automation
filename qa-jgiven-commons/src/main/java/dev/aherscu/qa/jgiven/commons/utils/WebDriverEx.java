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
package dev.aherscu.qa.jgiven.commons.utils;

import static java.lang.Thread.*;
import static java.util.Objects.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;

import com.google.common.collect.*;
import com.google.gson.*;

import dev.aherscu.qa.jgiven.commons.*;
import io.appium.java_client.*;
import io.appium.java_client.android.*;
import io.appium.java_client.ios.*;
import io.appium.java_client.windows.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;
import net.jodah.failsafe.event.*;
import net.jodah.failsafe.function.*;

/**
 * Provides additional actions over standard Selenium Remote Web Driver.
 *
 * <p>
 * <a href="https://www.w3.org/TR/?tag=dom&status=rec">W3C Standards</a>
 * </p>
 */
// TODO try to make it support WebDriver interface directly
@Slf4j
@ToString
public class WebDriverEx {
    /**
     * The application native context id.
     */
    public static final String                                      NATIVE_APP_CONTEXT =
        "NATIVE_APP";
    /**
     * Maps tests to remote Web Driver sessions, if there were any. Uses a
     * synchronized version since it may be accessed from multiple threads.
     */
    public static final Multimap<SessionName, WebDriverSessionInfo> remoteSessions     =
        Multimaps.synchronizedSetMultimap(HashMultimap.create());
    /**
     * Original capabilities used to initiate this web driver; might be null.
     */
    public final Capabilities                                       originalCapabilities;
    private final WebDriver                                         driver;

    /**
     * @param driver
     *            the driver to wrap
     * @param originalCapabilities
     *            the original capabilities
     */
    public WebDriverEx(
        final WebDriver driver,
        final Capabilities originalCapabilities) {
        this.driver = requireNonNull(driver,
            "web driver not initialized");
        this.originalCapabilities = requireNonNull(originalCapabilities,
            "must provide some capabilities");
    }

    /**
     * @param element
     *            an element
     * @return description of specified element
     */
    public static String descriptionOf(final WebElement element) {
        return EMPTY;
        // ISSUE toString generates a way too long description with redundant
        // and not needed data
        // .toString();

        // ISSUE getXXXX and isXXXX methods all do Selenium roundtrips
        // increasing chance of failures and execution time
        // MessageFormat
        // .format("element <{0}> containing \"{1}\" which {2} displayed",
        // element.getTagName(),
        // defaultIfBlank(element.getText(), ELLIPSIS),
        // BooleanUtils.toString(element.isDisplayed(), "is", "is not"));

        // TODO should find way to override RemoteWebDriver#toString
    }

    /**
     * Attempts to initialize a Web driver with specified capabilities.
     *
     * <p>
     * In addition, adds the Remote Web Driver session identifier to
     * {@link #remoteSessions} mapped to name of test as defined by
     * {@link UnitilsScenarioTest#unitilsBeforeClass} or
     * {@link UnitilsScenarioTest#unitilsBeforeMethod}
     * </p>
     *
     * @param capabilities
     *            the capabilities; might be null; the capabilities may include
     *            <tt>name</tt> for registering this web driver for some sort of
     *            status reporting mechanism -- see
     *            {@link AbstractSauceLabsReporter}
     * @return the initialized driver
     *
     * @throws RuntimeException
     *             or derivative, if the initialization failed
     */
    public static WebDriverEx from(final Capabilities capabilities) {
        log.debug("connecting session {} with {}",
            capabilities.getCapability("name"), capabilities);

        return DryRunAspect.dryRun
            ? null
            : Failsafe.with(
                // NOTE: if an exception is not handled during @Before methods
                // JGiven report is not generated at all
                Fallback.of(e -> {
                    log.debug("initialization failed due to {}",
                        e.getLastFailure().getMessage());
                    return null;
                }),
                new RetryPolicy<WebDriverEx>()
                    // at most 3 retries by default
                    .handle(WebDriverException.class)
                    .onRetry(e -> log.debug("re-attempting connection #{}",
                        e.getAttemptCount()))
                    .onRetriesExceeded(e -> log.error("retries exceeded.",
                        e.getFailure())))
                .onSuccess(e -> registerSession(e.getResult()))
                .get(() -> new WebDriverEx(webDriverFor(capabilities),
                    capabilities));
    }

    private static void registerSession(final WebDriverEx driver) {
        val name = driver.originalCapabilities.getCapability("name");

        if (isNull(name)) {
            log.warn("session name missing from capabilities"
                + " -- will not be reported");
            return;
        }

        val sessionNameWithTimestamp = SessionName.from(name.toString());
        val sessionId = driver.asRemote().getSessionId();

        log.debug("registering remote web driver session id {} for {}",
            sessionId.toString(), sessionNameWithTimestamp);

        log.debug("registered new session {}",
            remoteSessions.put(sessionNameWithTimestamp,
                WebDriverSessionInfo.builder()
                    .sessionId(sessionId)
                    .capabilities(driver.originalCapabilities)
                    .build()));
    }

    /**
     * @return the registered Web Driver session informations for current test
     *         method, including those registered during <tt>@BeforeClass</tt>
     */
    public static Iterable<WebDriverSessionInfo> sessionInfos() {
        return Iterables.concat(
            remoteSessions.get(SessionName
                .from(currentThread().getName())
                .asClassSession()),
            remoteSessions.get(SessionName
                .from(currentThread().getName())));
    }

    @SneakyThrows
    private static Class<? extends WebDriver> webDriverClassFor(
        final Capabilities capabilities) {
        return Class.forName(requireNonNull(capabilities
            .getCapability("class"), "must have a class capability")
                .toString())
            .asSubclass(WebDriver.class);
    }

    /**
     * Initializes a WebDriver for specified capabilities.
     *
     * @param capabilities
     *            the capabilities
     * @return the Web Driver
     */
    @SneakyThrows
    public static WebDriver webDriverFor(final Capabilities capabilities) {
        try {
            return nonNull(capabilities.getCapability("url"))
                ? webDriverClassFor(capabilities)
                    .getConstructor(URL.class, Capabilities.class)
                    .newInstance(new URL(
                        capabilities.getCapability("url")
                            .toString()),
                        capabilities)
                : webDriverClassFor(capabilities)
                    .getConstructor(Capabilities.class)
                    .newInstance(capabilities);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Casts an WebDriver into an AndroidDriver.
     *
     * @param <T>
     *            type of WebElement
     * @return the AndroidDriver interface
     * @throws ClassCastException
     *             if the driver is not of AndroidDriver type
     */
    @SuppressWarnings("unchecked")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "BC_UNCONFIRMED_CAST")
    public <T extends WebElement> AndroidDriver<T> asAndroid() {
        return (AndroidDriver<T>) driver;
    }

    public WebDriver asGeneric() {
        return driver;
    }

    @SuppressWarnings("unchecked")
    public <T extends WebElement> IOSDriver<T> asIOS() {
        return (IOSDriver<T>) driver;
    }

    /**
     * Casts an WebDriver into a JavascriptExecutor.
     *
     * @return the JavascriptExecutor interface
     * @throws ClassCastException
     *             if JavaScript not supported
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "BC_UNCONFIRMED_CAST")
    public JavascriptExecutor asJavaScriptExecutor() {
        return (JavascriptExecutor) driver;
    }

    /**
     * Casts an WebDriver into a MobileDriver.
     *
     * @param <T>
     *            type of WebElement
     * @return the MobileDriver interface
     * @throws ClassCastException
     *             if the driver is not of MobileDriver type
     */
    @SuppressWarnings("unchecked")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "BC_UNCONFIRMED_CAST")
    public <T extends WebElement> MobileDriver<T> asMobile() {
        return (MobileDriver<T>) driver;
    }

    /**
     * Casts an WebDriver into a WindowsDriver.
     *
     * @param <T>
     *            type of WebElement
     * @return the WindowsDriver interface
     * @throws ClassCastException
     *             if the driver is not of WindowsDriver type
     */
    @SuppressWarnings("unchecked")
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "BC_UNCONFIRMED_CAST")
    public <T extends WebElement> WindowsDriver<T> asWindows() {
        return (WindowsDriver<T>) driver;
    }

    /**
     * Casts an WebDriver into a RemoteWebDriver.
     *
     * @return the RemoteWebDriver interface
     * @throws ClassCastException
     *             if the driver is not of RemoteWebDriver type
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "BC_UNCONFIRMED_CAST")
    public RemoteWebDriver asRemote() {
        return (RemoteWebDriver) driver;
    }

    /**
     * Dispatches and event to a target element per <a href=
     * "https://www.w3.org/TR/2015/REC-dom-20151119/#interface-eventtarget">DOM
     * Level4</a>
     *
     * @param event
     *            the event to dispatch
     * @param element
     *            the element
     * @return status of event per specification
     * @throws UnsupportedOperationException
     *             if the operation fails
     */
    public boolean dispatch(final Event event, final WebElement element) {
        return (Boolean) asJavaScriptExecutor()
            .executeScript(
                "return arguments[0].dispatchEvent(" + event.toString() + ");",
                element);
    }

    /**
     * Bypasses Selenium by executing the click via JavaScript, thus enabling
     * clicking of obscured or otherwise hidden elements.
     *
     * @param element
     *            the element to click on
     */
    public void forceClick(final WebElement element) {
        asJavaScriptExecutor()
            .executeScript(
                "arguments[0].click();",
                element);
    }

    public boolean is(final Class<?> type) {
        return type.isInstance(driver);
    }

    /**
     * Quits the driver swallowing exceptions.
     */
    public void safelyQuit() {
        try {
            log.debug("quiting application");
            driver.quit();
        } catch (final Exception e) {
            // NOTE must handle all exceptions here otherwise JGiven reports
            // are not generated
            log.error("while quiting got", e);
        }
    }

    /**
     * Scrolls an Element into View per <a href=
     * "https://www.w3.org/TR/2016/WD-cssom-view-1-20160317/#extension-to-the-element-interface">CSSOM
     * View Module Element Interface Extensions</a>
     *
     * @param element
     *            the element
     * @return the element
     */
    public WebElement scrollIntoView(final WebElement element) {
        if (driver instanceof WindowsDriver)
            // TODO implement scrolling for WinAppDriver
            // https://github.com/microsoft/WinAppDriver/issues/1538
            // meanwhile do nothing
            return element;

        // ISSUE isDisplayed method is not reliable
        // if (!element.isDisplayed())
        asJavaScriptExecutor()
            .executeScript(
                "arguments[0].scrollIntoViewIfNeeded();",
                element);
        return element;
    }

    /**
     * Retrieves Web context identifier of running mobile application.
     *
     * @return the identifier of first Web context
     * @throws TestRuntimeException
     *             if no Web context/view was found
     */
    @SneakyThrows
    // ISSUE seems no longer in use -- maybe should be deprecated?
    public String webContextIdentifier() {
        // NOTE one iOS the initialization of web context is slow, hence this
        return Failsafe.with(
            Fallback.of(
                (CheckedConsumer<ExecutionAttemptedEvent<? extends String>>) e -> {
                    throw new TestRuntimeException(e.getLastFailure());
                }),
            new RetryPolicy<String>()
                // at most 3 retries by default
                .withMaxDuration(StageEx.pollTimeout)
                .handle(TestRuntimeException.class, WebDriverException.class)
                .onRetry(e -> log.debug("re-attempting finding web view #{}",
                    e.getAttemptCount()))
                .onRetriesExceeded(e -> log.error("retries exceeded.",
                    e.getFailure())))
            .onSuccess(e -> log
                .debug("found web view context {}", e.getResult()))
            .get(() -> asMobile()
                .getContextHandles()
                .stream()
                .peek(contextId -> log.trace("found context {}", contextId))
                .filter(contextId -> contextId.startsWith("WEBVIEW"))
                .findFirst()
                .orElseThrow(
                    () -> new TestRuntimeException("no webview available")));
    }

    /**
     * Represents a <a href=
     * "https://www.w3.org/TR/2015/REC-dom-20151119/#interface-event">DOM
     * Event</a>.
     */
    @AllArgsConstructor
    public static class Event {
        /**
         * The type of this Pointer Event.
         */
        public final Type                type;
        /**
         * <a href=
         * "https://www.w3.org/TR/pointerevents2/#dom-pointereventinit">Event
         * Initialization Dictionary</a>
         */
        public final Map<String, Object> eventInitDict;

        /**
         * @return JavaScript literal representation of this Event
         */
        @Override
        public String toString() {
            return MessageFormat.format("new {0}(\"{1}\",{2})",
                getClass().getSimpleName(),
                type,
                new Gson().toJson(eventInitDict, Map.class));
        }

        /**
         * Represents an event type.
         *
         * @author Adrian Herscu
         */
        public interface Type {
            // marker interface
        }
    }

    /**
     * Represents a <a href=
     * "https://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113/events.html#Events-MouseEvent">DOM
     * MouseEvent</a>.
     *
     * <p>
     * see also <a href=
     * "https://www.w3.org/TR/2019/WD-uievents-20190530/#idl-mouseevent">MouseEvent
     * Working Draft</a>
     * </p>
     */
    public static class MouseEvent extends UIEvent {
        /**
         * @param type
         *            type of event
         * @param eventInitDict
         *            initialization data
         */
        public MouseEvent(
            final Type type,
            final Map<String, Object> eventInitDict) {
            super(type, eventInitDict);
        }
    }

    /**
     * Represents a
     * <a href="https://www.w3.org/TR/2019/REC-pointerevents2-20190404/">DOM
     * Pointer Event.</a>
     */
    public static class PointerEvent extends MouseEvent {
        /**
         * @param type
         *            type of event
         * @param eventInitDict
         *            initialization data
         */
        public PointerEvent(
            final PointerEventType type,
            final Map<String, Object> eventInitDict) {
            super(type, eventInitDict);
        }

        /**
         * Types of <a href=
         * "https://www.w3.org/TR/pointerevents2/#pointer-event-types">Pointer
         * Events</a>
         */
        public enum PointerEventType implements Type {
            pointerover,
            pointerenter,
            pointerdown,
            pointermove,
            pointerup,
            pointercancel,
            pointerout,
            pointerleave,
            gotpointercapture,
            lostpointercapture
        }
    }

    /**
     * Represents a <a href=
     * "https://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113/events.html#Events-UIEvent">DOM
     * UIEvent</a>.
     *
     * <p>
     * see also <a href=
     * "https://www.w3.org/TR/2019/WD-uievents-20190530/#interface-uievent">UIEvent
     * Working Draft</a>
     * </p>
     */
    public static class UIEvent extends Event {
        /**
         * @param type
         *            type of event
         * @param eventInitDict
         *            initialization data
         */
        public UIEvent(
            final Type type,
            final Map<String, Object> eventInitDict) {
            super(type, eventInitDict);
        }
    }
}
