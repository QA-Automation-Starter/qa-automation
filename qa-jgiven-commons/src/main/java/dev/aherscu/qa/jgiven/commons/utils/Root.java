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
package dev.aherscu.qa.jgiven.commons.utils;

import java.lang.annotation.*;
import java.util.*;

import com.github.rodionmoiseev.c10n.share.*;

/**
 * Annotation bound to {@link Locale#ROOT} locale.
 *
 * @see <a href=
 *      "https://github.com/rodionmoiseev/c10n/wiki/Overview#storing-translations-in-source-code">
 *      Storing translations in source code</a>
 *
 * @author aherscu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Root {
    String extRes() default Constants.UNDEF;

    String intRes() default Constants.UNDEF;

    boolean raw() default false;

    String value() default Constants.UNDEF;
}
