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
package dev.aherscu.qa.jgiven.jdbc;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.util.concurrent.*;

import javax.annotation.concurrent.*;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.dbutils.*;

import com.zaxxer.hikari.*;

import dev.aherscu.qa.testing.utils.config.BaseConfiguration;
import lombok.*;

/**
 * Represents the configuration parameters for tests.
 *
 * @author aherscu
 */
@ThreadSafe
public final class TestConfiguration extends BaseConfiguration {
    private static final ConcurrentMap<String, QueryRunner> queryRunners =
        new ConcurrentHashMap<>();

    static {
        // IMPORTANT: this makes all property values to be parsed as
        // as list if commas are found inside
        AbstractConfiguration
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

    public QueryRunner queryRunnerFor(final String id) {
        return queryRunners.computeIfAbsent(id, _id -> {
            val dataSource = new HikariDataSource();
            dataSource.setDriverClassName(datasourceString(_id, "class"));
            dataSource.setJdbcUrl(datasourceString(_id, "url"));
            // FIXME all these are optional
            // dataSource.setUsername(datasourceString(_id, "username"));
            // dataSource.setPassword(datasourceString(_id, "password"));
            // dataSource.setConnectionTimeout(datasourceInt(_id, "timeout"));
            // dataSource.setMaximumPoolSize(datasourceInt(_id,
            // "pool.maxsize"));
            // dataSource.setMaxLifetime(datasourceInt(_id,
            // "pool.maxlifetime"));
            // dataSource.setIdleTimeout(datasourceInt(_id,
            // "pool.ideltimeout"));
            // FIXME script should execute once nor per connection initialization
            dataSource.setConnectionInitSql(
                stringResourceFrom(datasourceString(_id, "init")));
            return new QueryRunner(dataSource);
        });
    }

    private int datasourceInt(String id, String name) {
        return getInt("datasource" + DOT + id + DOT + name);
    }

    private String datasourceString(String id, String name) {
        return getString("datasource" + DOT + id + DOT + name);
    }

}
