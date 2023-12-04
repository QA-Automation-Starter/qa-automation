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

import static dev.aherscu.qa.jgiven.commons.utils.DryRunAspect.*;
import static dev.aherscu.qa.testing.utils.FileUtilsExtensions.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;

import org.jooq.lambda.*;
import org.testng.*;

import lombok.*;
import lombok.extern.slf4j.*;

/**
 * On finish, writes all identifiers used by this test suite into a file as
 * specified by the {@code identifiers-file} parameter of defining TestNG
 * descriptor. If no such parameter is defined, or is otherwise blank, then
 * {@code identifiers.txt} is assumed.
 */
@Slf4j
public class RandomIdReporter implements ISuiteListener {
    @Override
    public void onStart(final ISuite suite) {
        // nothing to do here
    }

    @Override
    @SneakyThrows
    public void onFinish(final ISuite suite) {
        if (dryRun) {
            log.info("dry run -- skipping");
            return;
        }

        val identifiersFileName =
            defaultIfBlank(
                suite.getParameter("identifiers-file"),
                "identifiers.txt");

        log.info("writing identifiers into {}", identifiersFileName);

        try (val writer = fileWriter(new File(identifiersFileName))) {
            ConfigurableScenarioTest.issuedRandomIds
                .forEach(Unchecked.consumer(
                    writer::write));

            log.debug("wrote identifiers into {}", identifiersFileName);
        }
    }
}
