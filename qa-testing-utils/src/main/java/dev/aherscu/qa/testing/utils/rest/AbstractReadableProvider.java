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
package dev.aherscu.qa.testing.utils.rest;

import java.lang.annotation.*;
import java.lang.reflect.*;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * Provides common implementation.
 *
 * @author aherscu
 *
 * @param <T>
 *            the concrete type of provider
 */
public abstract class AbstractReadableProvider<T> implements
    MessageBodyReader<T> {

    @Override
    public final boolean isReadable(final Class<?> type,
        final Type genericType,
        final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }
}
