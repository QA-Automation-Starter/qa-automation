/*
 * Copyright (c)  2021 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.util.*;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
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
