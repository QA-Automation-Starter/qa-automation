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
import java.util.*;

/**
 * An Iterator adapter for a JDBC ResultSet that will convert each row to an
 * object
 *
 * @param <T>
 *            The type to which each row will be converted
 */
class ResultSetIterator<T> implements Iterator<T> {

    /**
     * The ResultSet to iterate over
     */
    private final ResultSet     resultSet;

    /**
     * The handler that converts each row of the ResultSet to type T
     */
    private final RowHandler<T> rowHandler;

    /**
     * Whether or not the result set has more elements. There is a bit of a
     * mismatch between how ResultSets and Iterators work, so we have to set
     * this when the iterator is constructed, then set it each time we advance
     * the ResultSet.
     */
    private boolean             hasNext;

    /**
     * Constructs an iterator over the result set that will use the specified
     * RowHandler to convert each row to an object.
     */
    public ResultSetIterator(ResultSet resultSet, RowHandler<T> rowHandler)
        throws SQLException {
        this.resultSet = resultSet;
        this.rowHandler = rowHandler;
        hasNext = resultSet.next();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        try {
            T result = rowHandler.handleRow(resultSet);
            hasNext = resultSet.next();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
