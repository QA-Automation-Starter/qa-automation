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

package dev.aherscu.qa.tester.utils.pooling;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.pool2.impl.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Pool of object to be shared across parallel running tests. As long as a
 * pooled object is borrowed it will not be borrowed to other running test.
 * Borrowed pooled objects shall be returned as soon as possible in order not
 * block other tests running in parallel.
 * 
 * @param <T>
 *            type of object to pool
 */
@Slf4j
public final class AutoReturnableObjectPool<T>
    extends GenericObjectPool<AutoReturnable<T>> {

    private static final ConcurrentHashMap<Class<?>, AutoReturnableObjectPool<?>> POOLS =
        new ConcurrentHashMap<>();

    /**
     * Creates a pool of objects with size of maximum available objects; see
     * {@link AutoReturnableObjectFactory#AutoReturnableObjectFactory(List)}
     *
     * @param factory
     *            the pooled objects factory
     */
    public AutoReturnableObjectPool(
        final AutoReturnableObjectFactory<T> factory) {
        super(factory);
        val total = factory.objects.size();
        log.debug("setting total pooled objects to {} in pool {}",
            total, hashCode());
        setMaxTotal(total);
        setMaxIdle(-1); // otherwise maxTotal is not respected
    }

    /**
     * Initializes an object pool.
     *
     * @param availableObjects
     *            list of available objects for pooling
     * @param <T>
     *            type of objects to be pooled
     * @return pool for specified type of available objects
     *
     * @throws IllegalArgumentException
     *             if list of available objects is empty
     */
    public static <T> AutoReturnableObjectPool<T> poolFor(
        final List<T> availableObjects) {
        if (availableObjects.isEmpty())
            throw new IllegalArgumentException(
                "available objects list must contain at least one item");
        return new AutoReturnableObjectPool<>(
            new AutoReturnableObjectFactory<>(availableObjects));
    }

    /**
     * Initializes a singleton object pool. Available objects will be fixed at
     * first call.
     *
     * @param availableObjects
     *            list of available objects for pooling
     * @param <T>
     *            type of objects to be pooled
     * @return singleton pool for specified type of available objects; further
     *         calls will return same pool with same available object as
     *         provided at first call
     *
     * @throws IllegalArgumentException
     *             if list of available objects is empty
     */
    @SuppressWarnings("unchecked")
    public static <T> AutoReturnableObjectPool<T> singletonPoolFor(
        final List<T> availableObjects) {
        if (availableObjects.isEmpty())
            throw new IllegalArgumentException(
                "available objects list must contain at least one item");

        return (AutoReturnableObjectPool<T>) POOLS
            // NOTE: ConcurrentHashMap#computeIfAbsent is atomic
            // and the creation function below is guaranteed to be called once
            .computeIfAbsent(
                availableObjects.get(0).getClass(),
                (clazz) -> poolFor(availableObjects));
    }

    /**
     * Borrows a pooled object and marks it as pooled by this pool.
     *
     * @return a pooled object
     * @throws Exception
     *             if failed to create new pooled object; see
     *             {@link AutoReturnableObjectFactory#create()}
     */
    @Override
    public AutoReturnable<T> borrowObject() throws Exception {
        val pooledObject = super.borrowObject();
        log.trace("borrowing object {}", pooledObject.$.toString());
        pooledObject.pool = this;
        return pooledObject;
    }
}
