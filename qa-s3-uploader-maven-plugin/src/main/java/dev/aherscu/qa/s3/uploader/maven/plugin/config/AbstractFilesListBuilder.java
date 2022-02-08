/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.io.*;
import java.util.*;

import org.apache.maven.shared.utils.io.*;

public abstract class AbstractFilesListBuilder {

    protected final File                     inputDirectory;
    protected final List<String>             excludes;
    protected final Map<String, Metadata>    metadataMap;
    protected final Map<String, ManagedFile> fileMap;

    protected AbstractFilesListBuilder(final File inputDirectory,
        final List<String> excludes, final List<Metadata> metadatas) {
        this.inputDirectory = inputDirectory;
        this.excludes = excludes;
        metadataMap = buildMetadataMap(metadatas);
        fileMap = new HashMap<>();
    }

    private static Map<String, Metadata> buildMetadataMap(
        final Collection<Metadata> metadatas) {
        final Map<String, Metadata> map =
            new HashMap<>(metadatas.size());
        for (final Metadata metadata : metadatas) {
            map.put(metadata.getId(), metadata);
        }
        return map;
    }

    private static String convertToString(final Collection<String> list) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        for (final Iterator<?> iterator = list.iterator(); iterator
            .hasNext(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(iterator.next());
        }
        return builder.toString();
    }

    protected List<String> getMatchingFilesNames(final String pattern)
        throws IOException {
        return FileUtils.getFileNames(inputDirectory, pattern,
            convertToString(excludes), true);
    }

    protected Metadata getMetadata(final Bind bind) {
        Metadata metadata = metadataMap.get(bind.getMetadataId());
        if (metadata == null) {
            metadata = getDefaultMetadata();
            if (metadata == null)
                throw new IllegalArgumentException("The metadata with Id "
                    + bind.getMetadataId() + " is undefined.");
        }
        return metadata;
    }

    private Metadata getDefaultMetadata() {
        Metadata retMetadata = null;
        for (final Metadata metadata : metadataMap.values()) {
            if (metadata.isDefault()) {
                retMetadata = metadata;
            }
        }
        return retMetadata;
    }
}
