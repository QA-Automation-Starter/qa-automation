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
package dev.aherscu.qa.testing.utils.rest;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;
import javax.ws.rs.core.Response.Status.*;

import lombok.experimental.*;

/**
 * Provides REST client utility functions.
 *
 * @author aherscu
 *
 */
@UtilityClass
public final class RestClientUtils {

    /**
     * Tests whether the status is successful.
     *
     * @param status
     *            the REST status
     * @return true if the status belongs to {@link Family#SUCCESSFUL}
     */
    public static boolean successful(final Response.StatusType status) {
        return Family.SUCCESSFUL.equals(status.getFamily());
    }

    /**
     * Tests whether the status is unauthorised.
     *
     * @param status
     *            the REST status
     * @return true if the status was {@link Status#UNAUTHORIZED}
     */
    public static boolean unauthorized(final Response.StatusType status) {
        return Status.UNAUTHORIZED.equals(status);
    }
}
