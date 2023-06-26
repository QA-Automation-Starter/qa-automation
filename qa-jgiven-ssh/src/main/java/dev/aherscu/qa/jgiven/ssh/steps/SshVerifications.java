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
package dev.aherscu.qa.jgiven.ssh.steps;

import static org.assertj.core.api.Assertions.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.jgiven.ssh.model.*;

/**
 * Generic SSH configuration verifications. Verifications on the response
 * retrieved by previous action.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@ThreadSafe
public class SshVerifications<SELF extends SshVerifications<SELF>>
    extends GenericVerifications<SshScenarioType, SELF> {

    /**
     * The exit status of last SSH batch command.
     */
    @ExpectedScenarioState
    protected final ThreadLocal<Integer> exitStatus = new ThreadLocal<>();

    /**
     * Verifies that the exit status of last executed SSH batch command is as
     * expected.
     *
     * @param expected
     *            the expected exit status
     * @return {@link #self()}
     */
    public SELF the_exit_status_is(final int expected) {
        assertThat(exitStatus.get()).isEqualTo(Integer.valueOf(expected));
        return self();
    }
}
