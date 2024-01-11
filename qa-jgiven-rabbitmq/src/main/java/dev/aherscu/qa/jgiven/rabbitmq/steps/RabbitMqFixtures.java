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

package dev.aherscu.qa.jgiven.rabbitmq.steps;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;
import lombok.extern.slf4j.*;

@Slf4j
public class RabbitMqFixtures<K, V, SELF extends RabbitMqFixtures<K, V, SELF>>
    extends GenericFixtures<RabbitMqScenarioType, SELF> {

    @ProvidedScenarioState
    protected QueueHandler<K, V> queueHandler;

    public SELF a_queue(@SuppressWarnings("hiding") @POJOFormat(
        includeFields = { "queue",
            "channel" }) final QueueHandler<K, V> queueHandler) {
        log.debug("setting queue handler {}", queueHandler);
        this.queueHandler = queueHandler;
        return self();
    }
}
