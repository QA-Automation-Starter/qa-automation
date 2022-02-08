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

import static org.apache.commons.lang3.StringUtils.*;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

import dev.aherscu.qa.tester.utils.*;

/**
 * Formatter for {@link Object}s.
 * 
 * @author aherscu
 *
 */
@ThreadSafe
public class ObjectFormatter implements
    AnnotationArgumentFormatter<ObjectFormatter.Annotation> {

    @Override
    public String format(
        final Object argumentToFormat,
        final ObjectFormatter.Annotation annotation) {
        return argumentToFormat.toString()
            + ObjectUtilsExtensions.toString(
                argumentToFormat,
                annotation.separator(),
                annotation.length());
    }

    /**
     * Formatter annotation for {@link Object}s.
     *
     * @author aherscu
     *
     */
    @AnnotationFormat(ObjectFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        /**
         * @return the allowed length of each object string representation
         */
        @SuppressWarnings("MagicNumber")
        int length() default 20;

        /**
         * @return the separator to use between each field
         */
        String separator() default "," + CR + LF;
    }
}
