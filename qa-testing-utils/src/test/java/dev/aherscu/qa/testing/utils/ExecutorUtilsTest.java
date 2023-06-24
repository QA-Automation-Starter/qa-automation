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

package dev.aherscu.qa.testing.utils;

import static java.lang.System.*;
import static java.lang.Thread.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.*;
import java.util.concurrent.*;

import org.testng.annotations.*;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ExecutorUtilsTest {

    @SneakyThrows
    @Test(expectedExceptions = TimeoutException.class,
        timeOut = 1_500, // millis
        invocationCount = 10,
        groups = { "time-sensitive" })
    public void shouldTimeout() {
        val timeout = Duration.ofSeconds(1);
        assertThat(ExecutorUtils.timeout(() -> {
            log.debug("executing");
            val startMillis = currentTimeMillis();
            // noinspection StatementWithEmptyBody
            do {
                // just loop for 2 seconds
            } while (currentTimeMillis() - startMillis < 2_000);
            log.debug("returning exceeding timeout; interrupted {}",
                currentThread().isInterrupted());
            return true;
        }, timeout),
            is(true));
    }
}
