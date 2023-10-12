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
package dev.aherscu.qa.jgiven.rest.formatters;

import java.lang.annotation.*;

import javax.annotation.concurrent.*;
import javax.ws.rs.core.*;

import org.openqa.selenium.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

/**
 * Annotation formatter for {@link Form} objects.
 *
 * @author aherscu
 *
 */
@ThreadSafe
public class FormFormatter implements ArgumentFormatter<Form> {

    @Override
    public String format(
        final Form argumentToFormat,
        final String... notUsed) {
        return argumentToFormat.asMap().toString();
    }

    /**
     * Formatter annotation for {@link By} objects.
     *
     * @author aherscu
     *
     */
    @Format(value = FormFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        // no parameter to declare
    }
}
