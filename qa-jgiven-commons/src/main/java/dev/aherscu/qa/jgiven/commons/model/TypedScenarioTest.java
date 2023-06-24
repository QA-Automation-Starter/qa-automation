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
package dev.aherscu.qa.jgiven.commons.model;

import java.time.*;

import javax.annotation.concurrent.*;

import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.testing.utils.*;

/**
 * Strongly typed scenario. Ensures that all stages are of same type.
 *
 * @author aherscu
 *
 * @param <GIVEN>
 *            the fixtures stage
 * @param <WHEN>
 *            the actions stage
 * @param <THEN>
 *            the verifications stage
 * @param <T>
 *            type of scenario to be enforced for all stages
 */
@ThreadSafe
public class TypedScenarioTest<T extends AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN extends GenericActions<T, ?> & ScenarioType<T>, THEN extends GenericVerifications<T, ?> & ScenarioType<T>>
    extends ScenarioTestEx<GIVEN, WHEN, THEN> {

    /**
     * Base64-encoded epoch-milliseconds of this run using
     * {@link Instant#toEpochMilli()}.
     */
    public static final String EPOCH_MILLI_64 =
        Base64Utils.encode(Instant.now().toEpochMilli());
}
