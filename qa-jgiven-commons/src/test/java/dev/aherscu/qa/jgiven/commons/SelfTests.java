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

package dev.aherscu.qa.jgiven.commons;

import static dev.aherscu.qa.tester.utils.ThreadUtils.sleep;
import static java.lang.System.*;
import static java.lang.Thread.*;
import static java.util.concurrent.Executors.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.*;
import java.util.concurrent.*;

import org.testng.annotations.*;

import com.google.common.util.concurrent.*;

import lombok.*;
import lombok.extern.slf4j.*;
import net.jodah.failsafe.*;

@Slf4j
public class SelfTests {
    // IMPORTANT: must use a dynamic thread pool otherwise all threads may
    // be consumed by long running tasks, essentially blocking everything else
    private final static ExecutorService SCHEDULER       =
        newCachedThreadPool();
    private final static int             TIMEOUT_MS      = 1_000;
    private final static int             TEST_TIMEOUT_MS = 1_500;

    @BeforeMethod
    public void beforeMethod() {
        log.debug("before method {}", currentThread().getId());
    }

    @DataProvider(parallel = true)
    public Object[][] data() {
        return new Object[][] { { 1 }, { 2 }, { 3 }, { 4 } };
    }

    @SneakyThrows
    @Test(expectedExceptions = ExecutionException.class)
    public void shouldRepeatedlyTimeout() {
        assertThat(Failsafe
            .with(new RetryPolicy<>()
                .withMaxAttempts(3)
                .onRetry(e -> log.debug("retrying {}", e))
                .onFailedAttempt(e -> log.debug("failed attempt {}", e))
                .onRetriesExceeded(e -> log.debug("retries exceeded {}", e)),
                Timeout
                    .of(Duration.ofSeconds(1))
                    .withCancel(true)
                    .onFailure(e -> log.debug("timed out {}", e)))
            .getAsync(() -> {
                log.debug("executing");
                sleep(2_000);
                log.debug("returning exceeding timeout");
                return 8;
            })
            .get(),
            is(8));
    }

    @SneakyThrows
    @Test(invocationCount = 10, threadPoolSize = 10)
    public void shouldRetry() {
        assertThat(Failsafe
            .with(Fallback.of(e -> {
                log.debug("finally {}", e.getLastFailure().getMessage());
                return true;
            }), new RetryPolicy<>()
                .onRetry(e -> log.debug("retrying {}", e.getAttemptCount())))
            .get(() -> {
                log.debug("attempting...");
                throw new Exception("failed");
            }),
            is(true));
    }

    @Test(dataProvider = "data")
    public void shouldRun(final int data) {
        log.debug("data {}", data);
    }

    @Ignore("does not work")
    @SneakyThrows
    @Test(expectedExceptions = ExecutionException.class,
        timeOut = TEST_TIMEOUT_MS,
        invocationCount = 10)
    public void shouldTimeout() {
        assertThat(Failsafe
            .with(Timeout
                .of(Duration.ofMillis(TIMEOUT_MS))
                .withCancel(true)
                .onFailure(e -> log.debug("timed out {}", e)))
            .with(SCHEDULER)
            .getAsync(() -> {
                log.debug("executing");
                // ISSUE https://github.com/jhalterman/failsafe/issues/271
                // it works with sleep though
                // sleep(timeout.plusSeconds(1).toMillis());
                val startMillis = currentTimeMillis();
                // noinspection StatementWithEmptyBody
                do {
                    // just loop for 2 seconds
                } while (currentTimeMillis() - startMillis < 2_000);
                log.debug("returning exceeding timeout; interrupted {}",
                    currentThread().isInterrupted());
                return true;
            })
            .get(),
            is(true));
    }

    @Ignore("does not work - fails sporadically on slow machines")
    @SneakyThrows
    @Test(expectedExceptions = TimeoutException.class,
        timeOut = TEST_TIMEOUT_MS,
        invocationCount = 10,
        threadPoolSize = 10)
    public void shouldTimeout2() {
        assertThat(SimpleTimeLimiter.create(SCHEDULER)
            .callWithTimeout(() -> {
                log.debug("executing");
                val startMillis = currentTimeMillis();
                // noinspection StatementWithEmptyBody
                do {
                    // just loop for 2 seconds
                } while (currentTimeMillis() - startMillis < 2_000);
                log.debug("returning exceeding timeout; interrupted {}",
                    currentThread().isInterrupted());
                return true;
            }, Duration.ofMillis(TIMEOUT_MS).getSeconds(), TimeUnit.SECONDS),
            is(true));
    }
}
