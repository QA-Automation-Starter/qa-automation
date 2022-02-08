/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.util.*;

import com.amazonaws.services.s3.model.*;

public class ParametersValidator {

    private static final String       CONTENT_ENCODING_GZIP    = "gzip";
    private static final String       CONTENT_ENCODING_PLAIN   = "plain";
    private static final List<String> CONTENT_ENCODING_OPTIONS = Arrays
        .asList(CONTENT_ENCODING_PLAIN, CONTENT_ENCODING_GZIP);

    private final List<Include>       includes;
    private final List<Metadata>      metadatas;

    public ParametersValidator(final List<Include> includes,
        final List<Metadata> metadatas) {
        this.includes = includes;
        this.metadatas = metadatas;
    }

    private static boolean isEmptyMetadataId(final String metadataId) {
        return metadataId == null || "".equals(metadataId);
    }

    private static CannedAccessControlList validateCannedAcl(
        final Metadata metadata) {
        return CannedAccessControlList.valueOf(metadata.getCannedAcl());
    }

    private static void validateContentEncoding(final Metadata metadata) {
        if (!CONTENT_ENCODING_OPTIONS.contains(metadata.getContentEncoding()))
            throw new IllegalStateException("The metadata " + metadata.getId()
                + " has an invalid contentType");
    }

    private static void validateSecondsToExpire(final Metadata metadata) {
        if (metadata.getSecondsToExpire() < 0)
            throw new IllegalStateException("The metadata " + metadata.getId()
                + " has an invalid secondsToExpire");
    }

    public void validate() {
        validateIncludes();
        validateMetadatas();
    }

    private boolean existsDefaultMetadata() {
        boolean existsDefaultMetadata = false;
        for (final Metadata metadata : metadatas) {
            if (metadata.isDefault()) {
                existsDefaultMetadata = true;
                break;
            }
        }
        return existsDefaultMetadata;
    }

    private void validateIncludes() {
        final boolean existsDefaultMetadata = existsDefaultMetadata();
        for (final Include include : includes) {
            if (!existsDefaultMetadata
                && isEmptyMetadataId(include.getBind().getMetadataId()))
                throw new IllegalStateException("The include "
                    + include.getBind().getPattern()
                    + " does not define a metadataId and there isn't a default one");
        }
    }

    private void validateMetadatas() {
        for (final Metadata metadata : metadatas) {
            validateContentEncoding(metadata);
            validateSecondsToExpire(metadata);
            validateCannedAcl(metadata);
        }
    }
}
