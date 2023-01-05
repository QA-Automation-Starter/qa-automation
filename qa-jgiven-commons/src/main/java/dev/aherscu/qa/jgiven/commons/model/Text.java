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
package dev.aherscu.qa.jgiven.commons.model;

import static com.google.common.cache.CacheLoader.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.google.common.cache.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Represents a textual value.
 *
 * @author aherscu
 */
@RequiredArgsConstructor
@JsonSerialize(using = Text.Serializer.class)
@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN",
    justification = "hashcode implemented by lombok")
public class Text {

    private static final LoadingCache<String, String> normalizedValues =
        CacheBuilder
            .newBuilder()
            .build(from(value -> normalizeSpace(
                defaultString(value, EMPTY)
                    .toLowerCase(Locale.ENGLISH)
                    .replaceAll("\\p{Punct}", EMPTY))));

    /**
     * The value.
     */
    public final String                               value;

    public static String normalized(final String value) {
        return normalizedValues.getUnchecked(value);
    }

    @Override
    public int hashCode() {
        return normalizedValue().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj
            || obj instanceof Text
                && normalizedValue()
                    .equals(((Text) obj)
                        .normalizedValue());
    }

    @Override
    public final String toString() {
        return value;
    }

    public final String normalizedValue() {
        return normalized(value);
    }

    public static class Serializer<T extends Text>
        extends JsonSerializer<T> {
        @Override
        public void serialize(
            final T text,
            final JsonGenerator gen,
            final SerializerProvider provider)
            throws IOException {
            gen.writeString(text.value);
        }
    }
}
