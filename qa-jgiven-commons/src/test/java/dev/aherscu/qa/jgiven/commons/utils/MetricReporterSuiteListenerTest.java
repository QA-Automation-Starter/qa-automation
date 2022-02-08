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

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.jgiven.commons.utils.MetricReporterSuiteListener.*;
import static org.mockito.Mockito.*;

import org.testng.*;
import org.testng.annotations.*;

import lombok.*;

@Test
public class MetricReporterSuiteListenerTest {

    @BeforeClass
    public void initializeMetricRegistry() {
        try (val c = METRIC_REGISTRY.timer("foo").time()) {
            // do nothing, here just to create a metric
        }
        try (val c = METRIC_REGISTRY.timer("goo").time()) {
            // do nothing, here just to create another metric
        }
        try (val c = METRIC_REGISTRY.timer("boo").time()) {
            // do nothing, here just to create another metric
        }
    }

    public void shouldAggregateMetricsOnFinish() {
        val testSuite = mock(ISuite.class);
        when(testSuite.getParameter("target-directory"))
            .thenReturn("target/metrics");

        new MetricReporterSuiteListener().onFinish(testSuite);

        // TODO: verify that created an aggregated CSV of foo and goo
        // meanwhile verifying manually is good enough and works :)
    }
}
