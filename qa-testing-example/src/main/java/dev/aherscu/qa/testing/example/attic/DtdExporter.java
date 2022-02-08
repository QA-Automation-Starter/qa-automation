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
package dev.aherscu.qa.testing.example.attic;

import java.io.*;
import java.sql.*;

import org.dbunit.*;
import org.dbunit.database.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

/**
 * Exports database schema in DTD format suitable for DBUnit.
 */
@SuppressFBWarnings(value = "DMI_CONSTANT_DB_PASSWORD",
    justification = "this is an utility to export database schema")
public class DtdExporter {

    /**
     * Opens MySQL connection to a local or tunneled database and generates the
     * DTD into {@code app.dtd}.
     * 
     * @param args
     *            not used
     */
    @SneakyThrows({ FileNotFoundException.class, ClassNotFoundException.class,
        DataSetException.class, IOException.class, DatabaseUnitException.class,
        SQLException.class })
    public static void main(final String[] args) {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (val dtdFileOutputStream = new FileOutputStream("app.dtd")) {
            FlatDtdDataSet.write(
                new DatabaseConnection(
                    DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app",
                        "app_name",
                        "app_password"))
                            .createDataSet(),
                dtdFileOutputStream);
        }
    }
}
