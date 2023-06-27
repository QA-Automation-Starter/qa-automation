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

import static dev.aherscu.qa.testing.utils.ObjectUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.apache.commons.io.IOUtils.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javax.annotation.concurrent.*;

import org.apache.commons.io.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.ssh.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.schmizz.sshj.*;
import net.schmizz.sshj.transport.verification.*;

/**
 * Generic SSH configuration actions. Act on previously set configuration. Each
 * action closes the connection.
 * 
 * @param <SELF>
 *            the type of the subclass
 * 
 * @author aherscu
 */
@ThreadSafe
@Slf4j
@SuppressWarnings("boxing")
public class SshActions<SELF extends SshActions<SELF>>
    extends GenericActions<SshScenarioType, SELF> {
    /**
     * The area into which to download remote files. Initialized from
     * {@code target.downloads} system property and defaults to
     * {@code target/downloads}.
     */
    public static final String           TARGET_DOWNLOADS =
        System.getProperty("target.downloads", "target/downloads"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The exit status of last SSH batch command.
     */
    @ProvidedScenarioState
    protected final ThreadLocal<Integer> exitStatus       =
        new ThreadLocal<>();

    /**
     * The given SSH client configuration.
     */
    @ExpectedScenarioState
    protected ThreadLocal<Config>        configuration;
    /**
     * The given SSH client connection timeout.
     */
    @ExpectedScenarioState
    protected ThreadLocal<Integer>       connectionTimeout;
    /**
     * The given SSH remote stream redirection target.
     */
    @ExpectedScenarioState
    protected ThreadLocal<OutputStream>  executionOutput;
    /**
     * The given SSH remote execution timeout.
     */
    @ExpectedScenarioState
    protected ThreadLocal<Integer>       executionTimeout;
    /**
     * The given SSH client read timeout.
     */
    @ExpectedScenarioState
    protected ThreadLocal<Integer>       readTimeout;

    private static SSHClient connect(
        final SSHClient client,
        final URI url) throws IOException {
        log.debug("connecting to {}", url); //$NON-NLS-1$

        // FIXME: client configuration should be done from outside
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(url.getHost(), portOfOrDefault(url));

        return new SshInfo(url.getUserInfo())
            .authenticate(client);
    }

    private static int portOfOrDefault(final URI url) {
        return -1 == url.getPort()
            ? SSHClient.DEFAULT_PORT
            : url.getPort();
    }

    /**
     * Deletes the entire directory of downloaded files.
     * 
     * @return {@link #self()}
     */
    @NestedSteps
    public SELF deleting_downloaded_files() {
        return deleting_directory(new File(TARGET_DOWNLOADS));
    }

    /**
     * Downloads a file from target SSH machine to local machine.
     * 
     * @param url
     *            the machine from which to download
     * @param file
     *            the file path to download
     * @return {@link #self()}
     * @see SshActions#TARGET_DOWNLOADS
     */
    @SneakyThrows(IOException.class)
    public SELF downloading(final URI url, final File file) {
        val fixedFilePath = FilenameUtils.separatorsToUnix(file.toString());
        log.debug("downloading {} from {}", fixedFilePath, url); //$NON-NLS-1$
        try (val client = connect(createClient(), url)) {
            FileUtils.forceMkdir(new File(TARGET_DOWNLOADS));
            client
                .newSCPFileTransfer()
                .download(fixedFilePath, TARGET_DOWNLOADS);
        }
        return self();
    }

    /**
     * Executes a batch command on target SSH machine, using the timeout set by
     * {@link SshFixtures#execution_timeout(int)} and the target output stream
     * set by {@link SshFixtures#execution_output_to(OutputStream)}.
     * 
     * @param url
     *            the machine on which to execute
     * @param command
     *            the batch command to execute on SSH target machine
     * @return {@link #self()}
     */
    public SELF executing(final URI url, final String command) {
        return executing(url, command,
            executionTimeout.get(),
            TimeUnit.MILLISECONDS,
            executionOutput.get());
    }

    /**
     * Executes a batch command on target SSH machine.
     * 
     * @param url
     *            the machine on which to execute
     * @param command
     *            the batch command to execute on SSH target machine
     * @param timeout
     *            how long to wait for the command execution to complete; set to
     *            0 to wait indefinitely
     * @param timeUnit
     *            timeout units
     * @param outputStream
     *            output stream to receive what the remote SSH process generate;
     *            be warned that this can be very big
     * @return {@link #self()}
     */
    @SneakyThrows(IOException.class)
    public SELF executing(
        final URI url,
        final String command,
        final long timeout,
        final TimeUnit timeUnit,
        final OutputStream outputStream) {
        log.debug("executing {} on {} with timeout {} {} and output stream {}", //$NON-NLS-1$
            command, url, timeout, timeUnit.toString(), outputStream);
        try (val client = connect(createClient(), url);
            val session = client.startSession();
            val execution = session.exec(toUnix(command));
            val inputStream = execution.getInputStream()) {
            copy(inputStream, outputStream);
            execution.join(timeout, timeUnit);
            exitStatus.set(execution.getExitStatus());
            log.debug("remote SSH process ended"); //$NON-NLS-1$
        }

        return self();
    }

    private SSHClient createClient() {
        requireNonNull(configuration.get(),
            "must call given().an_SSH_client before"); //$NON-NLS-1$
        log.debug("setting connection timeout to {} and read timeout to {}", //$NON-NLS-1$
            connectionTimeout.get(), readTimeout.get());
        val client = new SSHClient(configuration.get());
        client.setConnectTimeout(connectionTimeout.get());
        client.setTimeout(readTimeout.get());
        return client;
    }
}
