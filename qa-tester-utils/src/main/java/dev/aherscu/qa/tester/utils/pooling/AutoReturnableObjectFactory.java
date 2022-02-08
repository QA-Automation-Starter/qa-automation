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

package dev.aherscu.qa.tester.utils.pooling;

import static java.util.Collections.*;

import java.util.*;

import javax.annotation.concurrent.*;

import org.apache.commons.pool2.*;
import org.apache.commons.pool2.impl.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Creates {@link AutoReturnable}s. To be used with
 * {@link AutoReturnableObjectPool}.
 */
@Slf4j
@ThreadSafe
public class AutoReturnableObjectFactory<T>
    extends BasePooledObjectFactory<AutoReturnable<T>> {
    public final List<T> objects;  // unmodifiable

    // read only by create() hence no need to be volatile
    private int          index = 0;

    /**
     * @param objects
     *            fixed list of objects to be pooled
     */
    public AutoReturnableObjectFactory(final List<T> objects) {
        if (0 == objects.size())
            throw new IllegalArgumentException("objects list cannot be empty");

        objects.forEach(object -> log
            .trace("initiating pool with {}->{}",
                object.hashCode(), object));

        this.objects = unmodifiableList(objects);
    }

    /**
     * As long as there are available objects, creates a pooled object.
     * 
     * @return a pooled object
     * @throws Exception
     *             should not happen
     */
    @Override
    public final synchronized AutoReturnable<T> create() throws Exception {
        if (index < objects.size()) {
            val object = new AutoReturnable<>(objects.get(index));
            log.debug("object #{} added to pool {}",
                index, object.$.toString());
            index += 1;
            return object;
        }

        log.error("index {} must be lower than total available objects {}",
            index, objects.size());
        throw new Exception("internal error; should not happen");
    }

    @Override
    public final PooledObject<AutoReturnable<T>> wrap(
        final AutoReturnable<T> obj) {
        return new DefaultPooledObject<>(obj);
    }
}
