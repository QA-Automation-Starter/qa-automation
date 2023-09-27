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
import co.elastic.clients.elasticsearch.core.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.elasticsearch.model.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ElasticSearchActions<TDocument, SELF extends ElasticSearchActions<TDocument, SELF>>
    extends GenericActions<ElasticSearchScenarioType<TDocument>, SELF> {
    @ProvidedScenarioState
    protected final ThreadLocal<IndexResponse> response = new ThreadLocal<>();
    @ExpectedScenarioState
    protected ThreadLocal<String>              index;
    @ExpectedScenarioState
    protected ThreadLocal<Class<TDocument>>    documentType;
    // see
    // https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/object-lifecycles.html
    @ExpectedScenarioState
    protected ElasticsearchClient              elasticsearchClient;

    @SneakyThrows
    public SELF adding_single_document(
        final TDocument document,
        @Hidden final Function<TDocument, String> indexedBy) {
        log.debug("adding document {} indexed by {}",
            document, indexedBy.apply(document));
        response.set(elasticsearchClient.index(i -> i
            .index(index.get())
            .id(indexedBy.apply(document))
            .document(document)));

        return self();
    }
}
