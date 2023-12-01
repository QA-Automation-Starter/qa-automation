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
// source https://github.com/rhuffman/dbstream
package dev.aherscu.qa.jgiven.jdbc.utils.dbutils;

import java.sql.*;

import org.apache.commons.dbutils.*;

/**
 * ResultSetHandler implementation that converts the ResultSet into a Stream of
 * Object[]s.
 *
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class BeanStreamingHandler<T> extends StreamingResultSetHandler<T> {

    private static final BasicRowProcessor rowProcessor =
        new BasicRowProcessor();

    private final Class<? extends T>       type;

    BeanStreamingHandler(Class<? extends T> type) {
        this.type = type;
    }

    @Override
    protected T handleRow(ResultSet rs) throws SQLException {
        return rowProcessor.toBean(rs, type);
    }
}
