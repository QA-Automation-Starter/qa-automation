/*
 * Copyright 2019 Robert Huffman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// source https://github.com/rhuffman/dbstream
package dev.aherscu.qa.jgiven.commons.utils.dbunit;

import java.sql.*;
import java.util.*;

import org.apache.commons.dbutils.*;

/**
 * ResultSetHandler implementation that converts the ResultSet into a Stream of
 * Map<String, Object>. There is one map for each row in the ResultSet. The keys
 * are the column names (as returned by
 *
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class MapStreamingHandler
    extends StreamingResultSetHandler<Map<String, Object>> {

    private static final BasicRowProcessor rowProcessor =
        new BasicRowProcessor();

    @Override
    protected Map<String, Object> handleRow(ResultSet resultSet)
        throws SQLException {
        return rowProcessor.toMap(resultSet);
    }
}
