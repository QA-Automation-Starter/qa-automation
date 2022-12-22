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
