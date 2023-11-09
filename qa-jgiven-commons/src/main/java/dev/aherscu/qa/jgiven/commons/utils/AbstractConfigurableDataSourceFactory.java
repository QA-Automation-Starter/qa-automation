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
package dev.aherscu.qa.jgiven.commons.utils;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.*;

import org.unitils.core.config.*;
import org.unitils.database.config.*;

/**
 * Data source factory that its configuration can be overridden via system
 * properties.
 *
 * @author aherscu
 *
 */
public abstract class AbstractConfigurableDataSourceFactory
    implements DataSourceFactory {

    /**
     * Configuration to be used by this data source factory.
     */
    protected CustomDatabaseConfiguration config;

    @Override
    public final void init(final DatabaseConfiguration fileConfig) {
        config = new CustomDatabaseConfiguration(fileConfig);
    }

    /**
     * Initialises itself using the properties in the given {@code Properties}
     * object.
     *
     * @param configuration
     *            the configuration, not null
     * @param databaseName
     *            TBD
     */
    @Override
    public final void init(final Properties configuration,
        final String databaseName) {
        config = new CustomDatabaseConfiguration(
            new DatabaseConfigurationsFactory(
                new Configuration(configuration))
                .create()
                .getDatabaseConfiguration(databaseName));
    }

    /**
     * Initialises itself using the properties in the given {@code Properties}
     * object.
     *
     * @param configuration
     *            the configuration, not null
     */
    @Override
    public final void init(final Properties configuration) {
        config = new CustomDatabaseConfiguration(
            new DatabaseConfigurationsFactory(
                new Configuration(configuration))
                .create()
                .getDatabaseConfiguration());
    }

    /**
     * Allows overriding the database configuration loaded from the
     * {@code unitils.properties} file via system properties.
     *
     * @author aherscu
     *
     */
    public static final class CustomDatabaseConfiguration
        extends DatabaseConfiguration {
        /**
         * The system property to use for overriding the database URL.
         */
        public static final String UNITILS_DATABASE_URL =
            "unitils.database.url"; //$NON-NLS-1$

        /**
         * @param config
         *            the read-only configuration
         */
        public CustomDatabaseConfiguration(
            final DatabaseConfiguration config) {
            super(config.getDatabaseName(),
                config.getDialect(),
                config.getDriverClassName(),
                // TODO allow customization of all other configuration
                // properties
                defaultIfBlank(
                    System.getProperty(UNITILS_DATABASE_URL),
                    config.getUrl()),
                config.getUserName(),
                config.getPassword(),
                config.getDefaultSchemaName(),
                config.getSchemaNames(),
                config.isUpdateDisabled(),
                config.isDefaultDatabase());
        }
    }
}
