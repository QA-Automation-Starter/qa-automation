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

import static dev.aherscu.qa.jgiven.commons.utils.WebDriverEx.*;
import static java.lang.Boolean.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.remote.CapabilityType.*;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import java.util.Optional;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.*;
import org.testng.annotations.*;

import com.google.common.collect.*;

import dev.aherscu.qa.testing.utils.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

/**
 * Tests WebDriver mechanism.
 *
 * @author Adrian Herscu
 */
@Test
@Slf4j
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
    justification = "short lived test instance")
public class WebDriverExTest {
    @Test(expectedExceptions = { SessionNotCreatedException.class })
    // FIXME suddenly the exception type changed, perhaps due to some dependency change?
    // Happens under Github Actions -- couldn't reproduce otherwise
    // @Ignore("Expected exception of type class org.openqa.selenium.SessionNotCreatedException but got java.lang.NoClassDefFoundError: org/openqa/selenium/mobile/NetworkConnection")
    public void shouldFailGettingWebDriver() {
        webDriverFor(new ImmutableCapabilities(ImmutableMap.builder()
            .put("-x:class", "org.openqa.selenium.remote.RemoteWebDriver")
            .put("-x:url", "http://host/path")
            .put("-x:target", "http://host/path")
            .put(BROWSER_NAME, "chrome")
            .build()));
    }

    /**
     * Tests creation of {@link PointerEvent}; all others being similar.
     */
    @SuppressWarnings({ "static-method", "serial" })
    public void shouldCreateEvent() {
        assertThat(
            new WebDriverEx.PointerEvent(
                WebDriverEx.PointerEvent.PointerEventType.pointerdown,
                new HashMap<String, Object>() {
                    {
                        put("bubbles", TRUE);
                    }
                })
                .toString(),
            is("new PointerEvent(\"pointerdown\",{\"bubbles\":true})"));
    }

    @Test(enabled = true) // just an experiment
    public void shouldRetry() {
        Failsafe.with(
            Fallback.of(e -> {
                log.debug(">>>> {}", e.getLastFailure().toString());
                return Optional.empty();
            }),
            new RetryPolicy<Optional<WebDriverEx>>()
                .withMaxDuration(Duration.ofSeconds(10))
                .handle(WebDriverException.class)
                .handle(TimeoutExceededException.class)
                .onRetry(
                    e -> log.debug("failed to initialize #{}; retrying",
                        e.getAttemptCount()))
                .onRetriesExceeded(
                    e -> log.error("retries exceeded")),
            Timeout.<Optional<WebDriverEx>> of(Duration.ofSeconds(1))
                .withCancel(true)
                .onFailure(e -> log.debug("toooooo much time")))
            .onSuccess(
                e -> log.debug("success {}", e.getResult().get()))
            .get(() -> {
                ThreadUtils.sleep(2000);
                return Optional
                    .of(new WebDriverEx(new HtmlUnitDriver(),
                        new MutableCapabilities()));
                // throw new WebDriverException();
                // throw new ClassNotFoundException("bum trah");
            });
    }
}
