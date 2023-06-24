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

import java.util.*;

import edu.umd.cs.findbugs.annotations.*;

@SuppressFBWarnings(
    value = "UWF_UNWRITTEN_FIELD",
    justification = "fields are filled in by the Maven plugin framework")
public class Bind {

    /**
     * List of expressions matching files to include. Could be regular
     * expressions using the expression %regex[].
     */
    String pattern;

    /**
     * The id of the metadata which will be applied to all the files matching
     * the pattern parameter.
     * 
     * @parameter
     * @required
     */
    String metadataId;

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Bind))
            return false;
        final Bind bind = (Bind) o;
        return Objects.equals(pattern, bind.pattern)
            && Objects.equals(metadataId, bind.metadataId);
    }

    public String getMetadataId() {
        return metadataId;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (pattern == null ? 0 : pattern.hashCode());
        result =
            prime * result + (metadataId == null ? 0 : metadataId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "bind ["
            + " pattern=" + pattern
            + " metadataId=" + metadataId
            + " ]";
    }
}
