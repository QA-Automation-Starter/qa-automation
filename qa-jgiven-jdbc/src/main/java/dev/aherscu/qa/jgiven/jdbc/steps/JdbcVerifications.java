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

package dev.aherscu.qa.jgiven.jdbc.steps;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.*;

import org.hamcrest.*;
import org.jooq.lambda.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.jdbc.model.*;
import dev.aherscu.qa.jgiven.jdbc.utils.dbutils.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JdbcVerifications<SELF extends JdbcVerifications<SELF>>
    extends GenericVerifications<JdbcScenarioType, SELF> {

    @ExpectedScenarioState
    public ThreadLocal<StreamingQueryRunner> queryRunner;

    public SELF the_query(
        final String sql,
        final Matcher<Stream<Object[]>> expected,
        final Object... params) {
        return eventually(Unchecked.function(self -> {
            try (val results = queryRunner.get()
                .queryStream(sql, new ArrayStreamingHandler(), params)
                .peek(row -> log.trace("row[0]: {}", row[0]))) {
                assertThat(results, expected);
                return self;
            }
        }));
    }
}
