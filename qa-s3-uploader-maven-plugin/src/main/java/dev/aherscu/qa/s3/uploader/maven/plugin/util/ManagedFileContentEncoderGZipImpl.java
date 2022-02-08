/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import dev.aherscu.qa.s3.uploader.maven.plugin.config.*;

public class ManagedFileContentEncoderGZipImpl
    implements ManagedFileContentEncoder {

    private static final int    BUFFER_SIZE           = 4096;
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private final File          tmpDirectory;
    private final List<String>  supportedContentEncodings;

    public ManagedFileContentEncoderGZipImpl(final File tmpDirectory) {
        this.tmpDirectory = tmpDirectory;
        supportedContentEncodings = new ArrayList<>();
        supportedContentEncodings.add(CONTENT_ENCODING_GZIP);
        if (!tmpDirectory.exists() && !tmpDirectory.mkdirs())
            throw new IllegalStateException(
                "cannot create directory " + tmpDirectory);
    }

    @Override
    public File encode(final ManagedFile managedFile) throws Exception {
        final File file = new File(managedFile.getFilename());
        File encodedFile;
        FileInputStream fis = null;
        GZIPOutputStream gzipos = null;
        try {
            final byte[] buffer = new byte[BUFFER_SIZE];
            encodedFile =
                File.createTempFile(file.getName() + "-", ".tmp", tmpDirectory);
            fis = new FileInputStream(file);
            gzipos = new GZIPOutputStream(new FileOutputStream(encodedFile));
            int read;
            do {
                read = fis.read(buffer, 0, buffer.length);
                if (read > 0) {
                    gzipos.write(buffer, 0, read);
                }
            } while (read >= 0);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (gzipos != null) {
                gzipos.close();
            }
        }
        return encodedFile;
    }

    @Override
    public boolean isContentEncodingSupported(final String contentEncoding) {
        return supportedContentEncodings
            .contains(contentEncoding.toLowerCase(Locale.ENGLISH));
    }
}
