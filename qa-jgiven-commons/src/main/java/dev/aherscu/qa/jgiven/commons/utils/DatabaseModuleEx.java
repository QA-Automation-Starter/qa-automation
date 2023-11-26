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

import static org.unitils.util.AnnotationUtils.*;

import java.util.*;

import javax.sql.*;

import lombok.extern.slf4j.*;
import org.unitils.database.*;
import org.unitils.database.annotations.*;

import lombok.*;

@Slf4j
public class DatabaseModuleEx extends DatabaseModule {
    @Override
    public void injectDataSource(final Object testObject) {
        val fields = getFieldsAnnotatedWith(testObject.getClass(),
            TestDataSource.class);
        val methods = getMethodsAnnotatedWith(testObject.getClass(),
            TestDataSource.class);

        if (fields.isEmpty() || methods.isEmpty()) {
            log.info("no datasources needed");
            return;
        }

        val mapDatasources = new HashMap<String, DataSource>();
        // update all databases
        for (Map.Entry<String, DataSourceWrapper> wrapper : wrappers
            .entrySet()) {
            DataSource dataSource2 =
                getDataSource(wrapper.getKey(), mapDatasources, testObject);
            // look if datasource is needed in test.
            setFieldDataSource(wrapper.getKey(), dataSource2, testObject,
                fields, methods);
        }
    }
}
