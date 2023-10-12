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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.io.*;
import java.lang.SuppressWarnings;
import java.util.*;

import org.apache.commons.lang3.tuple.*;

import com.google.common.base.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Encapsulates a report file with its status and attributes to be reported to
 * Orcanos.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "value object")
@Builder
@ToString
final class ReportHandle {

    static final String               DEVICE_NAME      = "DeviceName";
    static final String               FAIL             = "FAIL";
    static final String               PASS             = "PASS";
    static final String               PLATFORM_NAME    = "PlatformName";
    static final String               PLATFORM_VERSION = "PlatformVersion";

    // NOTE: this is the defined in qa-jgiven-commons as an annotation;
    // we do not include that dependency here in order to make the build faster.
    static final String               REFERENCE        =
        "dev.aherscu.qa.jgiven.commons.tags.Reference";
    static final String               TAG_SEPARATOR    = COLON;
    private final File                sourceFile;
    private final String              status;
    private final String              tag;
    private final Map<String, String> attributes;

    /**
     * Returns the device name from {@code DeviceName} attribute.
     *
     * @return the device name
     *
     */
    public String deviceName() {
        return attributes.get(DEVICE_NAME);
    }

    /**
     * Returns the execution set identifier from {@code Reference} attribute.
     *
     * @return the id
     *
     */
    public String executionSetId() {
        return substringAfter(reference(), UNDERSCORE);
    }

    /**
     * Returns supported {@code Reference} has an execution set id and a test
     * case id.
     *
     * @return true if has a execution set id
     *
     */
    public boolean hasSupportedReference() {
        return reference().matches("\\d+_\\d+");
    }

    /**
     * Returns supported status can be {@code SUCCESS</tt> or <tt>FAIL}.
     *
     * @return true if supported
     *
     */
    public boolean hasSupportedStatus() {
        return !Status.UNSUPPORTED.equals(status());
    }

    /**
     * Returns the platform name from {@code PlatformName} attribute.
     *
     * @return platform name
     *
     */
    public String platformName() {
        return attributes.get(PLATFORM_NAME);
    }

    /**
     * Returns the platform version from {@code PlatformVersion} attribute.
     *
     * @return platform version
     *
     */
    public String platformVersion() {
        return attributes.get(PLATFORM_VERSION);
    }

    public String reference() {
        Iterable<String> references = Splitter.onPattern(COMMA)
            .split(defaultIfNull(attributes.get(REFERENCE), EMPTY));

        var defaultReference = EMPTY;
        for (val r : references) {
            val taggedReference = r.contains(TAG_SEPARATOR)
                ? Pair.of(
                    substringBefore(r, TAG_SEPARATOR),
                    substringAfter(r, TAG_SEPARATOR))
                : Pair.of(EMPTY, r);
            if (defaultReference.isEmpty()
                && taggedReference.getKey().equals(EMPTY))
                defaultReference = taggedReference.getValue();
            if (taggedReference.getKey().equals(tag))
                return taggedReference.getValue();
        }
        return defaultReference;
    }

    /**
     * Returns the report source file to upload.
     *
     * @return the source file
     *
     */
    public File sourceFile() {
        return sourceFile;
    }

    /**
     * Returns the execution status.
     *
     * @return the status
     *
     */
    public Status status() {
        return Status.from(status);
    }

    /**
     * Returns the test case identifier from {@code Reference} attribute.
     *
     * @return the test id
     */
    public String testId() {
        return substringBefore(reference(), UNDERSCORE);
    }

    @SuppressWarnings("hiding")
    enum Status {
        PASS, FAIL, UNSUPPORTED;

        public static final String FAILED  = "FAILED";
        public static final String SUCCESS = "SUCCESS";

        static Status from(final String value) {
            switch (String.valueOf(value)) {
            case SUCCESS:
                return PASS;
            case FAILED:
                return FAIL;
            default:
                return UNSUPPORTED;
            }
        }
    }
}
