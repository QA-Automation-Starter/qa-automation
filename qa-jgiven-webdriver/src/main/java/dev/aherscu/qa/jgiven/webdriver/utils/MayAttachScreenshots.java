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

package dev.aherscu.qa.jgiven.webdriver.utils;

import com.tngtech.jgiven.annotation.*;

/**
 * Use to mark a JGiven stage that may attach screenshots.
 * 
 * @param <SELF>
 *            type of JGiven stage
 *
 * @see AttachesScreenshot
 * 
 * @author Adrian Herscu
 *
 */
public interface MayAttachScreenshots<SELF> {
    /**
     * Attaches a screenshot with not delay.
     *
     * @return self reference
     */
    @Hidden
    SELF attaching_screenshot();

    /**
     * Attaches a screenshot after specified delay.
     *
     * @param delayMs
     *            delay in milliseconds
     * @return self reference
     */
    @Hidden
    SELF attaching_screenshot(final int delayMs);
}
