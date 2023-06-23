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

package dev.aherscu.qa.s3.publisher.maven.plugin.util;

import java.io.*;
import java.util.*;

import dev.aherscu.qa.s3.publisher.maven.plugin.config.*;

public class ManagedFileContentEncoderPlainImpl
    implements ManagedFileContentEncoder {

    private static final String CONTENT_ENCODING_PLAIN = "plain";

    private final List<String>  supportedContentEncodings;

    public ManagedFileContentEncoderPlainImpl() {
        supportedContentEncodings = new ArrayList<>();
        supportedContentEncodings.add(CONTENT_ENCODING_PLAIN);
    }

    @Override
    public File encode(final ManagedFile managedFile) {
        return new File(managedFile.getFilename());
    }

    @Override
    public boolean isContentEncodingSupported(final String contentEncoding) {
        return supportedContentEncodings
            .contains(contentEncoding.toLowerCase(Locale.ENGLISH));
    }
}
