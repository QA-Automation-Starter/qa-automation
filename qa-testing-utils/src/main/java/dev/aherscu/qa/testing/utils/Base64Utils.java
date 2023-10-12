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

package dev.aherscu.qa.testing.utils;

import java.nio.*;
import java.util.*;

import lombok.experimental.*;

/**
 * Base64 utilities.
 *
 * @author Adrian Herscu
 *
 */
@UtilityClass
public class Base64Utils {

    /**
     * Encodes a specified UUID in short textual format compatible with URI
     * spec.
     *
     * @param uuid
     *            a UUID (16 bytes)
     * @return a 22 character Base64-encoded string of given UUID
     *
     * @throws NullPointerException
     *             if the UUID is null
     */
    public static String encode(final UUID uuid) {
        // noinspection MagicNumber
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                ByteBuffer.allocate(16)
                    .putLong(uuid.getMostSignificantBits())
                    .putLong(uuid.getLeastSignificantBits())
                    .array());
    }

    /**
     * Encodes a specified long in short textual format compatible with URI
     * spec.
     *
     * @param l
     *            a long (8-bytes)
     * @return a 11 character Base64-encoded string of given long
     */
    public static String encode(final long l) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                ByteBuffer.allocate(8)
                    .putLong(l)
                    .array());
    }
}
