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
package dev.aherscu.qa.tester.utils.rest;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

import javax.ws.rs.core.*;
import javax.xml.bind.*;

import lombok.extern.slf4j.*;

/**
 * Provides common XML unmarshalling over REST.
 * 
 * @author aherscu
 *
 * @param <T>
 *            the concrete type of provider
 */
@Slf4j
public abstract class AbstractJaxbReadableProvider<T>
    extends AbstractReadableProvider<T> {

    @Override
    public T readFrom(final Class<T> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType,
        final MultivaluedMap<String, String> httpHeaders,
        final InputStream entityStream) {
        log.trace("unmarshalling {} as {}", type, mediaType); //$NON-NLS-1$

        // NOTE: marked as potential resource leak by Eclipse compiler
        return JAXB.unmarshal(entityStream, type);
    }
}
