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
package dev.aherscu.qa.jgiven.commons.tags;

import java.lang.annotation.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.utils.*;

/**
 * Reference information.
 *
 * @author sgershman
 *
 */
@IsTag(prependType = true,
    name = "Reference",
    style = "background-color: darkgreen; color: white; font-weight: bold",
    descriptionGenerator = ReferenceTagDescriptionGenerator.class,
    hrefGenerator = ReferenceHrefGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {
    /**
     * @return list of Reference identifiers (numeric part only)
     */
    String[] value();
}
