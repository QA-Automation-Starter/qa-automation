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

package dev.aherscu.qa.jgiven.reporter;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;

import org.testng.xml.*;

import lombok.experimental.*;

@SuperBuilder
public abstract class AbstractQaJgivenReporter<T extends AbstractQaJgivenReporter<?>> {
    public static final String DEFAULT_REFERENCE_TAG    = "Reference";
    public static final String DEFAULT_SCREENSHOT_SCALE = "0.2";
    public static final String DEFAULT_DATE_PATTERN     = "yyyy-MMM-dd HH:mm O";

    protected File             outputDirectory;
    protected File             sourceDirectory;
    protected boolean          debug;
    protected String           screenshotScale;
    protected String           datePattern;
    protected boolean          pdf;
    protected String           referenceTag;
    protected String           templateResource;

    protected AbstractQaJgivenReporter() {
    }

    protected T from(XmlSuite xmlSuite) {
        referenceTag =
            defaultIfBlank(xmlSuite.getParameter("referenceTag"),
                referenceTag);
        screenshotScale =
            defaultIfBlank(xmlSuite.getParameter("screenshotScale"),
                screenshotScale);
        datePattern =
            defaultIfBlank(xmlSuite.getParameter("datePattern"),
                datePattern);
        templateResource =
            defaultIfBlank(xmlSuite.getParameter("templateResource"),
                templateResource);
        return (T) this;
    }
}
