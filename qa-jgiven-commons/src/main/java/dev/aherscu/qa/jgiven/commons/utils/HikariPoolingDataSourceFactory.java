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
package dev.aherscu.qa.jgiven.commons.utils;

import java.util.concurrent.*;

import javax.sql.*;

import org.unitils.database.*;
import org.unitils.database.config.*;

import com.zaxxer.hikari.*;

import lombok.extern.slf4j.*;

/**
 * Implementation of {@link DataSourceFactory} to replace the default connection
 * pooling used by
 * <a href="http://unitils.org/tutorial-database.html">Unitils</a>'
 * {@link DatabaseModule}.
 *
 * <p>
 * This implementation uses
 * <a href="http://brettwooldridge.github.io/HikariCP/">HikariCP</a> as its
 * connection pooling library.
 * </p>
 *
 * <p>
 * Adapted from {@link PropertiesDataSourceFactory}.
 * </p>
 *
 * @author aherscu
 */
@SuppressWarnings({ "MagicNumber", "SameReturnValue" })
@Slf4j
public class HikariPoolingDataSourceFactory
    extends AbstractConfigurableDataSourceFactory {

    @Override
    public final DataSource createDataSource() {
        log.trace(
            "configuring data source driver {} with URL {}, " //$NON-NLS-1$
                + "username {} and password {}", //$NON-NLS-1$
            config.getDriverClassName(),
            config.getUrl(),
            config.getUserName(),
            config.getPassword());
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUsername(config.getUserName());
        dataSource.setPassword(config.getPassword());
        dataSource.setJdbcUrl(config.getUrl());
        dataSource.setConnectionTimeout(connectionTimeout());
        dataSource.setMaximumPoolSize(maximumPoolSize());
        dataSource.setMaxLifetime(maxLifetime());
        dataSource.setIdleTimeout(idleTimeout());
        return dataSource;
    }

    /**
     * Override to customize the connection timeout.
     *
     * @return the configured connection timeout; by default 30,000 ms
     * @see HikariConfig#setConnectionTimeout(long)
     */
    @SuppressWarnings("static-method")
    protected long connectionTimeout() {
        return 30000;
    }

    /**
     * Override to customize the idle timeout.
     *
     * @return the configured idle timeout; by default 600,000 ms (10 minutes)
     * @see HikariConfig#setIdleTimeout(long)
     */
    @SuppressWarnings("static-method")
    protected long idleTimeout() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    /**
     * Override to customize the maximum life time of connection in the pool.
     *
     * @return the configured maximum life time; defaults to 1,800,000 ms (30
     *         minutes)
     * @see HikariConfig#setMaxLifetime(long)
     */
    @SuppressWarnings("static-method")
    protected long maxLifetime() {
        return TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Override to customize the maximum pool size.
     *
     * @return the configured maximum pool size; defaults to 10 connections
     * @see HikariConfig#setMaximumPoolSize(int)
     */
    @SuppressWarnings("static-method")
    protected int maximumPoolSize() {
        return 10;
    }
}
