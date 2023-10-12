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

import static dev.aherscu.qa.jgiven.commons.utils.ConnectionDefaults.*;

import java.io.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.ssh.model.*;
import net.schmizz.sshj.*;

/**
 * Generic SSH client fixtures. Sets up an SSH client configuration.
 *
 * @param <SELF>
 *            the type of the subclass
 *
 * @author aherscu
 */
@ThreadSafe
@SuppressWarnings("boxing")
public class SshFixtures<SELF extends SshFixtures<SELF>>
    extends GenericFixtures<SshScenarioType, SELF> {

    /**
     * The given SSH client configuration.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Config>       configuration;

    /**
     * The given SSH client connection timeout.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Integer>      connectionTimeout;

    /**
     * The given SSH remote stream redirection target.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<OutputStream> executionOutput;

    /**
     * The given SSH remote execution timeout.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Integer>      executionTimeout;

    /**
     * The given SSH client read timeout.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Integer>      readTimeout;

    {
        configuration = ThreadLocal.withInitial(DefaultConfig::new);
        connectionTimeout =
            ThreadLocal.withInitial(() -> DEFAULT_CONNECTION_TIMEOUT);
        readTimeout = ThreadLocal.withInitial(() -> DEFAULT_READ_TIMEOUT);
        executionTimeout =
            ThreadLocal.withInitial(() -> DEFAULT_EXECUTION_TIMEOUT);
        // noinspection UseOfSystemOutOrSystemErr
        executionOutput = ThreadLocal.withInitial(() -> System.err);
    }

    /**
     * Sets up a specific SSH configuration; otherwise the {@link DefaultConfig}
     * will be used.
     *
     * @param config
     *            the configuration to use
     *
     * @return {@link #self()}
     */
    public SELF an_SSH_client_configuration(final Config config) {
        configuration.set(config);
        return self();
    }

    /**
     * Sets the connection timeout on the given SSH client; otherwise
     * {@link ConnectionDefaults#DEFAULT_CONNECTION_TIMEOUT}.
     *
     * @param millis
     *            the read timeout interval, in milliseconds.
     *
     * @return {@link #self()}
     */
    public SELF connection_timeout(
        @UnitFormatter.Annotation("ms") final int millis) {
        connectionTimeout.set(millis);
        return self();
    }

    /**
     * Sets the execution output on the given SSH client; otherwise
     * {@link System#err}.
     *
     * @param target
     *            the target output stream
     *
     * @return {@link #self()}
     */
    public SELF execution_output_to(final OutputStream target) {
        executionOutput.set(target);
        return self();
    }

    /**
     * Sets the execution timeout on the given SSH client; otherwise
     * {@link ConnectionDefaults#DEFAULT_EXECUTION_TIMEOUT}.
     *
     * @param millis
     *            the read timeout interval, in milliseconds.
     *
     * @return {@link #self()}
     */
    public SELF execution_timeout(
        @UnitFormatter.Annotation("ms") final int millis) {
        executionTimeout.set(millis);
        return self();
    }

    /**
     * Sets the read timeout on the given SSH client; otherwise
     * {@link ConnectionDefaults#DEFAULT_READ_TIMEOUT}.
     *
     * @param millis
     *            the read timeout interval, in milliseconds.
     *
     * @return {@link #self()}
     */
    public SELF read_timeout(@UnitFormatter.Annotation("ms") final int millis) {
        readTimeout.set(millis);
        return self();
    }
}
