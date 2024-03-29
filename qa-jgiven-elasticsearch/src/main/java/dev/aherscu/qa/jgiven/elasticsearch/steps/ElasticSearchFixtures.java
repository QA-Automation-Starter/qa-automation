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

import java.util.function.*;

import com.tngtech.jgiven.annotation.*;

import co.elastic.clients.elasticsearch.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.elasticsearch.model.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ElasticSearchFixtures<T, TDocument, SELF extends ElasticSearchFixtures<T, TDocument, SELF>>
    extends GenericFixtures<ElasticSearchScenarioType<TDocument>, SELF> {

    @ProvidedScenarioState
    protected final ThreadLocal<String>                 index        =
        new ThreadLocal<>();
    @ProvidedScenarioState
    protected final ThreadLocal<Class<TDocument>>       documentType =
        new ThreadLocal<>();
    @ProvidedScenarioState
    protected final ThreadLocal<Function<TDocument, T>> convertBy    =
        ThreadLocal.withInitial(() -> tDocument -> (T) tDocument);

    // see
    // https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/object-lifecycles.html
    @ProvidedScenarioState
    protected ElasticsearchClient                       elasticsearchClient;

    @Hidden
    public SELF convertingBy(final Function<TDocument, T> convertBy) {
        log.debug("setting custom conversion function");
        this.convertBy.set(convertBy);
        return self();
    }

    public SELF elastic_search(
        @Hidden final ElasticsearchClient elasticsearchClient) {
        log.debug("setting client {}", elasticsearchClient);
        this.elasticsearchClient = elasticsearchClient;
        return self();
    }

    public SELF indexed_by(final String index) {
        log.debug("setting index {}", index);
        this.index.set(index);
        return self();
    }

    public SELF storing(final Class<TDocument> documentType) {
        log.debug("setting document type {}", documentType);
        this.documentType.set(documentType);
        return self();
    }
}
