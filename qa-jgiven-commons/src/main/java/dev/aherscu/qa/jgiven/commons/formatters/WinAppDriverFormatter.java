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

import static io.appium.java_client.remote.MobileCapabilityType.*;
import static java.util.Objects.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.lang.annotation.*;
import java.text.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

import dev.aherscu.qa.jgiven.commons.utils.*;

/**
 * Annotation formatter for WinAppDriver.
 *
 * @author aherscu
 */
@ThreadSafe
public class WinAppDriverFormatter
    implements
    AnnotationArgumentFormatter<WinAppDriverFormatter.Annotation> {
    @Override
    public String format(final Object argumentToFormat,
        final WinAppDriverFormatter.Annotation annotation) {
        if (isNull(argumentToFormat))
            return EMPTY;
        final org.openqa.selenium.Capabilities capabilities =
            ((WebDriverEx) argumentToFormat).originalCapabilities;
        return MessageFormat.format("{0} {1} {2}",
            capabilities.getCapability(PLATFORM_NAME),
            capabilities.getCapability(PLATFORM_VERSION),
            capabilities.getCapability("app"));
    }

    /**
     * Formatter annotation for WebDriver.
     *
     * @author aherscu
     */
    @AnnotationFormat(WinAppDriverFormatter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        // no parameter to declare
    }
}
