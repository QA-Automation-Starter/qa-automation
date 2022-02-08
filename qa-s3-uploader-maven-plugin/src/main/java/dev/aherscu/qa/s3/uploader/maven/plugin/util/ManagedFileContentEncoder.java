/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.util;

import java.io.*;

import dev.aherscu.qa.s3.uploader.maven.plugin.config.*;

public interface ManagedFileContentEncoder {
    File encode(ManagedFile managedFile) throws Exception;

    boolean isContentEncodingSupported(String contentEncoding);
}
