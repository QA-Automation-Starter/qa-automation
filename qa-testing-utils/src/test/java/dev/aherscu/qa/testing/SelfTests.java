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

package dev.aherscu.qa.testing;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.*;

import org.testng.annotations.*;

import com.google.common.base.*;

import dev.aherscu.qa.testing.utils.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

@Test
@Slf4j
public class SelfTests {

    public static final int                A_VALUE               = 5;
    private static final long              FIVE_THOUSANDS_MILLIS = 5_000;

    private static final Supplier<Integer> memoized              =
        Suppliers.memoize(SelfTests::someInt);

    private static int fakeMemoizedInt() {
        return Suppliers.memoize(SelfTests::someInt).get();
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @DataProvider(parallel = true)
    private static Iterator<String> parallelDataProvider() {
        return IntStream
            .rangeClosed(1, 10)
            .mapToObj(String::valueOf)
            .iterator();
    }

    private static int someInt() {
        ThreadUtils.sleep(FIVE_THOUSANDS_MILLIS);
        log.debug("===");
        return A_VALUE;
    }

    @BeforeClass
    public void beforeClass() {
        log.debug(">>>before class");
    }

    @BeforeMethod
    public void beforeMethod() {
        log.debug(">>>before method");
    }

    @Test(timeOut = FIVE_THOUSANDS_MILLIS + 100, enabled = false)
    public void shouldFakeMemoize() {
        log.debug(">>>");
        assertThat(fakeMemoizedInt()).isEqualTo(A_VALUE);
        assertThat(fakeMemoizedInt()).isEqualTo(A_VALUE);
    }

    @Test(timeOut = FIVE_THOUSANDS_MILLIS + 100)
    public void shouldMemoize() {
        log.debug(">>>");
        assertThat(memoized.get()).isEqualTo(A_VALUE);
        assertThat(memoized.get()).isEqualTo(A_VALUE);
    }

    @Test(timeOut = FIVE_THOUSANDS_MILLIS + 100, enabled = false)
    public void shouldNotMemoize() {
        log.debug(">>>");
        assertThat(someInt()).isEqualTo(A_VALUE);
        assertThat(someInt()).isEqualTo(A_VALUE);
    }

    @Test(dataProvider = "parallelDataProvider")
    public void shouldRunInParallel(final String value) {
        log.debug(">>>{}", value);
    }
}
