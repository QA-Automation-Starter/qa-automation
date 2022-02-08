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

import static dev.aherscu.qa.jgiven.commons.utils.WebDriverEx.*;
import static java.lang.Boolean.*;
import static java.util.Objects.*;

import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

import javax.annotation.concurrent.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

/**
 * Generic browser actions.
 *
 * @param <SELF>
 *            the type of the subclass
 * @author aherscu
 */
@ThreadSafe
@Slf4j
@SuppressWarnings({ "boxing", "serial", "ClassWithTooManyMethods" })
public class WebDriverActions<SELF extends WebDriverActions<SELF>>
    extends GenericActions<WebDriverScenarioType, SELF>
    implements MayAttachScreenshots<SELF> {
    /**
     * Expected browser object.
     */
    @ExpectedScenarioState
    protected ThreadLocal<WebDriverEx> webDriver;

    @Override
    @Hidden
    public SELF attaching_screenshot() {
        return attaching_screenshot(0);
    }

    @Override
    @Hidden
    public SELF attaching_screenshot(final int delayMs) {
        attachScreenshot(thisWebDriver(), delayMs);
        return self();
    }

    /**
     * Clicks a specified element, retrying as much as configured; the element
     * is retrieved via specified locator before each retry.
     *
     * @param locator
     *            the element
     * @return {@link #self()}
     */
    public SELF clicking(final By locator) {
        return clicking(() -> element(locator));
    }

    /**
     * Clicks a specified element, retrying as much as configured; the element
     * is retrieved via specified supplier before each retry.
     *
     * <p>
     * <tt>click</tt> metric will be updated and includes locating the element
     * and scrolling into view.
     * </p>
     *
     * @param elementSupplier
     *            late-bound element
     * @return {@link #self()}
     */
    public SELF clicking(final Supplier<WebElement> elementSupplier) {
        try (val clickTimerContext = clickTimer.time()) {
            return retrying(self -> clicking_once(elementSupplier));
        }
    }

    public SELF clicking_once(final By locator) {
        return clicking_once(() -> element(locator));
    }

    public SELF clicking_once(final Supplier<WebElement> elementSupplier) {
        val element = elementSupplier.get();
        log.debug("clicking {}", descriptionOf(element));
        element.click();
        return self();
    }

    /**
     * Finds all elements matching specified locator.
     *
     * <p>
     * <tt>locateTimer</tt> metric will be updated not including the scrolling
     * into view.
     * </p>
     *
     * @param locator
     *            the locator
     * @return the element
     */
    @Hidden
    public List<WebElement> elements(final By locator) {
        log.debug("locating {}", locator);
        return elements(locator, thisWebDriver().asGeneric());
    }

    /**
     * Clicks a specified element even if hidden or out of view, retrying as
     * much as configured; the element is retrieved via specified supplier
     * before each retry.
     *
     * <p>
     * <tt>click</tt> metric will be updated and includes locating the element.
     * </p>
     *
     * @param elementSupplier
     *            late-bound element
     * @return {@link #self()}
     */
    @As("clicking")
    public SELF forcefullyClicking(final Supplier<WebElement> elementSupplier) {
        try (val clickTimerContext = clickTimer.time()) {
            return retrying(self -> {
                val element = elementSupplier.get();
                log.debug("forcefully clicking {}", descriptionOf(element));
                thisWebDriver().forceClick(element);
                return self;
            });
        }
    }

    /**
     * Clicks a specified element even if hidden or out of view, retrying as
     * much as configured; the element is retrieved via specified locator before
     * each retry.
     *
     * @param locator
     *            the element
     * @return {@link #self()}
     */
    @As("clicking")
    public SELF forcefullyClicking(final By locator) {
        return forcefullyClicking(() -> element(locator));
    }

    /**
     * Long presses a specified element, retrying as much as configured; the
     * element is retrieved via specified supplier before each retry.
     *
     * @param elementSupplier
     *            late-bound element
     * @return {@link #self()}
     */
    public SELF long_pressing(final Supplier<WebElement> elementSupplier) {
        return retrying(self -> {
            val element = elementSupplier.get();
            log.debug("long pressing {}", descriptionOf(element));
            thisWebDriver()
                .dispatch(new WebDriverEx.PointerEvent(
                    PointerEvent.PointerEventType.pointerdown,
                    new HashMap<String, Object>() {
                        {
                            put("bubbles", TRUE);
                        }
                    }),
                    element);
            return self;
        });
    }

    /**
     * Long presses a specified element, retrying as much as configured;the
     * element is retrieved via specified locator before each retry.
     *
     * @param locator
     *            the element
     * @return {@link #self()}
     */
    public SELF long_pressing(final By locator) {
        return long_pressing(() -> element(locator));
    }

    /**
     * Opens an URL into the given browser.
     *
     * @param url
     *            the URL to open
     * @return {@link #self()}
     */
    public SELF opening(final URI url) {
        thisWebDriver().asGeneric().get(url.toString());

        if (!thisWebDriver().is(JavascriptExecutor.class))
            return self();

        return retrying(
            self -> {
                if (thisWebDriver().asJavaScriptExecutor()
                    .executeScript("return document.readyState")
                    .equals("complete"))
                    return self;
                throw new FailsafeException();
            });
    }

    @SneakyThrows(URISyntaxException.class)
    public SELF opening(final String url) {
        return opening(new URI(url));
    }

    /**
     * Rotates devices to specified orientation.
     *
     * @param orientation
     *            the orientation
     *
     * @return #self()
     */
    public SELF rotating_device_to(final ScreenOrientation orientation) {
        thisWebDriver().asMobile().rotate(orientation);
        return self();
    }

    /**
     * Sends the application to background for specified duration.
     *
     * @param duration
     *            the duration
     * @return {@link #self()}
     */
    public SELF sending_application_to_background_for(final Duration duration) {
        log.debug("sending application to background for {}", duration);
        thisWebDriver().asMobile().runAppInBackground(duration);
        log.debug("returned from background");
        return self();
    }

    /**
     * Submits the form containing the specified field.
     *
     * @param locator
     *            the field
     * @return {@link #self()}
     */
    // TODO provide a late-bound alternative like in long_pressing
    public SELF submitting_the_form_containing(final By locator) {
        return retrying(self -> {
            log.debug("submitting {}", locator);
            element(locator).submit();
            return self();
        });
    }

    /**
     * Terminates the specified application.
     *
     * @param appId
     *            the application identifier
     * @return {@link #self()}
     */
    public SELF terminating_application(final String appId) {
        log.debug("application {} was running and stopped successfully: {}",
            appId,
            thisWebDriver().asMobile().terminateApp(appId));
        return self();
    }

    /**
     * Types into a field, clearing it before and hiding the keyboard
     * afterwards.
     *
     * @param value
     *            the value to type
     * @param elementSupplier
     *            late-bound element
     * @return {@link #self()}
     */
    public SELF typing_$_into(
        @Quoted final String value,
        final Supplier<WebElement> elementSupplier) {
        try (val sendKeysTimerContext = sendKeysTimer.time()) {
            return retrying(self -> {
                val element = elementSupplier.get();
                log.debug("typing {} into {}", value, descriptionOf(element));
                // NOTE: this method does not work on ios
                // element.sendKeys(Keys.CONTROL + "a");
                // element.sendKeys(Keys.DELETE);
                element.clear();
                element.sendKeys(value);
                return self.hiding_keyboard();
            });
        }
    }

    /**
     * Types into a field, clearing it before and hiding the keyboard
     * afterwards.
     *
     * @param value
     *            the value to type
     * @param locator
     *            the field
     * @return {@link #self()}
     */
    public SELF typing_$_into(
        @Quoted final String value,
        final By locator) {
        return typing_$_into(value, () -> element(locator));
    }

    public SELF typing_$_into_without_clearing(
        @Quoted final String value,
        final Supplier<WebElement> elementSupplier) {
        try (val sendKeysTimerContext = sendKeysTimer.time()) {
            return retrying(self -> {
                val element = elementSupplier.get();
                log.debug("typing {} into {}", value, descriptionOf(element));
                element.sendKeys(value);
                return self.hiding_keyboard();
            });
        }
    }

    public SELF typing_$_into_without_clearing(
        @Quoted final String value,
        final By locator) {
        return typing_$_into_without_clearing(value, () -> element(locator));
    }

    /**
     * Activates specified application.
     *
     * @param appId
     *            the application identifier
     * @return {@link #self()}
     */
    @Hidden
    protected SELF activating_application(final String appId) {
        log.debug("activating application {}", appId);
        thisWebDriver().asMobile().activateApp(appId);
        return self();
    }

    /**
     * Finds an element by specified locator and brings it into view. If the
     * locator is matching multiple elements then the first one is returned.
     *
     * <p>
     * <tt>locateTimer</tt> metric will be updated not including the scrolling
     * into view.
     * </p>
     *
     * @param locator
     *            the locator
     * @return the element
     * @throws NoSuchElementException
     *             If no matching elements are found
     */
    @Hidden
    protected WebElement element(final By locator) {
        log.debug("locating {}", locator);
        return element(locator, thisWebDriver().asGeneric());
    }

    /**
     * Retrieves matching DOM elements by specified locator and context. Retries
     * several time if the count of elements is zero.
     *
     * <p>
     * <tt>locateTimer</tt> metric will be updated.
     * </p>
     *
     * @param locator
     *            the locator
     * @return list of DOM elements
     */
    @Hidden
    protected List<WebElement> ensureElements(final By locator) {
        log.debug("locating {}", locator);
        return ensureElements(locator, thisWebDriver().asGeneric());
    }

    /**
     * Retrieves matching DOM elements by specified locator and context. Retries
     * several time if the count of elements is zero.
     *
     * <p>
     * <tt>locateTimer</tt> metric will be updated.
     * </p>
     *
     * @param locator
     *            the locator
     * @return true if there are matching DOM elements
     */
    @Hidden
    protected boolean hasElements(final By locator) {
        return !ensureElements(locator).isEmpty();
    }

    /**
     * Hides the keyboard by clicking on the screen.
     *
     * @return {@link #self()}
     */
    @Hidden
    protected SELF hiding_keyboard() {
        log.debug("hiding the keyboard");
        // WORKAROUND: not supported on all devices or operating systems
        // webDriver.get().asMobile().hideKeyboard();
        return forcefullyClicking(By.xpath("/html/body"));
        // return self();
    }

    /**
     * Scrolls specified element into view.
     *
     * <p>
     * <tt>scrollIntoView</tt> metric will be updated.
     * </p>
     *
     * @param element
     *            the element to scroll into view
     * @return the element
     */
    @Hidden
    @Override
    protected WebElement scrollIntoView(final WebElement element) {
        try (val scrollIntoViewTimerContext = scrollIntoViewTimer.time()) {
            log.debug("scrolling to {}", descriptionOf(element));
            thisWebDriver().scrollIntoView(element);
        }
        return element;
    }

    /**
     * Switches to specified Appium context.
     *
     * @param byRule
     *            naming rule of Appium context
     * @return {@link #self()}
     * @throws NoSuchContextException
     *             if no such context exists
     */
    @Hidden
    protected SELF switching_to_context(final Predicate<String> byRule) {
        return context(byRule, thisWebDriver().asMobile());
    }

    /**
     * Switches to specified window.
     *
     * @param nameOrHandle
     *            the name of the window
     * @return {@link #self()}
     */
    @Hidden
    protected SELF switching_to_window(@Hidden final String nameOrHandle) {
        log.debug("switching to window {}", nameOrHandle);
        thisWebDriver().asGeneric().switchTo().window(nameOrHandle);
        return self();
    }

    protected final WebDriverEx thisWebDriver() {
        return requireNonNull(
            requireNonNull(webDriver, "web driver fixtures stage omitted")
                .get(),
            "web driver not initialized");
    }
}
