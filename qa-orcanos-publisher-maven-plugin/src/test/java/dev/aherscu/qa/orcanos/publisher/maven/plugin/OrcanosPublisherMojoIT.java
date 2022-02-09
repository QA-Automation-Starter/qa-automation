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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import java.io.*;

import org.apache.maven.plugin.testing.*;

import lombok.*;

/**
 * Simulates a Maven plugin running the "publish" goal against a real Orcanos
 * instance.
 */
public class OrcanosPublisherMojoIT extends AbstractMojoTestCase {
    private static final File PLUGIN_CONFIG =
        new File(getBasedir(), "target/test-classes/plugin-config.xml");

    /**
     * Tests existence of plugin configuration manifest.
     */
    @SuppressWarnings("static-method")
    public void _testConfigurationExistence() {
        assertTrue(PLUGIN_CONFIG.exists());
    }

    @SneakyThrows
    public void testPublishGoal() {
        @SuppressWarnings("CastToConcreteClass")
        val mojo = (OrcanosPublisherMojo) lookupMojo("publish", PLUGIN_CONFIG);

        mojo.execute();

        // TODO: verify Orcanos was updated
    }
}
