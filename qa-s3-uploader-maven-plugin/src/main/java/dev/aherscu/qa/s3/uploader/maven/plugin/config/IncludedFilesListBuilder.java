/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.io.*;
import java.util.*;

public class IncludedFilesListBuilder extends AbstractFilesListBuilder {

    private final List<Include>               includes;
    private final ConstrainedFilesListBuilder constrainedFilesListBuilder;

    public IncludedFilesListBuilder(final File inputDirectory,
        final List<Include> includes,
        final List<String> excludes, final List<Metadata> metadatas) {
        super(inputDirectory, excludes, metadatas);
        this.includes = includes;
        constrainedFilesListBuilder = new ConstrainedFilesListBuilder(
            inputDirectory, excludes, metadatas);
    }

    public List<ManagedFile> build() throws IOException {
        for (final Include include : includes) {
            collectIncludedFiles(include);
            replaceConstrainedFiles(include);
        }
        return new ArrayList<>(fileMap.values());
    }

    private void collectIncludedFiles(final Include include)
        throws IOException {
        final List<String> includedFileNames =
            getMatchingFilesNames(include.getBind().getPattern());
        for (final String fileName : includedFileNames) {
            final ManagedFile managedFile =
                new ManagedFile(fileName, getMetadata(include.getBind()));
            fileMap.put(fileName, managedFile);
        }
    }

    private void replaceConstrainedFiles(final Include include)
        throws IOException {
        final List<ManagedFile> constrainedFiles =
            constrainedFilesListBuilder.build(include);
        for (final ManagedFile managedFile : constrainedFiles) {
            fileMap.put(managedFile.getFilename(), managedFile);
        }
    }
}
