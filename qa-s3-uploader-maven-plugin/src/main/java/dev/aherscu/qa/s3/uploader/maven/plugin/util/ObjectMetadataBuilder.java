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

package dev.aherscu.qa.s3.uploader.maven.plugin.util;

import java.io.*;
import java.util.*;

import javax.activation.*;

import com.amazonaws.services.s3.model.*;

import dev.aherscu.qa.s3.uploader.maven.plugin.config.*;

public class ObjectMetadataBuilder {

    private static final MimetypesFileTypeMap mimeMap =
        new MimetypesFileTypeMap();

    private final ManagedFile                 managedFile;
    private final File                        encodedFile;
    private final ObjectMetadata              objectMetadata;
    private Date                              expiresReference;

    public ObjectMetadataBuilder(final ManagedFile managedFile,
        final File encodedFile) {
        this.managedFile = managedFile;
        this.encodedFile = encodedFile;
        objectMetadata = new ObjectMetadata();
    }

    private static boolean isEmptyContentType(final String contentType) {
        return contentType == null || "".equals(contentType);
    }

    public ObjectMetadata buildMetadata() {
        initializeCalendar();
        assignContentLength();
        assignLastModified();
        assignContentEncoding();
        assignContentType();
        assignCacheControl();
        assignContentDisposition();
        assignContentLanguage();
        assignExpires();
        assignWebsiteRedirectLocation();

        return objectMetadata;
    }

    Date getExpiresReference() {
        return expiresReference;
    }

    private void assignCacheControl() {
        if (managedFile.getMetadata().getCacheControl() != null) {
            objectMetadata
                .setCacheControl(managedFile.getMetadata().getCacheControl());
        }
    }

    private void assignContentDisposition() {
        if (managedFile.getMetadata().getContentDisposition() != null) {
            objectMetadata.setContentDisposition(
                managedFile.getMetadata().getContentDisposition());
        }
    }

    private void assignContentEncoding() {
        if (!encodedFile.equals(new File(managedFile.getFilename()))) {
            objectMetadata.setContentEncoding(
                managedFile.getMetadata().getContentEncoding());
        }
    }

    private void assignContentLanguage() {
        if (managedFile.getMetadata().getContentLanguage() != null) {
            objectMetadata.setHeader("Content-Disposition",
                managedFile.getMetadata().getContentLanguage());
        }
    }

    private void assignContentLength() {
        objectMetadata.setContentLength(encodedFile.length());
    }

    private void assignContentType() {
        String contentType;
        if (!isEmptyContentType(managedFile.getMetadata().getContentType())) {
            contentType = managedFile.getMetadata().getContentType();
        } else {
            contentType = mimeMap.getContentType(managedFile.getFilename());
        }
        objectMetadata.setContentType(contentType);
    }

    private void assignExpires() {
        if (managedFile.getMetadata().getSecondsToExpire() != 0) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,
                managedFile.getMetadata().getSecondsToExpire());
            objectMetadata.setHttpExpiresDate(calendar.getTime());
        }
    }

    private void assignLastModified() {
        objectMetadata.setLastModified(new Date(encodedFile.lastModified()));
    }

    private void assignWebsiteRedirectLocation() {
        if (managedFile.getMetadata().getWebsiteRedirectLocation() != null) {
            objectMetadata.setHeader("x-amz-website-redirect-location",
                managedFile.getMetadata().getWebsiteRedirectLocation());
        }
    }

    private void initializeCalendar() {
        final Calendar calendar = Calendar.getInstance();
        expiresReference = calendar.getTime();
    }
}
