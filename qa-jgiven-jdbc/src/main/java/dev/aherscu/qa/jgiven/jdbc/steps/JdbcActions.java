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

import java.util.*;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.jdbc.model.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JdbcActions<SELF extends JdbcActions<SELF>>
    extends GenericActions<JdbcScenarioType, SELF> {
    @ProvidedScenarioState
    public final ThreadLocal<Integer>        result    = new ThreadLocal<>();
    @ProvidedScenarioState
    public final ThreadLocal<List<Object[]>> resultSet = new ThreadLocal<>();
    @ExpectedScenarioState
    public ThreadLocal<QueryRunner>          queryRunner;

    @SneakyThrows
    public SELF executing(final String sql, final Object... params) {
        log.debug("executing {}", sql);
        result.set(queryRunner.get().execute(sql, params));
        return self();
    }

    @SneakyThrows
    public SELF querying(final String sql, final Object... params) {
        log.debug("querying {}", sql);
        resultSet
            .set(queryRunner.get().query(sql, new ArrayListHandler(), params));
        return self();
    }
}
