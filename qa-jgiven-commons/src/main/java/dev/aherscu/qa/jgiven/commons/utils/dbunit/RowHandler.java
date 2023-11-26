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

/**
 * Converts the current row of a ResultSet to an object
 *
 * @param <T>
 *            The type of object to which the row will be converted
 */
interface RowHandler<T> {

    /**
     * Converts the current row of the ResultSet to an object
     *
     * @param resultSet
     *            The ResultSet to use
     * @return The currente row converted to an object of type T
     */
    T handleRow(ResultSet resultSet) throws SQLException;

}
