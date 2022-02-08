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

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.config.*;

/**
 * Generates links to any reference.
 */
public class ReferenceHrefGenerator extends MvelReferenceGenerator
    implements TagHrefGenerator {

    @Override
    public String generateHref(
        final TagConfiguration tagConfiguration,
        final Annotation annotation,
        final Object value) {
        return generateWith(value);
    }

    @Override
    protected String template() {
        return System.getProperty("reference.href", "about:blank");
    }

}
