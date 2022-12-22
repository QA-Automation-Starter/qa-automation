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
import java.text.*;
import java.util.*;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.*;
import org.apache.maven.plugin.logging.*;

import com.amazonaws.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import dev.aherscu.qa.s3.uploader.maven.plugin.config.*;

public class S3Uploader {

    private final AmazonS3Client                  client;
    private final Log                             log;
    private final String                          bucketName;
    private final File                            inputDirectory;
    private final List<ManagedFileContentEncoder> contentEncoders;
    private final boolean                         refreshExpiredObjects;
    private final SimpleDateFormat                httpDateFormat;

    public S3Uploader(final AmazonS3Client client, final Log log,
        final List<ManagedFileContentEncoder> contentEncoders,
        final String bucketName,
        final File inputDirectory, final File tmpDirectory,
        final boolean refreshExpiredObjects) {
        this.client = client;
        this.log = log;
        this.bucketName = bucketName;
        this.inputDirectory = inputDirectory;
        this.contentEncoders = contentEncoders;
        this.refreshExpiredObjects = refreshExpiredObjects;
        httpDateFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static String calculateETag(final File file) throws Exception {
        return Hex.encodeHexString(DigestUtils.md5(new FileInputStream(file)));
    }

    private static boolean isLocalFileSameAsRemote(final File localFile,
        final ObjectMetadata remoteFileMetadata) throws Exception {
        return remoteFileMetadata != null &&
            remoteFileMetadata.getETag().equals(calculateETag(localFile));
    }

    private static boolean isMetadataExpired(
        final ObjectMetadata objectMetadata) {
        // NOTE: quick and dirty fix
        return true;
    }

    private static String transformFileNameSlashesToS3(final String fileName) {
        return fileName.replace('\\', '/');
    }

    public void uploadManagedFile(final ManagedFile managedFile)
        throws Exception {
        final File encodedFile = encodeManagedFile(managedFile);
        final String remoteFileName = getRemoteFileName(managedFile);
        final ObjectMetadata remoteMetadata =
            retrieveObjectMetadata(remoteFileName);
        final ObjectMetadataBuilder objectMetadataBuilder =
            new ObjectMetadataBuilder(managedFile, encodedFile);
        final ObjectMetadata objectMetadata =
            objectMetadataBuilder.buildMetadata();

        if (!isLocalFileSameAsRemote(encodedFile, remoteMetadata)) {
            log.debug("uploading file " + managedFile.getFilename() + " to "
                + bucketName);
            client.putObject(bucketName, remoteFileName,
                new FileInputStream(encodedFile), objectMetadata);
            setObjectAcl(managedFile, remoteFileName);
        } else {
            if (refreshExpiredObjects && isMetadataExpired(remoteMetadata)) {
                log.debug("refreshing metadata for file "
                    + managedFile.getFilename());
                client.copyObject(
                    buildCopyObjectRequest(remoteFileName, objectMetadata));
                setObjectAcl(managedFile, remoteFileName);
            } else {
                log.debug("the object " + remoteFileName + " stored at "
                    + bucketName + " does not require update");
            }
        }
    }

    private CopyObjectRequest buildCopyObjectRequest(
        final String remoteFileName,
        final ObjectMetadata objectMetadata) {
        return new CopyObjectRequest(bucketName, remoteFileName, bucketName,
            remoteFileName)
                .withNewObjectMetadata(objectMetadata);
    }

    private File encodeManagedFile(final ManagedFile managedFile)
        throws Exception {
        File encodedFile = null;
        for (final ManagedFileContentEncoder contentEncoder : contentEncoders) {
            if (contentEncoder.isContentEncodingSupported(
                managedFile.getMetadata().getContentEncoding())) {
                log.debug("contentEncoding file " + managedFile.getFilename());
                encodedFile = contentEncoder.encode(managedFile);
            }
        }
        return encodedFile;
    }

    private String getRemoteFileName(final ManagedFile managedFile) {
        final File file = new File(managedFile.getFilename());
        return transformFileNameSlashesToS3(removeBasePath(file));
    }

    private void logObjectMetadata(final String remoteFileName,
        final ObjectMetadata objectMetadata) {
        log.debug("  ETag: " + objectMetadata.getETag());
        log.debug("  ContentType: " + objectMetadata.getContentType());
        log.debug("  CacheControl: " + objectMetadata.getCacheControl());
        log.debug("  ContentEncoding: " + objectMetadata.getContentEncoding());
        log.debug("  ContentLength: " + objectMetadata.getContentLength());
        log.debug("  Expires: "
            + (objectMetadata.getHttpExpiresDate() == null ? "unknown"
                : httpDateFormat.format(objectMetadata.getHttpExpiresDate())));
        log.debug("  LastModified: " + objectMetadata.getLastModified());
    }

    private String removeBasePath(final File file) {
        return file.getPath().substring(inputDirectory.getPath().length() + 1);
    }

    private ObjectMetadata retrieveObjectMetadata(final String remoteFileName) {
        log.debug("retrieving metadata for " + remoteFileName);
        ObjectMetadata objectMetadata = null;
        try {
            objectMetadata =
                client.getObjectMetadata(bucketName, remoteFileName);
            logObjectMetadata(remoteFileName, objectMetadata);
        } catch (final AmazonServiceException e) {
            log.debug(e);
        }
        return objectMetadata;
    }

    private void setObjectAcl(final ManagedFile managedFile,
        final String remoteFileName) {
        final CannedAccessControlList acl = CannedAccessControlList
            .valueOf(managedFile.getMetadata().getCannedAcl());
        client.setObjectAcl(bucketName, remoteFileName, acl);
    }
}
