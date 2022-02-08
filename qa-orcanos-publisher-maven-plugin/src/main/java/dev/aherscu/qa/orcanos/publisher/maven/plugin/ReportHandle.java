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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.tuple.*;

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
    static final String               REFERENCE        =
        "dev.aherscu.qa.jgiven.commons.tags.Reference";
    static final String               TAG_SEPARATOR    = COLON;
    private final File                sourceFile;
    private final String              status;
    private final String              tag;
    private final Map<String, String> attributes;

    /**
     * @return the device name from <tt>DeviceName</tt> attribute.
     */
    public String deviceName() {
        return attributes.get(DEVICE_NAME);
    }

    /**
     * @return the execution set identifier from <tt>Reference</tt> attribute.
     */
    public String executionSetId() {
        return substringAfter(reference(), UNDERSCORE);
    }

    /**
     * @return supported <tt>Reference</tt> has an execution set id and a test
     *         case id.
     */
    public boolean hasSupportedReference() {
        return reference().matches("\\d+_\\d+");
    }

    /**
     * @return supported status can be <tt>SUCCESS</tt> or <tt>FAIL</tt>.
     */
    public boolean hasSupportedStatus() {
        return !Status.UNSUPPORTED.equals(status());
    }

    /**
     * @return the platform name from <tt>PlatformName</tt> attribute.
     */
    public String platformName() {
        return attributes.get(PLATFORM_NAME);
    }

    /**
     * @return the platform version from <tt>PlatformVersion</tt> attribute.
     */
    public String platformVersion() {
        return attributes.get(PLATFORM_VERSION);
    }

    public String reference() {
        val references = defaultIfNull(attributes.get(REFERENCE), EMPTY)
            .split(COMMA);

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
     * @return the report source file to upload
     */
    public File sourceFile() {
        return sourceFile;
    }

    /**
     * @return the execution status
     */
    public Status status() {
        return Status.from(status);
    }

    /**
     * @return the test case identifier from <tt>Reference</tt> attribute.
     */
    public String testId() {
        return substringBefore(reference(), UNDERSCORE);
    }

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
