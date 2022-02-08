/*
 * Copyright (c)  2020 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
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
