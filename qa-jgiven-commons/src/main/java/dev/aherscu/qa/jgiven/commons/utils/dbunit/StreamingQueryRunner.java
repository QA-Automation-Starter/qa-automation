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
import java.util.stream.*;

import javax.sql.*;

import org.apache.commons.dbutils.*;

/**
 * An extension of the Apache DbUtils QueryRunner that adds methods to produce
 * Streams where each element of the stream is constructed from a row in a
 * ResultSet. This takes advantage of database cursors (assuming the underlying
 * JDBC ResultSet does) so the entire query result does not have to be read into
 * memory.
 */
public class StreamingQueryRunner extends QueryRunner {

    public StreamingQueryRunner(DataSource dataSource) {
        super(dataSource);
    }

    private static void closeUnchecked(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a Query and returns a Stream in which each element represents a
     * row in the result. (This method cannot be named "query" because it would
     * hide the corresponding methods in the superclass.)
     *
     * @param sql
     *            The SQL query to execute
     * @param handler
     *            The ResultSetHandler that converts the ResultSet to a Stream
     * @param args
     *            The arguments to pass to the query as prepared statement
     *            parameters
     */
    public <T> Stream<T> queryStream(String sql,
        StreamingResultSetHandler<T> handler, Object... args)
        throws SQLException {
        // We cannot use try-with-resources: if there is no exception the
        // Connection, PreparedStatement,
        // and ResultSet must remain open.
        Connection connection = getDataSource().getConnection();
        try {
            return query(connection, true, sql, handler, args);
        } catch (SQLException | RuntimeException | Error e) {
            closeUnchecked(connection);
            throw e;
        }
    }

    public Stream<Object[]> queryStream(String sql, Object... args)
        throws SQLException {
        return queryStream(sql, new ArrayStreamingHandler(), args);
    }

    /**
     * Executes a Query and returns a Stream in which each element represents a
     * row in the result. (This method cannot be named "query" because it would
     * hide the corresponding methods in the superclass.)
     *
     * @param connection
     *            The database Connection to use
     * @param sql
     *            The SQL query to execute
     * @param handler
     *            The ResultSetHandler that converts the ResultSet to a Stream
     * @param args
     *            The arguments to pass to the query as prepared statement
     *            parameters
     */
    public <T> Stream<T> queryStream(
        Connection connection, String sql, StreamingResultSetHandler<T> handler,
        Object... args)
        throws SQLException {
        return query(connection, false, sql, handler, args);
    }

    /**
     * Executes a Query and returns a Stream in which each element represents a
     * row in the result.
     *
     * @param connection
     *            The database Connection to use
     * @param closeConnection
     *            Whether or not the connection should be closed when the stream
     *            is closed
     * @param sql
     *            The SQL query to execute
     * @param handler
     *            The ResultSetHandler that converts the ResultSet to a Stream
     * @param args
     *            The arguments to pass to the query as prepared statement
     *            parameters
     */
    private <T> Stream<T> query(
        Connection connection,
        boolean closeConnection,
        String sql,
        StreamingResultSetHandler<T> handler,
        Object... args)
        throws SQLException {
        // We cannot use try-with-resources: if there is no exception the
        // PreparedStatement
        // and ResultSet must remain open.
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            fillStatement(statement, args);
            ResultSet resultSet = statement.executeQuery();
            Stream<T> stream = handler.handle(resultSet)
                .onClose(() -> closeUnchecked(resultSet))
                .onClose(() -> closeUnchecked(statement));
            if (closeConnection) {
                return stream.onClose(() -> closeUnchecked(connection));
            }
            return stream;
        } catch (SQLException | RuntimeException | Error e) {
            closeUnchecked(statement);
            throw e;
        }
    }
}
