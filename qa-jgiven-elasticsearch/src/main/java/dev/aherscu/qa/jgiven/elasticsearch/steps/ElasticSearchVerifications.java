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
import java.util.stream.*;

import org.hamcrest.*;
import org.jooq.lambda.*;

import com.tngtech.jgiven.annotation.*;

import co.elastic.clients.elasticsearch.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.util.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.jgiven.elasticsearch.model.*;

public class ElasticSearchVerifications<TDocument, SELF extends ElasticSearchVerifications<TDocument, SELF>>
    extends GenericVerifications<ElasticSearchScenarioType<TDocument>, SELF> {
    @ExpectedScenarioState
    protected ThreadLocal<IndexResponse>    response;
    @ExpectedScenarioState
    protected ThreadLocal<String>           index;
    @ExpectedScenarioState
    protected ThreadLocal<Class<TDocument>> documentType;

    // see
    // https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/object-lifecycles.html
    @ExpectedScenarioState
    protected ElasticsearchClient           elasticsearchClient;

    public SELF the_document(
        final String id,
        final Matcher<TDocument> matcher) {
        return eventually_assert_that(
            Unchecked.supplier(() -> elasticsearchClient.get(g -> g
                .index(index.get())
                .id(id),
                documentType.get())
                .source()),
            matcher);
    }

    public SELF the_index(
        final Query query,
        final Matcher<Stream<TDocument>> matcher) {
        return eventually_assert_that(
            Unchecked.supplier(() -> elasticsearchClient.search(s -> s
                .index(index.get())
                .query(query),
                documentType.get())
                .hits()
                .hits()
                .stream()
                .map(Hit::source)),
            matcher);
    }
}
