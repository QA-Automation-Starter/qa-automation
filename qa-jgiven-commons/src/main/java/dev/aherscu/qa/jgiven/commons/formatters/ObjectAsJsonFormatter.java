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
package dev.aherscu.qa.jgiven.commons.formatters;

import static dev.aherscu.qa.testing.utils.ObjectMapperUtils.*;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;

import com.fasterxml.jackson.core.*;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

/**
 * Formatter for {@link Object}s using ReflectionToStringBuilder.
 *
 * @author aherscu
 */
@ThreadSafe
public class ObjectAsJsonFormatter implements AnnotationArgumentFormatter<ObjectAsJsonFormatter.Annotation> {

    @Override
    public String format(final Object argumentToFormat, final ObjectAsJsonFormatter.Annotation annotation) {
        try {
            return mapper.writeValueAsString(argumentToFormat);
        } catch (final JsonProcessingException e) {
            return e.toString();
        }
    }

    /**
     * Formatter annotation for {@link Object}s.
     *
     * @author aherscu
     */
    @AnnotationFormat(ObjectAsJsonFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    public @interface Annotation {
        // no fields on purpose
    }
}
