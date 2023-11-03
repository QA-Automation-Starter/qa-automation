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

import static java.util.Objects.*;

import org.testng.*;

import com.saucelabs.saucerest.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.extern.slf4j.*;

/**
 * Reports {@code pass} or {@code fail} to SauceLabs per Web Driver session.
 *
 * <p>
 * See also:
 * </p>
 * <ul>
 * <li><a href=
 * "https://wiki.saucelabs.com/display/DOCS/Setting+Test+Status+to+Pass+or+Fail">Setting
 * Test Status to Pass or Fail</a></li>
 *
 * <li><a href="https://github.com/saucelabs/saucerest-java">Sauce REST API
 * client for Java</a></li>
 * </ul>
 *
 * <p>
 * Add to your {@code testng.xml} file in order to report test statuses to
 * SauceLabs.
 * </p>
 *
 */
@Slf4j
public abstract class AbstractSauceLabsReporter extends TestListenerAdapter {

    @Override
    public void onTestSuccess(final ITestResult testResult) {
        if (nonNull(sauceLabs())) {
            log.debug("reporting test success for {}", testResult.toString());
            WebDriverEx.sessionInfos().forEach(
                sessionInfo -> {
                    log.trace("succeeded on session {} -> {}",
                        sessionInfo.sessionId.toString(),
                        sessionInfo.capabilities.getCapability("sauce:name"));
                    sauceLabs()
                        .jobPassed(sessionInfo.sessionId.toString());
                });
        }
    }

    @Override
    public void onTestFailure(final ITestResult testResult) {
        if (nonNull(sauceLabs())) {
            // FIXME the diagnostics are wrong with non Web Driver tests
            // if the reported test is not setting a Web Driver session
            // then no session id will be found and this log would be misleading
            // same for onTestSuccess below
            log.debug("reporting test failure for {}", testResult.toString());
            WebDriverEx.sessionInfos().forEach(
                sessionInfo -> {
                    log.trace("failed on session {} -> {}",
                        sessionInfo.sessionId.toString(),
                        sessionInfo.capabilities.getCapability("sauce:name"));
                    sauceLabs()
                        .jobFailed(sessionInfo.sessionId.toString());
                });
        }
    }

    /**
     * @return implement to return a {@link SauceREST}.
     */
    protected abstract SauceREST sauceLabs();
}
