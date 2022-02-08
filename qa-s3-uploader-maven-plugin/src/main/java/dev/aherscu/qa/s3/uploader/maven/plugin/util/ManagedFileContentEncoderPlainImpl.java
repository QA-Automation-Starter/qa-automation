/*
 * Copyright (c)  2020 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.util;

import java.io.*;
import java.util.*;

import dev.aherscu.qa.s3.uploader.maven.plugin.config.*;

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
