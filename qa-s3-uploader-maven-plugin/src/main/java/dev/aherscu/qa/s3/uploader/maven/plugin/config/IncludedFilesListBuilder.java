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
