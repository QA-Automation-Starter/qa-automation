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

package dev.aherscu.qa.testing.example.scenarios.tutorial;

import static dev.aherscu.qa.testing.example.scenarios.tutorial._5_TestingMobileApplicationOnSauceLabs.*;
import static java.lang.Thread.*;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.openqa.selenium.*;
import org.testng.annotations.*;

import com.tngtech.jgiven.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.testng.*;

import edu.umd.cs.findbugs.annotations.*;
import io.appium.java_client.*;
import lombok.*;
import lombok.extern.slf4j.*;

@As("Testing with JGiven")
@Slf4j
public class _6_TestingWithJGiven extends
    ScenarioTest<_6_TestingWithJGiven.Fixtures, _6_TestingWithJGiven.Actions, _6_TestingWithJGiven.Verifications> {

    private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    @DataProvider(parallel = true)
    static Object[][] environmentLabels() {
        return new Object[][] {
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") },
            { new Label("TBD") }
        };
    }

    @Test(dataProvider = "environmentLabels")
    public void shouldAllowLogin(final Label label) {
        given()
            .application_installed(webDriver.get());

        then()
            .valid_email_required();

        when()
            .entering_environment(label)
            .and().hiding_keyboard();

        then()
            .can_tap_to_login();
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @AfterMethod(alwaysRun = true) // important, otherwise we may leak resources
    private void afterMethodCloseWebDriver() {
        log.debug("quitting");
        webDriver.get().quit();
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @BeforeMethod
    @SneakyThrows
    private void beforeMethodOpenWebDriver() {
        // NOTE: ensure you have app.apk uploaded to SauceLabs
        // curl -u "TBD:TBD" -X POST
        // https://saucelabs.com/rest/v1/storage/TBD --data-binary
        // @TBD.apk
        log.debug("opening web driver");
        webDriver.set(saucelabsApp(getClass().getSimpleName()
            + "#" + currentThread().getId()));
        webDriver.get().manage().timeouts().implicitlyWait(5, SECONDS);
    }

    static class Actions extends Stage<Actions> {
        @ScenarioState
        private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

        Actions entering_environment(final Label label) {
            log.debug("entering environment {}", label);
            webDriver.get().findElement(By.xpath("//input"))
                .sendKeys(label.value);

            return self();
        }

        Actions hiding_keyboard() {
            log.debug("hiding keyboard");
            ((HidesKeyboard) webDriver.get()).hideKeyboard();
            return self();
        }
    }

    static class Fixtures extends Stage<Fixtures> {
        @ScenarioState
        private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

        Fixtures application_installed(
            @Hidden final WebDriver webDriver) {
            log.debug("application installed {}", webDriver);
            this.webDriver.set(webDriver);
            return self();
        }
    }

    @AllArgsConstructor
    static class Label {
        final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    static class Verifications extends Stage<Verifications> {
        @ScenarioState
        private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

        Verifications can_tap_to_login() {
            log.debug("can tap to login");
            assertThat(webDriver.get()
                .findElement(By.xpath("//*[text()='Tap anywhere to Login']"))
                .isDisplayed(),
                is(true));

            return self();
        }

        Verifications valid_email_required() {
            log.debug("valid email required");
            assertThat(webDriver.get()
                .findElements(By
                    .xpath("//*[text()='Please enter valid email']")),
                not(empty()));
            return self();
        }
    }
}
