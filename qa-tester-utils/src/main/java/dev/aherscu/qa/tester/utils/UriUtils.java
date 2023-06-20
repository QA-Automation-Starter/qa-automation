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
package dev.aherscu.qa.tester.utils;

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

import lombok.*;

/**
 * URI handling utilities.
 *
 * @author aherscu
 *
 */
public class UriUtils {
    /**
     * Extracts the last segment of the path of specified URL. For example, for
     * {@code http://somewhere.com/path/to/resource}, {@code resource} would be
     * returned.
     * 
     * @param url
     *            the URL
     * @return the last segment of the specified URL
     */
    public static String lastSegmentOf(final URI url) {
        return new File(url.getPath()).getName();
    }

    /**
     * @param uris
     *            the URIs
     * @return list of strings from specified list of URIs.
     */
    public static List<String> listOfStringsFrom(final List<URI> uris) {
        return uris.stream().map(URI::toString).collect(Collectors.toList());
    }

    /**
     * Extracts the password from specified URL. For example, for
     * {@code http://jdoe:s3cr3t@somewhere.com/path/to/resource}, {@code s3cr3t}
     * would be returned.
     * 
     * @param url
     *            the URL
     * @return the password
     */
    public static String passwordFrom(final URI url) {
        return substringAfter(url.getUserInfo(), COLON);
    }

    /**
     * Extracts a path segment from specified URL.
     * 
     * @param url
     *            the URL
     * @param n
     *            the index of requested path segment, where the first one is 0
     * @return the requested path segment
     */
    public static String pathSegmentFrom(final URI url, final int n) {
        return url.getPath().split(SLASH)[n + 1];
    }

    /**
     * Sneakily re-throws the {@link URISyntaxException} thrown by {@link URI}.
     * 
     * @param url
     *            the {@code String} to parse as a URL
     * @return {@link URL#URL(String)}
     */
    @SneakyThrows(URISyntaxException.class)
    public static URI quietUriFrom(final String url) {
        return new URI(url);
    }

    /**
     * Removes username and password from specified URL.
     * 
     * @param url
     *            the URL
     * @return URL without username and password
     */
    public static URI stripUserInfoFrom(final URI url) {
        return quietUriFrom(
            url.toString().replace(url.getUserInfo() + "@", EMPTY));
    }

    /**
     * Extracts the user-name from specified URL. For example, for
     * {@code http://jdoe:s3cr3t@somewhere.com/path/to/resource}, {@code jdoe}
     * would be returned.
     * 
     * @param url
     *            the URL
     * @return the password
     */
    public static String usernameFrom(final URI url) {
        return substringBefore(url.getUserInfo(), COLON);
    }
}
