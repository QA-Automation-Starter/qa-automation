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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import static dev.aherscu.qa.orcanos.publisher.maven.plugin.ReportHandle.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import com.google.common.collect.*;

import edu.umd.cs.findbugs.annotations.*;

public class ReportHandleTest {

    static final String EXECUTION_ID   = randomNumeric(6);
    static final String TAG            = randomAlphanumeric(18);
    static final String TEST_ID        = randomNumeric(6);
    static final String TEST_REFERENCE = TEST_ID + UNDERSCORE + EXECUTION_ID;
    static final String TEST_STRING    = random(80);

    @Test
    public void shouldHaveDeviceName() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(DEVICE_NAME, TEST_STRING))
            .build()
            .deviceName(),
            is(TEST_STRING));
    }

    @Test
    public void shouldHaveExecutionSetId() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(REFERENCE, TEST_REFERENCE))
            .build()
            .executionSetId(),
            is(EXECUTION_ID));
    }

    @Test
    public void shouldHavePlatformName() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(PLATFORM_NAME, TEST_STRING))
            .build()
            .platformName(),
            is(TEST_STRING));
    }

    @Test
    public void shouldHavePlatformVersion() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(PLATFORM_VERSION, TEST_STRING))
            .build()
            .platformVersion(),
            is(TEST_STRING));
    }

    @Test
    public void shouldHaveReference() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(REFERENCE, TEST_REFERENCE))
            .build()
            .reference(),
            is(TEST_REFERENCE));
    }

    @Test(dataProvider = "references")
    public void shouldHaveReference(
        final String expectedReference,
        final String referenceString) {
        assertThat(ReportHandle.builder()
            .tag(TAG)
            .attributes(ImmutableMap.of(REFERENCE, referenceString))
            .build()
            .reference(),
            is(expectedReference));
    }

    @Test(dataProvider = "statuses")
    public void shouldHaveStatus(
        final Status expectedStatus,
        final String statusString) {
        assertThat(ReportHandle.builder().status(statusString).build().status(),
            is(expectedStatus));
    }

    @Test
    public void shouldHaveSupportedReference() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(REFERENCE, TEST_REFERENCE))
            .build()
            .hasSupportedReference(),
            is(true));
    }

    @Test
    public void shouldHaveTestId() {
        assertThat(ReportHandle.builder()
            .attributes(ImmutableMap.of(REFERENCE, TEST_REFERENCE))
            .build()
            .testId(),
            is(TEST_ID));
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @DataProvider
    private Object[][] references() {
        return new Object[][] {
            { "", "" },
            { "123_456", "123_456,789_123" },
            { "", "footag:123_456" },
            { "789_123", "footag:123_456,789_123" },
            { "789_123",
                "footag:123_456,123_456," + TAG + TAG_SEPARATOR + "789_123" },
        };
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @DataProvider
    private Object[][] statuses() {
        return new Object[][] {
            { Status.FAIL, Status.FAILED },
            { Status.PASS, Status.SUCCESS },
            { Status.UNSUPPORTED, TEST_STRING },
            { Status.UNSUPPORTED, null },
        };
    }
}
