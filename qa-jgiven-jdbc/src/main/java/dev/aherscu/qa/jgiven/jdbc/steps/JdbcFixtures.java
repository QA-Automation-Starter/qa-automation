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

import org.apache.commons.dbutils.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.jdbc.model.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JdbcFixtures<SELF extends JdbcFixtures<SELF>>
    extends GenericFixtures<JdbcScenarioType, SELF> {
    @ProvidedScenarioState
    public final ThreadLocal<QueryRunner> queryRunner = new ThreadLocal<>();

    public SELF a_query_runner(final QueryRunner queryRunner) {
        log.debug("setting query runner {}",
            queryRunner.getDataSource().toString());
        this.queryRunner.set(queryRunner);
        return self();
    }
}
