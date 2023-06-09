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

package dev.aherscu.qa.jgiven.rabbitmq.utils;

import static dev.aherscu.qa.jgiven.rabbitmq.utils.QueueHandler.*;

import java.time.*;

import com.rabbitmq.client.*;

import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

@Slf4j
public class AbstractQueueHandlerTest {
    public static final ConnectionFactory LOCAL_RABBITMQ =
        connectionFactoryFrom("amqp://guest:guest@localhost");

    protected final static RetryPolicy<?> retryPolicy    =
        new RetryPolicy<>()
            .withMaxRetries(-1)
            .withDelay(Duration.ofSeconds(2))
            .withMaxDuration(Duration.ofSeconds(20))
            .onRetry(
                e -> log.trace("retrying due to {}", e.toString()))
            .onRetriesExceeded(
                e -> log.trace("retries exceeded for {}", e.toString()))
            .handleIf(e -> e instanceof AssertionError
                || e instanceof NullPointerException);

}
