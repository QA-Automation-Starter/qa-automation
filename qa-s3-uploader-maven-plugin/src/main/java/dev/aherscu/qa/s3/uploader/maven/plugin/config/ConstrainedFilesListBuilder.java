/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.io.*;
import java.util.*;

public class ConstrainedFilesListBuilder extends AbstractFilesListBuilder {

    public ConstrainedFilesListBuilder(final File inputDirectory,
        final List<String> excludes, final List<Metadata> metadatas) {
        super(inputDirectory, excludes, metadatas);
    }

    public List<ManagedFile> build(final Include include) throws IOException {
        List<ManagedFile> managedFiles;
        if (include.getContraints() == null) {
            managedFiles = Collections.emptyList();
        } else {
            for (final Bind constraint : include.getContraints()) {
                final List<String> constrainedFileNames =
                    getMatchingFilesNames(constraint.getPattern());
                for (final String fileName : constrainedFileNames) {
                    final ManagedFile managedFile =
                        new ManagedFile(fileName, getMetadata(constraint));
                    fileMap.put(fileName, managedFile);
                }
            }
            managedFiles = new ArrayList<>(fileMap.values());
        }
        return managedFiles;
    }
}
