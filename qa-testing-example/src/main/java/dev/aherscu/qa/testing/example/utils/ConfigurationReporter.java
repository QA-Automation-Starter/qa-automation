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

import static dev.aherscu.qa.jgiven.commons.utils.DryRunAspect.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.config.AbstractConfiguration.*;
import static io.appium.java_client.remote.MobileCapabilityType.*;

import java.io.*;

import org.apache.commons.configuration.*;
import org.testng.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.tester.utils.logging.*;
import dev.aherscu.qa.testing.example.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * On finish, reports following configuration properties:
 * <ul>
 * <li>version</li>
 * <li>number of parallel devices used to run the test</li>
 * <li>list of devices used throughout the test</li>
 * </ul>
 */
@Slf4j
public class ConfigurationReporter implements ISuiteListener {
    @Override
    public void onStart(final ISuite suite) {
        // nothing to do here
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "DM_DEFAULT_ENCODING",
        justification = "invalid detection")
    @Override
    @SneakyThrows
    @SuppressWarnings("boxing")
    public void onFinish(final ISuite suite) {
        if (dryRun) {
            log.info("dry run -- skipping");
            return;
        }

        val configuration =
            new TestConfiguration(
                new DefaultConfigurationBuilder(CONFIGURATION_SOURCES)
                    .getConfiguration());

        val configurationReportFileName =
            System.getProperty("configuration.report.file",
                defaultIfBlank(
                    suite.getParameter("configuration-report-file"),
                    "configuration-report.txt"));

        log.info("reporting configuration into {}",
            configurationReportFileName);

        try (val output = new FileOutputStream(configurationReportFileName);
            val logging = new LoggingOutputStream(output, log::info);
            val writer = new PrintWriter(logging)) {

            writer.println("Number of parallel devices:"
                + UnitilsScenarioTest.concurrency.intValue());
            writer.println("List of devices used:");
            configuration.requiredCapabilities()
                .stream()
                .map(deviceCapabilities -> deviceCapabilities
                    .getCapability(DEVICE_NAME)
                    + COLON
                    + deviceCapabilities.getCapability(PLATFORM_VERSION))
                .forEach(writer::println);

            log.debug("reported configuration into {}",
                configurationReportFileName);
        }
        // DELETEME -- marked as not thrown but spotbugs
        // catch (final Exception e) {
        // log.warn("failed reporting configuration due to {}",
        // e.getMessage());
        // }
    }
}
