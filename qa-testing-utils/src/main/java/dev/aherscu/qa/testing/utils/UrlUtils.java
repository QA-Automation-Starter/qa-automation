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

package dev.aherscu.qa.testing.utils;

import java.net.*;

import lombok.*;

/**
 * URL handling utilities.
 *
 * @author Adrian Herscu
 *
 */
public class UrlUtils {

    /**
     * @param url
     *            the URL
     * @return the host within specified URL
     */
    public static String hostOf(final String url) {
        return quietlyCreateUrlFrom(url).getHost();
    }

    /**
     * Checks whether specified URL is well-formed.
     * 
     * @param url
     *            the URL to check
     * @return true if URL is well-formed, namely if {@code new URL(url)} does
     *         not throw {@link MalformedURLException}
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    public static boolean isUrl(final String url) {
        try {
            @SuppressWarnings("unused")
            val parsedUrl = new URL(url);
            return true;
        } catch (@SuppressWarnings("unused") final MalformedURLException e) {
            return false;
        }
    }

    /**
     * Sneakily re-throws the {@link MalformedURLException} thrown by
     * {@link URL#URL(String)}.
     * 
     * @param url
     *            the {@code String} to parse as a URL
     * @return {@link URL}
     */
    @SneakyThrows(MalformedURLException.class)
    public static URL quietlyCreateUrlFrom(final String url) {
        return new URL(url);
    }
}
