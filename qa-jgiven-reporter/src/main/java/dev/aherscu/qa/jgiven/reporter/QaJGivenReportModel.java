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

package dev.aherscu.qa.jgiven.reporter;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.lang.Double.*;
import static java.lang.Integer.toHexString;
import static java.lang.Long.*;
import static java.nio.charset.StandardCharsets.*;
import static java.text.MessageFormat.format;
import static java.time.format.DateTimeFormatter.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.Base64;

import org.apache.commons.codec.binary.*;
import org.apache.commons.io.output.*;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.tester.utils.*;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder
@With
@AllArgsConstructor
@Slf4j
@SuppressWarnings("ClassWithTooManyFields")
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "referenced from JMustache template at runtime")
public class QaJGivenReportModel<T> {
    public final String          screenshotScale;
    public final String          testDocumentId;
    public final String          testDocumentRev;
    public final String          specDocumentId;
    public final String          specDocumentRev;
    public final String          planDocumentId;
    public final String          planDocumentRev;
    public final String          traceabilityDocumentId;
    public final String          traceabilityDocumentRev;
    public final String          productName;
    public final String          productVersion;
    public final T               jgivenReport;

    public final String          datePattern;

    public final Mustache.Lambda shorten            =
        (frag, out) -> out.write(abbreviateMiddle(prettified(frag.execute()),
            ELLIPSIS, 1024));

    public final Mustache.Lambda deleteEOL          =
        (frag, out) -> out.write(normalizeSpace(frag.execute()
            .replaceAll("[\\r\\n]", SPACE)));

    public final Mustache.Lambda nanoToMillis       =
        (frag, out) -> out.write(Long
            .toString(parseLong(frag.execute()) / 1_000_000));

    public final Mustache.Lambda asId               =
        (frag, out) -> out.write(toHexString(frag.execute().hashCode())
            .toUpperCase(Locale.ENGLISH));

    public final Mustache.Lambda simpleName         =
        (frag, out) -> out.write(substringAfterLast(frag.execute(), DOT));

    public final Mustache.Lambda translateIntroWord =
        this::translateIntroWord;

    public final Mustache.Lambda scaleImage         =
        this::scaleImage;

    public final Mustache.Lambda isStepFailed       =
        (frag, out) -> out.write(
            StepStatus.FAILED.equals(((StepModel) frag.context()).getStatus())
                ? frag.execute()
                : EMPTY);

    public final Mustache.Lambda isStepPassed       =
        (frag, out) -> out.write(
            StepStatus.PASSED.equals(((StepModel) frag.context()).getStatus())
                ? frag.execute()
                : EMPTY);

    public final Mustache.Lambda saveImage          =
        this::saveImage;

    private final ZonedDateTime  date               =
        ZonedDateTime.now();

    @SneakyThrows
    public void saveImage(Template.Fragment frag, Writer out) {
        val imageHash = toHexString(frag.execute().hashCode());
        out.write(imageHash);
        try (val pngOutputStream = new FileOutputStream(imageHash + ".png")) {
            ImageUtils.Pipeline
                .from(new ByteArrayInputStream(Base64
                    .getMimeDecoder()
                    .decode(frag.execute().getBytes(UTF_8))))
                .into(pngOutputStream, "png");
        }
    }

    public final String date() {
        return date.format(ofPattern(datePattern));
    }

    @SuppressWarnings("resource")
    public void scaleImage(
        final Template.Fragment frag,
        final Writer out) {
        ImageUtils.Pipeline
            .from(new ByteArrayInputStream(Base64
                .getMimeDecoder()
                .decode(frag.execute().getBytes(UTF_8))))
            .scale(parseDouble(screenshotScale), parseDouble(screenshotScale))
            // NOTE JMustache requires the output stream to be left open
            .into(new Base64OutputStream(
                new WriterOutputStream(out, UTF_8),
                true, -1, null),
                "png");
    }

    @SneakyThrows
    private void translateIntroWord(
        final Template.Fragment frag,
        final Writer out) {

        val introWord = frag.execute().toLowerCase(Locale.US);
        switch (introWord) {
        case "given":
            out.write("Pre-condition(s)");
            break;
        case "when":
            out.write("Operation(s)");
            break;
        case "then":
            out.write("Verification(s)");
            break;
        case "and":
        case "with":
            out.write("and");
            break;
        default:
            log.warn(format("unrecognized introduction word [{0}]",
                introWord));
        }
    }
}
