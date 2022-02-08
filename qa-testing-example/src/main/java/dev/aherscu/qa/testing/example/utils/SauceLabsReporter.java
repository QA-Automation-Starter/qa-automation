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

package dev.aherscu.qa.testing.example.utils;

import static dev.aherscu.qa.tester.utils.UriUtils.*;
import static java.util.Objects.*;

import com.saucelabs.saucerest.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class SauceLabsReporter extends AbstractSauceLabsReporter {

    @Override
    protected SauceREST sauceLabs() {
        try {
            val seleniumUrl =
                quietUriFrom(System.getProperty("saucelabs.reporter.url"));
            val accountName = requireNonNull(usernameFrom(seleniumUrl),
                "must have an account name");
            // NOTE: this access key can be regenerated via
            // https://app.saucelabs.com/user-settings
            val accessKey = requireNonNull(passwordFrom(seleniumUrl),
                "must have a password");
            return new SauceREST(accountName, accessKey, DataCenter.US);
        } catch (final Throwable t) {
            log.warn("SauceLabs not defined -- {}", t.getMessage());
            return null;
        }
    }
}
