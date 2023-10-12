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

import org.apache.commons.beanutils.*;

/**
 * Provides the means to specify a {@link Converter} for the annotated type.
 *
 * <p>
 * EXPERIMENTAL
 * </p>
 *
 * @author aherscu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanUtilsConverter {

    /**
     * @return the {@link Converter} that should be applied at runtime
     */
    Class<? extends Converter> value();

}
