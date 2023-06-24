/*
 * Copyright 2023 Adrian Herscu
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

package dev.aherscu.qa.s3.publisher.maven.plugin.config;

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
