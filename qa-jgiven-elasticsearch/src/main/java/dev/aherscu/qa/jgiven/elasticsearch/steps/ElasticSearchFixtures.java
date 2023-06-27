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

package dev.aherscu.qa.jgiven.elasticsearch.steps;

import com.tngtech.jgiven.annotation.*;

import co.elastic.clients.elasticsearch.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.elasticsearch.model.*;

public class ElasticSearchFixtures<TDocument, SELF extends ElasticSearchFixtures<TDocument, SELF>>
    extends GenericFixtures<ElasticSearchScenarioType<TDocument>, SELF> {

    @ProvidedScenarioState
    protected final ThreadLocal<String>           index        =
        new ThreadLocal<>();
    @ProvidedScenarioState
    protected final ThreadLocal<Class<TDocument>> documentType =
        new ThreadLocal<>();
    // see
    // https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/object-lifecycles.html
    @ProvidedScenarioState
    protected ElasticsearchClient                 elasticsearchClient;

    public SELF with(final Class<TDocument> documentType) {
        this.documentType.set(documentType);
        return self();
    }

    public SELF an_index(final String index) {
        this.index.set(index);
        return self();
    }

    public SELF an_elasticsearch_instance(
        final ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
        return self();
    }
}
