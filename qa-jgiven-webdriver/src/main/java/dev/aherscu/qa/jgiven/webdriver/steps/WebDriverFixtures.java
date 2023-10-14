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
package dev.aherscu.qa.jgiven.webdriver.steps;

import static dev.aherscu.qa.jgiven.commons.utils.WebDriverEx.*;
import static dev.aherscu.qa.testing.utils.UrlUtils.*;
import static java.util.Objects.*;

import java.lang.SuppressWarnings;
import java.util.function.*;

import javax.annotation.concurrent.*;

import org.openqa.selenium.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.webdriver.formatters.*;
import dev.aherscu.qa.jgiven.webdriver.model.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic web driver fixtures. If WebDriver's capabilities contain
 * {@link WebDriverFixtures#AUTO_QUIT}, then closes the WebDriver by end of
 * scenario.
 *
 * @param <SELF>
 *            the type of the subclass
 * @author aherscu
 */
@ThreadSafe
@Slf4j
public class WebDriverFixtures<SELF extends WebDriverFixtures<SELF>>
    extends GenericFixtures<WebDriverScenarioType, SELF> {

    /**
     * Add to capabilities in order to automatically quit the web driver by end
     * of scenario; value does not matter.
     */
    public static final String               AUTO_QUIT = "autoQuit";

    /**
     * The given Web Driver.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<WebDriverEx> webDriver =
        new ThreadLocal<>();

    /**
     * Sets a web driver for this scenario. If it has has a capability named
     * {@link WebDriverFixtures#AUTO_QUIT}, then it will automatically quit
     * after scenario.
     *
     * @param webDriver
     *            the driver
     * @return {@link #self()}
     * @throws NullPointerException
     *             if the driver reference was null
     */
    @NestedSteps
    public SELF a_web_driver(
        @SuppressWarnings("hiding") @WebDriverFormatter.Annotation final WebDriverEx webDriver) {
        log.debug("setting web driver {}", webDriver);
        // TODO pretty print
        currentStep
            .addAttachment(Attachment.plainText(webDriver.originalCapabilities
                .asMap()
                .toString()));
        this.webDriver.set(requireNonNull(webDriver,
            "must provide a web driver"));
        return self();
    }

    /**
     * Opens Web application at specified host if not already open.
     *
     * @param applicationUrl
     *            the location of the application
     * @return {@link #self()}
     */
    public SELF at(final String applicationUrl) {
        val currentUrl = thisWebDriver().asGeneric().getCurrentUrl();

        log.trace("currently at {} asking for {}",
            currentUrl, applicationUrl);

        // NOTE: browsers opened by Selenium may point to an invalid URL
        // e.g. Chrome on SauceLabs points at data: which is not a valid URL
        if (isUrl(currentUrl)
            && hostOf(currentUrl)
                .equals(hostOf(applicationUrl))) {
            log.debug("already at {}", applicationUrl);
        } else {
            log.debug("opening {}", applicationUrl);
            thisWebDriver().asGeneric().get(applicationUrl);
        }
        return self();
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
    protected SELF context(final Predicate<String> byRule) {
        return context(byRule, (ContextAware) thisWebDriver());
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
        return requireNonNull(webDriver.get(), "web driver not initialized");
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @AfterScenario
    private void afterScenarioQuitWebDriver() {
        if (nonNull(thisWebDriver().originalCapabilities
            .getCapability(AUTO_QUIT))) {
            log.debug("automatically quitting after scenario");
            thisWebDriver().safelyQuit();
        }
    }
}
