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
package dev.aherscu.qa.jgiven.elasticsearch;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.UriUtils.*;

import java.net.*;

import javax.annotation.concurrent.*;

import org.apache.commons.configuration.*;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.impl.client.*;
import org.elasticsearch.client.*;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.json.jackson.*;
import co.elastic.clients.transport.rest_client.*;
import dev.aherscu.qa.testing.utils.config.BaseConfiguration;
import lombok.*;

/**
 * Represents the configuration parameters for tests.
 *
 * @author aherscu
 */
@ThreadSafe
public final class TestConfiguration extends BaseConfiguration {

    static {
        // IMPORTANT: this makes all property values to be parsed as
        // as list if commas are found inside
        org.apache.commons.configuration.AbstractConfiguration
            .setDefaultListDelimiter(COMMA.charAt(0));
    }

    /**
     * Loads the specified configurations.
     *
     * @param configurations
     *            the additional configurations; might be null or empty
     */
    public TestConfiguration(final Configuration... configurations) {
        super(configurations);
    }

    public ElasticsearchClient elasticSearchClient() {
        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(
                usernameFrom(elasticSearchUrl()),
                passwordFrom(elasticSearchUrl())));

        return new ElasticsearchClient(
            new RestClientTransport(
                RestClient.builder(
                    new HttpHost(
                        elasticSearchUrl().getHost(),
                        elasticSearchUrl().getPort()))
                    .setHttpClientConfigCallback(hc -> hc
                        .setDefaultCredentialsProvider(credentialsProvider))
                    .build(),
                new JacksonJsonpMapper()));
    }

    @SneakyThrows
    public URI elasticSearchUrl() {
        return new URI(getString("elasticsearch.url"));
    }
}
