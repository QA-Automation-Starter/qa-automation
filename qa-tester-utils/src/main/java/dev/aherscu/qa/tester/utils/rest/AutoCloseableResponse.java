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
package dev.aherscu.qa.tester.utils.rest;

import java.lang.SuppressWarnings;

import javax.ws.rs.core.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

/**
 * Wraps a given {@link Response} providing an {@link AutoCloseable} interface
 * atop.
 *
 * @author aherscu
 */
@Slf4j
@SuppressFBWarnings(
    value = "IMC_IMMATURE_CLASS_IDE_GENERATED_PARAMETER_NAMES",
    justification = "generated by lombok")
@SuppressWarnings("ClassWithTooManyMethods")
public class AutoCloseableResponse extends Response {

    @Delegate(excludes = ExcludedFromDelegation.class)
    private final Response response;

    /**
     * @param response
     *            the response to wrap
     */
    public AutoCloseableResponse(final Response response) {
        this.response = response;
    }

    @Override
    public void close() {
        log.trace("REST response closed."); //$NON-NLS-1$
        response.close();
    }

    interface ExcludedFromDelegation {
        void close();
    }

}