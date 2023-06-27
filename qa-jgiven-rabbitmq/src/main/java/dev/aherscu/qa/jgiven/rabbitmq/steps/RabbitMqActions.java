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

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.rabbitmq.model.*;
import dev.aherscu.qa.jgiven.rabbitmq.utils.*;

public class RabbitMqActions<K, V, SELF extends RabbitMqActions<K, V, SELF>>
    extends GenericActions<RabbitMqScenarioType, SELF> {
    @ExpectedScenarioState
    protected QueueHandler<K, V>  queueHandler;
    @ProvidedScenarioState
    protected Function<V, byte[]> toBytes;

    // ISSUE cannot report stream contents because that will just consume it
    public SELF publishing(@Hidden final Stream<Message<V>> messages) {
        queueHandler.publish(messages);
        return self();
    }

    public SELF publishing(final List<Message<V>> messages) {
        return publishing(messages.stream());
    }

    public SELF consuming() {
        queueHandler.consume();
        return self();
    }
}
