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

import static dev.aherscu.qa.jgiven.commons.utils.ScenarioTestListenerEx.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static io.appium.java_client.remote.options.SupportsDeviceNameOption.DEVICE_NAME_OPTION;
import static io.appium.java_client.remote.options.SupportsPlatformVersionOption.PLATFORM_VERSION_OPTION;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.remote.CapabilityType.*;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import com.google.common.collect.*;
import com.google.gson.*;
import com.tngtech.jgiven.report.impl.*;
import com.tngtech.jgiven.report.model.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Tests {@link ScenarioTestListenerEx} with mocked dependencies.
 */
@Slf4j
public class ScenarioTestListenerExTest {

    // NOTE: the report model allows multiple classes, however the TestNG
    // ITestListener#onFinish is called per class
    private static final String CLASS_EXCLUDED           =
        "ClassExcluded";
    private static final String CLASS_INCLUDED           =
        "ClassIncluded";

    private static final Gson   GSON                     =
        new GsonBuilder().setPrettyPrinting().create();

    private static final String METHOD_WITHOUT_SESSIONS  =
        "methodWithoutSessions";
    private static final String METHOD_WITH_ONE_SESSION  =
        "methodWithOneSession";
    private static final String METHOD_WITH_TWO_SESSIONS =
        "methodWithTwoSessions";

    private static final String T_DEVICE_1               = "Device 1";
    private static final String T_DEVICE_2               = "Device 2";
    private static final String T_PLATFORM_1             = "Platform 1";
    private static final String T_PLATFORM_2             = "Platform 2";
    private static final String T_VERSION_1              = "Version 1";
    private static final String T_VERSION_2              = "Version 2";

    static {
        // TODO there should be a test specific without this flag
        System.setProperty("report-all-devices", EMPTY);
    }

    private Map<String, ReportModel> reportModels;

    private static ITestClass mockedTestClassFor(final String aClass) {
        val mockedTestClass = mock(ITestClass.class);
        when(mockedTestClass.getName())
            .thenReturn(aClass);
        return mockedTestClass;
    }

    private static TestRunner mockedTestRunner(
        final Collection<ITestClass> testClasses,
        final Map<String, ReportModel> reportModels) {
        val mockedTestContext = mock(TestRunner.class);
        when(mockedTestContext.getAttribute(REPORT_MODELS_ATTRIBUTE))
            .thenReturn(reportModels);
        when(mockedTestContext.getTestClasses())
            .thenReturn(testClasses);
        return mockedTestContext;
    }

