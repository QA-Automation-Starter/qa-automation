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

public class Metadata {

    /**
     * The id of this metadata.
     */
    String  id;
    /**
     * Cache control directive. The format must be compliant with Cache-Control
     * HTTP header.
     */
    String  cacheControl;
    String  contentDisposition;
    /**
     * Content type value. Should be used for those cases where it is not
     * possible to guess it from file extension.
     */
    String  contentType;
    /**
     */
    String  contentLanguage;
    /**
     * Number of seconds to add to time when the file is uploaded to S3 for
     * setting Expires metadata.
     */
    int     secondsToExpire;
    /**
     * Content encoding. Only plain and gzip are supported currentlt.
     */
    String  contentEncoding;
    /**
     */
    String  websiteRedirectLocation;
    /**
     * The S3 permission to apply to uploaded files. Could be any of the
     * following: AuthenticatedRead, BucketOwnerFullControl, BucketOwnerRead,
     * LogDeliveryWrite, Private, PublicRead, PublicReadWrite.
     */
    String  cannedAcl;
    /**
     * Set if this is the default metadata which will be applied to all Binds
     * which don't specify a metadata id.
     */
    boolean def;

    private static String toLowerCase(final String value) {
        if (value != null)
            return value.toLowerCase(Locale.ENGLISH);

        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Metadata))
            return false;
        final Metadata metadata = (Metadata) o;
        return Objects.equals(id, metadata.id)
            && Objects.equals(cacheControl, metadata.cacheControl)
            && Objects.equals(contentDisposition, metadata.contentDisposition)
            && Objects.equals(contentType, metadata.contentType)
            && Objects.equals(contentLanguage, metadata.contentLanguage)
            && secondsToExpire == metadata.secondsToExpire
            && Objects.equals(contentEncoding, metadata.contentEncoding)
            && Objects
                .equals(websiteRedirectLocation,
                    metadata.websiteRedirectLocation)
            && Objects.equals(cannedAcl, metadata.cannedAcl)
            && def == metadata.def;
    }

    public String getCacheControl() {
        return toLowerCase(cacheControl);
    }

    public String getCannedAcl() {
        return cannedAcl;
    }

    public String getContentDisposition() {
        return toLowerCase(contentDisposition);
    }

    public String getContentEncoding() {
        return toLowerCase(contentEncoding);
    }

    public String getContentLanguage() {
        return toLowerCase(contentLanguage);
    }

    public String getContentType() {
        return toLowerCase(contentType);
    }

    public String getId() {
        return id;
    }

    public int getSecondsToExpire() {
        return secondsToExpire;
    }

    public String getWebsiteRedirectLocation() {
        return toLowerCase(websiteRedirectLocation);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    public boolean isDefault() {
        return def;
    }

    @Override
    public String toString() {
        return "metadada ["
            + " id=" + id
            + " cacheControl=" + cacheControl
            + " contentDisposition=" + contentDisposition
            + " contentType=" + contentType
            + " contentLanguage=" + contentLanguage
            + " secondsToExpire=" + secondsToExpire
            + " contentEncoding=" + contentEncoding
            + " websiteRedirectLocation=" + websiteRedirectLocation
            + " cannedAcl=" + cannedAcl
            + " ]";
    }
}
