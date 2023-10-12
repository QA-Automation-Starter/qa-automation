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

import static java.util.Objects.*;

import lombok.extern.slf4j.*;

@Slf4j
public class AutoReturnable<T> implements AutoCloseable {
    public final T              $;
    AutoReturnableObjectPool<T> pool;

    AutoReturnable(final T object) {
        this.$ = object;
    }

    /**
     * Returns the object to its pool.
     *
     * @throws Exception
     *             pooled object is missing reference to originating pool;
     *             should not happen if borrowed via
     *             {@link AutoReturnableObjectPool#borrowObject()}
     */
    @Override
    public final void close() throws Exception {
        if (isNull(pool))
            throw new Exception(
                "pooled object must be retrieved by borrowing from pool");

        log.trace("returning object {}", this.$);
        pool.returnObject(this);
    }
}
