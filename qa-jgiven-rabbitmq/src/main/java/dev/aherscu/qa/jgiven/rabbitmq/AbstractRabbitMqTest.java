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

package dev.aherscu.qa.jgiven.rabbitmq;

import org.testng.annotations.*;

import com.rabbitmq.client.*;

import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.steps.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;
import lombok.*;

public abstract class AbstractRabbitMqTest<K, V> extends
    UnitilsScenarioTest<TestConfiguration, RabbitMqScenarioType, RabbitMqFixtures<K, V, ?>, RabbitMqActions<K, V, ?>, RabbitMqVerifications<K, V, ?>> {
    protected QueueHandler<K, V> queueHandler;
    protected Connection         connection;

    public AbstractRabbitMqTest(Class<TestConfiguration> configurationType) {
        super(configurationType);
    }

    @BeforeClass
    @SneakyThrows
    protected final void beforeClassOpenConnection() {
        connection = configuration().connectionFactory().newConnection();
    }

    @BeforeMethod
    protected abstract void beforeMethodInitiateQueueHandler();

    @AfterMethod(alwaysRun = true)
    @SneakyThrows
    protected final void afterMethodCloseQueueHandler() {
        queueHandler.close();
    }

    @AfterClass(alwaysRun = true)
    @SneakyThrows
    protected final void afterClassCloseConnection() {
        connection.close();
    }
}
