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
package dev.aherscu.qa.jgiven.ssh.model;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.io.*;

import com.google.common.base.*;

import lombok.*;
import lombok.extern.slf4j.*;
import net.schmizz.sshj.*;
import net.schmizz.sshj.common.*;

/**
 * Encapsulates the {@code ssh-info} field as defined in
 * <a href="https://tools.ietf.org/html/draft-ietf-secsh-scp-sftp-ssh-uri-04">
 * Uniform Resource Identifier (URI) Scheme for Secure File Transfer Protocol
 * (SFTP) and Secure Shell (SSH)</a>.
 *
 * @author aherscu
 */
@Slf4j
@AllArgsConstructor
public final class SshInfo {
    /**
     * The default SSH directory; that's the {@code .ssh} directory under the
     * user's home directory.
     */
    public static final File   DEFAULT_SSH_DIR      =
        new File(StandardSystemProperty.USER_HOME.value(), ".ssh");           //$NON-NLS-1$
    /**
     * If you wish to specify a custom SSH private key location then you should
     * launch your process with
     * {@code -Dssh.private.key.file=<your private key path>}. This system
     * property will be consulted before trying to load an SSH private key from
     * its default location (as specified in {@link #DEFAULT_SSH_DIR}).
     *
     * @see #authenticate(SSHClient)
     */
    public static final String SSH_PRIVATE_KEY_FILE = "ssh.private.key.file"; //$NON-NLS-1$
    private final String       sshInfo;

    private static String defaultSshPrivateKeyFile(final String name) {
        return new File(DEFAULT_SSH_DIR, name).toString();
    }

    private static String sshPrivateKeyFile(final String defaultName) {
        val file = System.getProperty(SSH_PRIVATE_KEY_FILE,
            defaultSshPrivateKeyFile(defaultName));
        log.debug("using SSH private key from {}", file); //$NON-NLS-1$
        return file;
    }

    /**
     * Authenticates an SSH client using this ssh-info.
     *
     * <p>
     * The recognized {@code ssh-info} structure is as follows: {@code
     * <user-name>;auth=<method>:<password>}
     * </p>
     *
     * <p>
     * where:
     * </p>
     * <ul>
     * <li>{@code <user-name>} -- required, the user name</li>
     * <li>auth={@code <method>} -- required, the authentication method; one of:
     * <ul>
     * <li>PASSWORD
     * <li>PUBLIC_KEY; if this method is used, then an attempt to read an user
     * defined SSH private key file will be made using the
     * {@value #SSH_PRIVATE_KEY_FILE} system property, if that fails then it
     * will fallback on reading {@code id_rsa} file from the
     * {@link #DEFAULT_SSH_DIR}
     * </ul>
     * </li>
     * <li>{@code <password>} -- required, the password; can be empty
     * </ul>
     * 
     *
     * @param client
     *            the SSH client
     * @return the authenticated SSH client
     * @throws SSHException
     *             authentication or transport failure
     */
    public SSHClient authenticate(final SSHClient client)
        throws SSHException {
        switch (authenticationMethod()) {
        case PASSWORD:
            client.authPassword(userName(), password());
            break;
        case PUBLIC_KEY:
            client.authPublickey(userName(), sshPrivateKeyFile("id_rsa")); //$NON-NLS-1$
            break;
        default:
            throw new UnsupportedOperationException(sshInfo);
        }
        return client;
    }

    private SshInfo.AuthMethod authenticationMethod() {
        return AuthMethod.valueOf(cparam("auth")); //$NON-NLS-1$
    }

    private String cparam(final String cparamName) {
        return Splitter
            .on(COMMA)
            .trimResults()
            .withKeyValueSeparator(EQUAL)
            .split(substringBetween(sshInfo, SEMI, COLON))
            .get(cparamName);
    }

    private String password() {
        return substringAfter(sshInfo, COLON);
    }

    private String userName() {
        return substringBefore(substringBefore(sshInfo, COLON), SEMI);
    }

    private enum AuthMethod {
        PASSWORD, PUBLIC_KEY
    }
}
