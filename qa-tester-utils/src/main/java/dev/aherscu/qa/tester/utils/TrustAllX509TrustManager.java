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

package dev.aherscu.qa.tester.utils;

import java.security.*;
import java.security.cert.*;

import javax.net.ssl.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * A 509 trust manager for debugging. Use to disable SSL certificate
 * verification.
 *
 * @author Adrian Herscu
 *
 */
@Slf4j
public class TrustAllX509TrustManager implements X509TrustManager {

    public static final SSLContext TRUST_ALL_SSL_CONTEXT = trustAllSslContext();

    /**
     * Disables SSL certificate and host validation.
     *
     * <p>
     * Use as follows: <code>
     * static {
     *   TrustAllX509TrustManager.disableSslCertificateValidation();
     * }
     * </code>
     * </p>
     */
    public static void disableSslCertificateValidation() {
        log.debug("turning off SSL certificate validation");
        SSLContext.setDefault(TRUST_ALL_SSL_CONTEXT);
        HttpsURLConnection
            .setDefaultSSLSocketFactory(
                TRUST_ALL_SSL_CONTEXT.getSocketFactory());
        HttpsURLConnection
            .setDefaultHostnameVerifier((string, ssls) -> true);
    }

    @SneakyThrows
    private static SSLContext trustAllSslContext() {
        try {
            val sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,
                new TrustManager[] { new TrustAllX509TrustManager() },
                new SecureRandom());
            return sslContext;
        } catch (final NoSuchAlgorithmException | KeyManagementException e) {
            log.error(
                "could not initialize a trusting all SSL context due to {}",
                e.getMessage());
            throw e;
        }
    }

    @Override
    public void checkClientTrusted(
        final java.security.cert.X509Certificate[] certs,
        final String authType) {
        log.trace("client trusted {} -> {}", authType, certs);
    }

    @Override
    public void checkServerTrusted(
        final java.security.cert.X509Certificate[] certs,
        final String authType) {
        log.trace("server trusted {} -> {}", authType, certs);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        log.trace("accepting all issuers");
        return new X509Certificate[] {};
    }
}
