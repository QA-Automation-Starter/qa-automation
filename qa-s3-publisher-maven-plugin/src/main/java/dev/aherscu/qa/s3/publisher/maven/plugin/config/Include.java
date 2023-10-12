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
public class Include {

    /**
     * The bind of a pattern with the metadata to apply to all the files
     * matching it.
     */
    Bind       bind;

    /**
     * List of binds specifying constraints in order to override metadata for a
     * subset of files.
     *
     * @parameter
     */
    List<Bind> constraints;

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Include))
            return false;
        final Include include = (Include) o;
        return Objects.equals(bind, include.bind)
            && Objects.equals(constraints, include.constraints);
    }

    public Bind getBind() {
        return bind;
    }

    public List<Bind> getContraints() {
        return constraints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (bind == null ? 0 : bind.hashCode());
        result = prime * result
            + (constraints == null ? 0 : constraints.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "include ["
            + " bind=" + bind
            + " constraints=" + constraints
            + " ]";
    }
}
