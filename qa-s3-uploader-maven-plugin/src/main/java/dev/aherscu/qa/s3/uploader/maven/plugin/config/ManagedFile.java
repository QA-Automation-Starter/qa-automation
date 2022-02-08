/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.util.*;

public class ManagedFile {

    private final String   fileName;
    private final Metadata metadata;

    public ManagedFile(final String fileName, final Metadata metadata) {
        this.fileName = fileName;
        this.metadata = metadata;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ManagedFile))
            return false;
        final ManagedFile managedFile = (ManagedFile) o;
        return Objects.equals(fileName, managedFile.fileName)
            && Objects.equals(metadata, managedFile.metadata);
    }

    public String getFilename() {
        return fileName;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + (fileName == null ? 0 : fileName.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "managedFile ["
            + " fileName=" + fileName
            + " metadata=" + metadata
            + " ]";
    }
}
