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

package dev.aherscu.qa.testing.rabbitmq.scenarios;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.tester.utils.config.*;
import dev.aherscu.qa.testing.rabbitmq.actions.*;
import dev.aherscu.qa.testing.rabbitmq.fixtures.*;
import dev.aherscu.qa.testing.rabbitmq.model.*;
import dev.aherscu.qa.testing.rabbitmq.verifications.*;

public class RabbitMqTest extends
    UnitilsScenarioTest<BaseConfiguration, RabbitMqScenarioType, RabbitMqFixtures<?>, RabbitMqActions<?>, RabbitMqVerifications<?>> {
    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     *
     * @param configurationType
     *            type of configuration
     */
    protected RabbitMqTest(Class<BaseConfiguration> configurationType) {
        super(configurationType);
    }
}
