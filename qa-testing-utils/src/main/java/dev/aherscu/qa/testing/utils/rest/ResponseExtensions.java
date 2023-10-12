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

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import lombok.experimental.*;

/**
 * Fixes the JAX RS API a bit...
 *
 * @author aherscu
 *
 */
@UtilityClass
public final class ResponseExtensions {
    /**
     * Closes this response and returns it; enables fluent coding style.
     *
     * <p>
     * Assuming that you have the Lombok library in your classpath, then
     * applying {@code @ExtensionMethod({ ResponseExtensions.class })} on your
     * class will allow you to use
     * {@code target.request().get().closeThis().getCookies()...}, where
     * {@code target} is a {@link WebTarget}.
     *
     * @param thisResponse
     *            this response
     * @return the closed response
     * @see Response#close()
     */
    public static Response closeThis(final Response thisResponse) {
        thisResponse.close();
        return thisResponse;
    }
}
