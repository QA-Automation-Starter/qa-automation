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

import java.net.*;

import javax.ws.rs.*;

import org.openqa.selenium.*;

import lombok.extern.slf4j.*;

/**
 * A test runtime exception from which recovery is not possible.
 *
 * @author aherscu
 *
 */
@Slf4j
public class TestRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a test run-time exception without cause.
     */
    public TestRuntimeException() {
        super();
    }

    /**
     * Constructs a test run-time exception with a message.
     *
     * @param message
     *            the message of this exception
     */
    public TestRuntimeException(final String message) {
        super(message);
    }

    /**
     * Constructs a test run-time exception with a cause.
     *
     * @param cause
     *            the cause of this exception
     */
    public TestRuntimeException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param t
     *            a throwable to check
     * @return true if specified exception is due to some temporary condition,
     *         for example element not rendered yet or value does not match
     *         expectation yet.
     */
    public static boolean isRecoverableException(final Throwable t) {
        log.trace("testing if can recover from {}", t.toString());
        return t instanceof AssertionError
            || t.getCause() instanceof AssertionError
            || t instanceof WebDriverException
            || t instanceof ProcessingException
                && t.getCause() instanceof ConnectException;
    }
}
