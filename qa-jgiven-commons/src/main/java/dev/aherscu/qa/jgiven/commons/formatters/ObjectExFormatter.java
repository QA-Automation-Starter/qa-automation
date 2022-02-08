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
package dev.aherscu.qa.jgiven.commons.formatters;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;

import org.apache.commons.lang3.builder.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

/**
 * Formatter for {@link Object}s using ReflectionToStringBuilder.
 * 
 * @author aherscu
 *
 */
@ThreadSafe
public class ObjectExFormatter implements
    AnnotationArgumentFormatter<ObjectExFormatter.Annotation> {

    @Override
    public String format(
        final Object argumentToFormat,
        final ObjectExFormatter.Annotation annotation) {
        return ReflectionToStringBuilder
            .toString(argumentToFormat, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Formatter annotation for {@link Object}s.
     *
     * @author aherscu
     *
     */
    @AnnotationFormat(ObjectExFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        // no fields on purpose
    }
}
