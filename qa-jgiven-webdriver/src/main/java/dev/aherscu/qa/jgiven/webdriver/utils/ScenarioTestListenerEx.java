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

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static io.appium.java_client.remote.MobileCapabilityType.*;
import static java.util.Objects.*;
import static lombok.AccessLevel.*;

import java.util.*;
import java.util.stream.*;

import org.testng.*;

import com.google.common.collect.*;
import com.tngtech.jgiven.report.impl.*;
import com.tngtech.jgiven.report.model.*;
import com.tngtech.jgiven.testng.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * TestNG Test listener to enrich JGiven report with additional tags.
 *
 * <p>
 * Currently, following tags are added:
 * </p>
 * <ul>
 * <li>device name</li>
 * <li>platform name</li>
 * <li>platform version</li>
 * </ul>
 * in addition, number of test retries, if any, is added too.
 *
 */
@NoArgsConstructor // required by TestNG framework
@AllArgsConstructor(access = MODULE) // for unit testing
@Slf4j
public final class ScenarioTestListenerEx extends ScenarioTestListener {
    static final String                                 DEVICE_NAME_TAG      =
        "DeviceName";
    static final String                                 PLATFORM_NAME_TAG    =
        "PlatformName";
    static final String                                 PLATFORM_VERSION_TAG =
        "PlatformVersion";
    static final String                                 RETRIES_TAG          =
        "Retries";

    // to be used for unit testing -- initialized via constructor
    private CommonReportHelper                          alternativeReportHelper;
    private Multimap<SessionName, WebDriverSessionInfo> alternativeRemoteSessions;

    @SuppressWarnings("unchecked")
    private static Stream<Map.Entry<String, ReportModel>> reportModelsFor(
        final ITestContext context) {
        return ((Map<String, ReportModel>) context
            .getAttribute(REPORT_MODELS_ATTRIBUTE))
                .entrySet()
                .stream()
                .filter(reportModelEntry -> ((TestRunner) context)
                    .getTestClasses()
                    .stream()
                    .map(IClass::getName)
                    .anyMatch(testClassName -> testClassName.equals(
                        reportModelEntry.getKey())));
    }

    private static void reportRetries(
        final ReportModel reportModel,
        final ScenarioModel scenario) {

        val qualifiedMethodName = reportModel.getClassName()
            + DOT + scenario.getTestMethodName();

        if (TestRetryAnalyzer.retryCounters.containsKey(qualifiedMethodName)) {

            val retriesTag = new Tag(RETRIES_TAG,
                RETRIES_TAG,
                TestRetryAnalyzer.retryCounters
                    .get(qualifiedMethodName)
                    .toString())
                        .setPrependType(true);

            reportModel.addTag(retriesTag);
            scenario.addTag(retriesTag);
        }
    }

    // NOTE: this duplication is because
    // ReportModel has nothing in common with ScenarioModel
    private static void reportSession(
        final WebDriverSessionInfo session,
        final ScenarioModel scenario) {
        scenario.addTag(new Tag(DEVICE_NAME_TAG, DEVICE_NAME_TAG,
            session.capabilities.getCapability(DEVICE_NAME)));
        scenario.addTag(new Tag(PLATFORM_NAME_TAG, PLATFORM_NAME_TAG,
            session.capabilities.getCapability(PLATFORM_NAME)));
        scenario.addTag(new Tag(PLATFORM_VERSION_TAG, PLATFORM_VERSION_TAG,
            session.capabilities.getCapability(PLATFORM_VERSION)));
    }

    private static void reportSession(
        final WebDriverSessionInfo session,
        final ReportModel reportModel) {
        reportModel.addTag(new Tag(DEVICE_NAME_TAG, DEVICE_NAME_TAG,
            session.capabilities.getCapability(DEVICE_NAME)));
        reportModel.addTag(new Tag(PLATFORM_NAME_TAG, PLATFORM_NAME_TAG,
            session.capabilities.getCapability(PLATFORM_NAME)));
        reportModel.addTag(new Tag(PLATFORM_VERSION_TAG, PLATFORM_VERSION_TAG,
            session.capabilities.getCapability(PLATFORM_VERSION)));
    }

    @Override
    public void onFinish(final ITestContext context) {
        reportModelsFor(context)
            .peek(reportModelEntry -> log.trace("report {}", reportModelEntry))
            .map(Map.Entry::getValue)
            .forEach(reportModel -> {
                log.trace("adorning report for {}",
                    reportModel.getClassName());

                sessionsFor(reportModel)
                    .peek(sessions -> log
                        .trace("sessions for this class {}", sessions))
                    .forEach(session -> reportSession(
                        session.getValue(), reportModel));

                reportModel.getScenarios()
                    .forEach(scenario -> {
                        log.trace("adorning scenario {}",
                            scenario.getTestMethodName());

                        reportRetries(reportModel, scenario);

                        val sessions = sessionsFor(reportModel)
                            .filter(
                                session -> isEmpty(session.getKey().methodName)
                                    || session.getKey().methodName
                                        .equals(scenario.getTestMethodName()));

                        // FIXME undocumented flag
                        if (isNull(System.getProperty("report-all-devices"))) {
                            sessions.reduce((first, second) -> second)
                                .ifPresent(session -> reportSession(
                                    session.getValue(), scenario));
                        } else {
                            sessions.forEach(session -> reportSession(
                                session.getValue(), scenario));
                        }
                    });

                reportHelper().finishReport(reportModel);
            });
    }

    private Multimap<SessionName, WebDriverSessionInfo> remoteSessions() {
        return nonNull(alternativeRemoteSessions)
            ? alternativeRemoteSessions
            : remoteSessions;
    }

    private CommonReportHelper reportHelper() {
        return nonNull(alternativeReportHelper)
            ? alternativeReportHelper
            : new CommonReportHelper();
    }

    private Stream<Map.Entry<SessionName, WebDriverSessionInfo>> sessionsFor(
        final ReportModel reportModel) {
        return remoteSessions()
            .entries()
            .stream()
            .filter(sessionInfoEntry -> sessionInfoEntry
                .getKey().className.equals(reportModel.getSimpleClassName()))
            .peek(s -> log.debug("session {}", s));
    }
}
