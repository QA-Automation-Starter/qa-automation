/*
 * Copyright (c)  2020 To-Be-Defined. All rights reserved.
 * Confidential and Proprietary.
 */

package dev.aherscu.qa.s3.uploader.maven.plugin.config;

import java.util.*;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
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
