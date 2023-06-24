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

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;

import org.openqa.selenium.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

/**
 * Annotation formatter for {@link Capabilities} objects.
 * 
 * @author aherscu
 *
 */
@ThreadSafe
public class CapabilitiesFormatter
    implements AnnotationArgumentFormatter<CapabilitiesFormatter.Annotation> {

    @Override
    public String format(
        final Object argumentToFormat,
        final Annotation annotation) {
        return stream(annotation.value())
            .map(((Capabilities) argumentToFormat)::getCapability)
            .map(Object::toString)
            .collect(joining(COLON));
    }

    /**
     * Formatter annotation for {@link Capabilities} objects.
     *
     * @author aherscu
     *
     */
    @AnnotationFormat(CapabilitiesFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        /**
         * @return array of capabilities
         */
        String[] value();
    }
}
