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

import static dev.aherscu.qa.jgiven.commons.utils.WebDriverEx.*;
import static java.util.Objects.*;

import java.util.*;

import javax.annotation.concurrent.*;

import org.hamcrest.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import io.appium.java_client.appmanagement.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic browser verifications.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@ThreadSafe
@Slf4j
public class WebDriverVerifications<SELF extends WebDriverVerifications<SELF>>
    extends GenericVerifications<WebDriverScenarioType, SELF>
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
     * Verifies that a specific element matches given criteria.
     *
     * @param locator
     *            the element to look for
     * @param matcher
     *            the criteria
     * @return {@link #self()}
     * @throws NoSuchElementException
     *             if the element does not exist
     */
    public SELF element(final By locator,
        final Matcher<WebElement> matcher) {
        try (val assertElementContext = assertElementTimer.time()) {
            return eventually(self -> {
                log.debug("element {} {}", locator, matcher);
                MatcherAssert.assertThat(element(locator), matcher);
                return self();
            });
        }
    }

    /**
     * Verifies that a specific set of elements matches given criteria.
     *
     * @param locator
     *            the elements to look for
     * @param matcher
     *            the criteria
     * @return {@link #self()}
     */
    public SELF elements(
        final By locator,
        final Matcher<Iterable<WebElement>> matcher) {
        return eventually(self -> {
            log.debug("expecting {}", matcher);
            MatcherAssert.assertThat(elements(locator), matcher);
            return self;
        });
    }

    /**
     * Verifies application state matches specified criteria.
     *
     * @param matcher
     *            verification criteria
     * @param appId
     *            application identifier
     * @return {@link #self()}
     */
    public SELF the_application(final Matcher<ApplicationState> matcher,
        final String appId) {
        return eventually_assert_that(
            () -> thisWebDriver().asMobile().queryAppState(appId),
            matcher);
    }

    /**
     * Verifies the title is as expected.
     *
     * @param expected
     *            the expected title
     * @return {@link #self()}
     */
    public SELF the_title(final Matcher<String> expected) {
        return eventually(self -> {
            log.debug("title matches {}", expected);
            MatcherAssert.assertThat(thisWebDriver().asGeneric().getTitle(),
                expected);
            return self();
        });
    }

    /**
     * Finds an element by specified locator and brings it into view. If the
     * locator is matching multiple elements then the first one is returned.
     *
     * <p>
     * {@code locateTimer} metric will be updated not including the scrolling
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
     * Finds all elements matching specified locator.
     *
     * <p>
     * {@code locateTimer} metric will be updated not including the scrolling
     * into view.
     * </p>
     *
     * @param locator
     *            the locator
     * @return the element
     */
    protected List<WebElement> elements(final By locator) {
        log.debug("locating {}", locator);
        return elements(locator, thisWebDriver().asGeneric());
    }

    /**
     * Scrolls specified element into view.
     *
     * <p>
     * {@code scrollIntoView} metric will be updated.
     * </p>
     *
     * @param element
     *            the element to scroll into view
     * @return the element
     */
    @Override
    protected WebElement scrollIntoView(final WebElement element) {
        try (val scrollIntoViewTimerContext = scrollIntoViewTimer.time()) {
            log.debug("scrolling to {}", descriptionOf(element));
            thisWebDriver().scrollIntoView(element);
        }
        return element;
    }

    protected final WebDriverEx thisWebDriver() {
        return requireNonNull(
            requireNonNull(webDriver, "web driver fixtures stage omitted")
                .get(),
            "web driver not initialized");
    }
}
