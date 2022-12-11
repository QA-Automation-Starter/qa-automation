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
