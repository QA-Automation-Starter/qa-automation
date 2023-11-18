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

import java.sql.*;

import org.testng.annotations.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class EmbeddedDbTest {
    @Test
    @SneakyThrows
    public void shouldOpenInsertAndSelect() {
        // not really needed...
        Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver")
            .getDeclaredConstructor()
            .newInstance();
        val conn =
            DriverManager
                .getConnection("jdbc:derby:memory:testing;create=true");
        val stmt = conn.createStatement();
        stmt.execute("create table APP.TEST_TABLE(INTEGER_COLUMN INTEGER)");
        stmt.execute("insert into APP.TEST_TABLE values (1)");
        val rs = stmt.executeQuery("select INTEGER_COLUMN from APP.TEST_TABLE");
        while (rs.next())
            log.debug("INTEGER_COLUMN: {}", rs.getObject("INTEGER_COLUMN"));
    }
}