    private static ReportModel reportModelOf(
        final String className,
        final String... methodNames) {

        val reportModel = new ReportModel();

        reportModel.setClassName(className);
        reportModel.setScenarios(
            stream(methodNames)
                .map(ScenarioTestListenerExTest::scenarioModelOf)
                .collect(toList()));

        return reportModel;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ReportModel> reportModelsFrom(
        final ITestContext testContext) {
        return (Map<String, ReportModel>) testContext
            .getAttribute(REPORT_MODELS_ATTRIBUTE);
    }

    private static ScenarioModel scenarioModelOf(final String methodName) {
        val scenarioModel = new ScenarioModel();
        scenarioModel.setTestMethodName(methodName);
        return scenarioModel;
    }

    private static String tagMapKeyOf(final String name, final String value) {
        return name + DASH + value;
    }

    @BeforeClass
    public void beforeClassPopulateReportModel() {
        val mockedTestContext =
            mockedTestRunner(
                singletonList(mockedTestClassFor(CLASS_INCLUDED)),
                ImmutableMap.<String, ReportModel> builder()
                    .put(CLASS_INCLUDED,
                        reportModelOf(CLASS_INCLUDED,
                            METHOD_WITH_TWO_SESSIONS,
                            METHOD_WITH_ONE_SESSION,
                            METHOD_WITHOUT_SESSIONS))
                    .put(CLASS_EXCLUDED,
                        reportModelOf(CLASS_EXCLUDED,
                            METHOD_WITH_TWO_SESSIONS))
                    .build());

        new ScenarioTestListenerEx(
            mock(CommonReportHelper.class),
            ImmutableMultimap.<SessionName, WebDriverSessionInfo> builder()
                .putAll(SessionName.builder()
                    .className(CLASS_INCLUDED)
                    .methodName(METHOD_WITH_TWO_SESSIONS).build(),
                    // NOTE a method can zero or more WebDriver sessions
                    WebDriverSessionInfo.builder()
                        .capabilities(new DesiredCapabilitiesEx()
                            .with(DEVICE_NAME_OPTION, T_DEVICE_2)
                            .with(PLATFORM_NAME, T_PLATFORM_2)
                            .with(PLATFORM_VERSION_OPTION, T_VERSION_2))
                        .build(),
                    WebDriverSessionInfo.builder()
                        .capabilities(new DesiredCapabilitiesEx()
                            .with(DEVICE_NAME_OPTION, T_DEVICE_1)
                            .with(PLATFORM_NAME, T_PLATFORM_1)
                            .with(PLATFORM_VERSION_OPTION, T_VERSION_1))
                        .build())
                .putAll(SessionName.builder()
                    .className(CLASS_INCLUDED)
                    .methodName(METHOD_WITH_ONE_SESSION).build(),
                    WebDriverSessionInfo.builder()
                        .capabilities(new DesiredCapabilitiesEx()
                            .with(DEVICE_NAME_OPTION, T_DEVICE_1)
                            .with(PLATFORM_NAME, T_PLATFORM_1)
                            .with(PLATFORM_VERSION_OPTION, T_VERSION_1))
                        .build())
                .build())
            .onFinish(mockedTestContext);

        reportModels = reportModelsFrom(mockedTestContext);

        reportModels
            .forEach((key, value) -> log
                .debug("{}->{}", key, GSON.toJson(value)));
    }

    @Test
    public void selfTest() {
        assertThat(ImmutableMap.builder()
            .put(CLASS_INCLUDED, reportModelOf(CLASS_INCLUDED,
                METHOD_WITH_TWO_SESSIONS,
                METHOD_WITH_ONE_SESSION))
            .put(CLASS_EXCLUDED, reportModelOf(CLASS_EXCLUDED,
                METHOD_WITH_TWO_SESSIONS))
            .build(),
            hasEntry(is(CLASS_INCLUDED),
                hasProperty("className", is(CLASS_INCLUDED))));
    }

    @Test
    public void shouldAdornScenarioForMethodWithOneSession() {
        assertThat(reportModels.get(CLASS_INCLUDED).getScenarios().get(1),
            both(hasProperty("testMethodName", is(METHOD_WITH_ONE_SESSION)))
                .and(hasProperty("tagIds",
                    hasItems(
                        tagMapKeyOf(DEVICE_NAME_TAG, T_DEVICE_1),
                        tagMapKeyOf(PLATFORM_NAME_TAG, T_PLATFORM_1),
                        tagMapKeyOf(PLATFORM_VERSION_TAG, T_VERSION_1)))));
    }

    @Test
    public void shouldAdornScenarioForMethodWithTwoSessions() {
        assertThat(reportModels.get(CLASS_INCLUDED).getScenarios().get(0),
            both(hasProperty("testMethodName", is(METHOD_WITH_TWO_SESSIONS)))
                .and(hasProperty("tagIds",
                    hasItems(
                        tagMapKeyOf(DEVICE_NAME_TAG, T_DEVICE_2),
                        tagMapKeyOf(PLATFORM_NAME_TAG, T_PLATFORM_2),
                        tagMapKeyOf(PLATFORM_VERSION_TAG, T_VERSION_2),
                        tagMapKeyOf(DEVICE_NAME_TAG, T_DEVICE_1),
                        tagMapKeyOf(PLATFORM_NAME_TAG, T_PLATFORM_1),
                        tagMapKeyOf(PLATFORM_VERSION_TAG, T_VERSION_1)))));
    }

    @Test
    public void shouldAdornTagMap() {
        assertThat(reportModels.get(CLASS_INCLUDED).getTagMap(),
            both(hasEntry(is(tagMapKeyOf(DEVICE_NAME_TAG, T_DEVICE_1)),
                hasProperty("fullType", is(DEVICE_NAME_TAG))))
                .and(hasEntry(
                    is(tagMapKeyOf(PLATFORM_NAME_TAG, T_PLATFORM_1)),
                    hasProperty("fullType",
                        is(PLATFORM_NAME_TAG))))
                .and(hasEntry(
                    is(tagMapKeyOf(PLATFORM_VERSION_TAG, T_VERSION_1)),
                    hasProperty("fullType",
                        is(PLATFORM_VERSION_TAG))))
                .and(hasEntry(
                    is(tagMapKeyOf(DEVICE_NAME_TAG, T_DEVICE_2)),
                    hasProperty("fullType",
                        is(DEVICE_NAME_TAG))))
                .and(hasEntry(
                    is(tagMapKeyOf(PLATFORM_NAME_TAG, T_PLATFORM_2)),
                    hasProperty("fullType",
                        is(PLATFORM_NAME_TAG))))
                .and(hasEntry(
                    is(tagMapKeyOf(PLATFORM_VERSION_TAG, T_VERSION_2)),
                    hasProperty("fullType",
                        is(PLATFORM_VERSION_TAG)))));
    }

    @Test
    public void shouldNotAdornExcludedClass() {
        assertThat(reportModels.get(CLASS_EXCLUDED).getTagMap().keySet(),
            hasSize(0));
    }

    @Test
    public void shouldNotAdornScenarioForMethodWithoutSessions() {
        assertThat(reportModels.get(CLASS_INCLUDED).getScenarios().get(2),
            both(hasProperty("testMethodName", is(METHOD_WITHOUT_SESSIONS)))
                .and(hasProperty("tagIds", hasSize(0))));
    }
}
