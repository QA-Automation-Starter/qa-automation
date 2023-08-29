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

package dev.aherscu.qa.testing.extra.pooling;

import static dev.aherscu.qa.testing.extra.pooling.AutoReturnableObjectPool.*;
import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.pool2.*;
import org.jooq.lambda.*;
import org.testng.annotations.*;

import dev.aherscu.qa.testing.extra.*;
import lombok.*;

// TODO move to separate parallel module

public class AutoReturnableObjectPoolTest {

    private static final int                              MAX_AVAILABLE_OBJECTS =
        100;
    private final List<SomeObject>                        availableObjects;
    private final List<OtherObject>                       otherAvailableObjects;
    private final ObjectPool<AutoReturnable<SomeObject>>  pooledObjects;
    private final ObjectPool<AutoReturnable<OtherObject>> otherPooledObjects;

    {
        availableObjects = new ArrayList<>(MAX_AVAILABLE_OBJECTS);
        for (var i = 0; i < MAX_AVAILABLE_OBJECTS; i++) {
            availableObjects.add(new SomeObject(i));
        }
        pooledObjects = singletonPoolFor(availableObjects);

        otherAvailableObjects = new ArrayList<>(MAX_AVAILABLE_OBJECTS);
        for (var i = 0; i < MAX_AVAILABLE_OBJECTS; i++) {
            otherAvailableObjects.add(new OtherObject(i));
        }
        otherPooledObjects = singletonPoolFor(otherAvailableObjects);
    }

    // NOTE: must run after shouldBorrowFirstObjectAlways,
    // otherwise shouldBorrowFirstObjectAlways will definitely fail
    // because the pool would be not in its initial state
    @Test(dependsOnMethods = "shouldBorrowFirstObjectAlways",
            groups = { "time-sensitive" })
    public void shouldBorrowDifferentObjects() {
        ParallelLoop.PROTOTYPE
            .withThreadPool(new ForkJoinPool(100))
            .withRepetitions(4_000_000)
            .run(Unchecked.intConsumer(id -> {
                try (val object =
                    pooledObjects.borrowObject();
                    val otherObject =
                        otherPooledObjects.borrowObject()) {
                    assertThat(pooledObjects.getNumActive())
                        .isGreaterThanOrEqualTo(0)
                        .isLessThanOrEqualTo(availableObjects.size());
                    assertThat(pooledObjects.getNumIdle())
                        .isGreaterThanOrEqualTo(0)
                        .isLessThanOrEqualTo(availableObjects.size());
                    assertThat(object.$.id)
                        .isGreaterThanOrEqualTo(0)
                        .isLessThan(availableObjects.size());

                    assertThat(otherPooledObjects.getNumActive())
                        .isGreaterThanOrEqualTo(0)
                        .isLessThanOrEqualTo(otherAvailableObjects.size());
                    assertThat(otherPooledObjects.getNumIdle())
                        .isGreaterThanOrEqualTo(0)
                        .isLessThanOrEqualTo(otherAvailableObjects.size());
                    assertThat(otherObject.$.id)
                        .isGreaterThanOrEqualTo(0)
                        .isLessThan(otherAvailableObjects.size());
                }
            }));
    }

    @SneakyThrows
    @Test(groups = { "time-sensitive" })
    public void shouldBorrowFirstObjectAlways() {
        try (val object = pooledObjects.borrowObject()) {
            assertThat(object.$.id).isEqualTo(0);
        }
        try (val object = pooledObjects.borrowObject()) {
            assertThat(object.$.id).isEqualTo(0);
        }
    }

    @AllArgsConstructor
    @ToString
    private static final class OtherObject {
        public final int id;
    }

    @AllArgsConstructor
    @ToString
    private static final class SomeObject {
        public final int id;
    }
}
