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

import static dev.aherscu.qa.tester.utils.FilenameUtilsExtensions.*;
import static dev.aherscu.qa.tester.utils.NumberUtils.*;
import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.nio.file.Files.*;
import static java.util.stream.StreamSupport.*;
import static org.jooq.lambda.Sneaky.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.Timer;
import java.util.stream.*;

import org.testng.*;

import com.codahale.metrics.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * On finish, writes all accumulated metrics to console and to CSV files. The
 * CSV files are written to directory as specified via <tt>target-directory</tt>
 * parameter. If this parameter is not specified, then <tt>target</tt> is
 * assumed.
 * 
 * <p>
 * If <tt>metric-report-interval-ms</tt> parameter is specified, then plots the
 * accumulated metrics to console every so milliseconds.
 * </p>
 * 
 * <p>
 * To be used via <tt>testng.xml</tt>; see <a href=
 * "https://testng.org/doc/documentation-main.html#parameters-testng-xml">
 * TestNG Documentation - Parameters from testng.xml</a> and <a href=
 * "https://testng.org/doc/documentation-main.html#listeners-testng-xml">TestNG
 * Documentation - Specifying listeners with testng.xml</a>
 * </p>
 */
@Slf4j
public class MetricReporterSuiteListener implements ISuiteListener {
    // ISSUE seems that UnitilsScenarioTest derived test classes
    // prevents IHookable#run to be invoked, hence cannot use this method
    // for handling pre/post method execution.
    // UnitilsScenarioTest implements IHookable#run in order to integrate with
    // the Unitils framework; perhaps that implementation is faulty.

    /**
     * Metrics to be reported shall be registered here.
     * <p>
     * Assuming TestNG runs in a single classloader, then this is a singleton.
     * </p>
     */
    public static final MetricRegistry METRIC_REGISTRY    =
        new MetricRegistry();

    private static final String        CSV_HEADER_PATTERN =
        "^[a-z]+[_|a-z|0-9]*(,[a-z]+[_|a-z|0-9]*)*$";

    private final java.util.Timer      timer              =
        new Timer("metrics-reporter");

    private static Stream<Path> csvFilesStreamOf(final ISuite suite)
        throws IOException {
        try (val directories = newDirectoryStream(
            pathFor(suite), "*.csv")) {
            return stream(directories.spliterator(), false);
        }
    }

    private static void mergeCsvFilesOf(final ISuite suite) {
        try (val output =
            new PrintWriter(
                pathFor(suite)
                    .resolve("all-metrics.csv")
                    .toFile(),
                StandardCharsets.UTF_8.toString())) {

            val wasHeaderProcessed = new boolean[] { false };

            csvFilesStreamOf(suite)
                .flatMap(path -> supplier(() -> lines(path)).get()
                    .filter(line -> wasHeaderProcessed[0]
                        ? !line.matches(CSV_HEADER_PATTERN)
                        : (wasHeaderProcessed[0] = true))
                    .map(line -> (line.matches(CSV_HEADER_PATTERN)
                        ? "metric"
                        : getBaseName(path.toFile()))
                        + COMMA + line))
                .forEach(line -> runnable(() -> output.println(line)).run());

        } catch (final IOException e) {
            log.error("failed to merge metrics due to {}", e.getMessage());
        }
    }

    @SneakyThrows(IOException.class)
    private static Path pathFor(final ISuite suite) {
        return createDirectories(Paths.get(
            defaultIfBlank(suite
                .getParameter("target-directory"),
                "target/metrics")));
    }

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "easier to read with small performance impact")
    @Override
    public void onStart(final ISuite suite) {
        val intervalMs = parseLongOrZero(suite
            .getParameter("metric-report-interval-ms"));

        if (0 == intervalMs)
            return;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log.info("temporary metrics for {}", suite.getName());

                ConsoleReporter.forRegistry(METRIC_REGISTRY)
                    .build()
                    .report();
            }
        }, intervalMs, intervalMs);
    }

    @Override
    public void onFinish(final ISuite suite) {
        timer.cancel();

        log.info("metrics after executing {}", suite.getName());

        ConsoleReporter.forRegistry(METRIC_REGISTRY)
            .build()
            .report();

        CsvReporter.forRegistry(METRIC_REGISTRY)
            .build(pathFor(suite).toFile())
            .report();

        mergeCsvFilesOf(suite);
    }
}
