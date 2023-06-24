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

import java.time.*;
import java.util.*;

import org.testng.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Writes something to console every few minutes in order to keep Jenkins alive.
 */
@Slf4j
public class JenkinsKeepAlive implements ISuiteListener {

    private final Timer timer = new Timer("jenkins-keep-alive");

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "easier to read with small performance impact")
    @Override
    public void onStart(final ISuite suite) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log.info("...");
            }
        }, 0, Duration.ofMinutes(1).toMillis());
    }

    @Override
    @SneakyThrows
    public void onFinish(final ISuite suite) {
        timer.cancel();
    }
}
