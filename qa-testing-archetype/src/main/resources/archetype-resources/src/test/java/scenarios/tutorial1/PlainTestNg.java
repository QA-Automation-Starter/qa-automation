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

package ${package}.scenarios.tutorial1;

import static java.lang.Math.*;

import org.testng.annotations.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class PlainTestNg {
    @Builder // lombok generates a builder
    @ToString // lombok generates a nice toString method
    static class FAT { // Full-Adder Truth
        final boolean a, b, sum, carry;
    }

    @Test
    public void _1_shouldSucceed() {
        assert true; // "assert" requires -ea vm flag
    }

    @Test(dataProvider = "_2_notTruthTable")
    public void _2_checkNot(
        final boolean argument,
        final boolean result) {
        log.debug("checking {} is not {}", argument, result);
        assert argument != result;
    }

    @Test(dataProvider = "_3_fullAdderTruthTable")
    public void _3_checkFullAdder(final FAT v) {
        assert ((v.a || v.b) == v.sum)
            && ((v.a && v.b) == v.carry);
    }

    @Test(dataProvider = "_4_sinusTruthTable")
    public void _4_checkSinus(final double angle, final double sinus) {
        assert sin(angle) == sinus;
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @DataProvider(parallel = false) // parallel is not always good magic
    private Object[][] _2_notTruthTable() {
        return new Object[][] {
            { false, true },
            { true, false }
        };
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @DataProvider
    private Object[][] _3_fullAdderTruthTable() {
        return new Object[][] {
            // @formatter:off
            {FAT.builder().a(false).b(false).sum(false).carry(false).build()},
            {FAT.builder().a(false).b(true) .sum(true) .carry(false).build()},
            {FAT.builder().a(true) .b(false).sum(true) .carry(false).build()},
            {FAT.builder().a(true) .b(true) .sum(true) .carry(true) .build()},
            // @formatter:on
        };
    }

    @SuppressFBWarnings(
        value = "UPM_UNCALLED_PRIVATE_METHOD",
        justification = "called by testng framework")
    @DataProvider
    private Object[][] _4_sinusTruthTable() {
        return new Object[][] {
            // @formatter:off
            // there are toooooooo much possibilities...
            { 0,         0 },
            // { PI,        0 }, // fails because precision loss
            { PI / 2,    1 },
            { 1.5 * PI, -1 },
            // { 2 * PI,    0 }, // fails because precision loss
            // @formatter:on
        };
    }
}
