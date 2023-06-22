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
import javax.xml.namespace.*;

import lombok.extern.slf4j.*;

/**
 * Provides common XML marshalling over REST.
 * 
 * @author aherscu
 *
 * @param <T>
 *            the concrete type of provider
 */
@Slf4j
public abstract class AbstractJaxbWriteableProvider<T>
    extends AbstractWriteableProvider<T> {
    /**
     * The root XML element name to use when marshalling.
     * 
     * @see #writeTo(Object, Class, Type, Annotation[], MediaType,
     *      MultivaluedMap, OutputStream)
     */
    protected final String elementName;

    /**
     * Initializes this provider to use a specified root XML element name.
     * 
     * @param elementName
     *            to be used as the name of the root XML element
     */
    protected AbstractJaxbWriteableProvider(final String elementName) {
        this.elementName = elementName;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public void writeTo(final T t, final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType,
        final MultivaluedMap<String, Object> httpHeaders,
        final OutputStream entityStream) {
        log.trace("marshalling {} as {}", type, mediaType); //$NON-NLS-1$

        // ISSUE: JAXB tries to use the class name as the element name in
        // serialized XML; we need it to be always "Entity"
        // NOTE: marked as potential resource leak by Eclipse compiler
        JAXB.marshal(
            new JAXBElement<>(
                new QName(elementName), (Class<T>) type, t),
            entityStream);
    }
}
