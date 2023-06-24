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

package dev.aherscu.qa.testing.utils.testng;

import org.testng.*;

import dev.aherscu.qa.testing.utils.*;

/**
 * Disables SSL certificate validation before the TestNG test suites begins
 * execution. This allows running with self-signed certificates.
 * 
 * <p>
 * Usage: <a href=
 * "http://testng.org/doc/documentation-main.html#listeners-dev.aherscu.qa.tester.utils.testng-xml">TestNG
 * Listeners</a>
 * </p>
 *
 * @author Adrian Herscu
 *
 */
public class DisableSslCertificateValidation implements ISuiteListener {

    @Override
    public void onStart(final ISuite suite) {
        TrustAllX509TrustManager.disableSslCertificateValidation();
    }

    @Override
    public void onFinish(final ISuite suite) {
        // nothing to do here
    }
}
