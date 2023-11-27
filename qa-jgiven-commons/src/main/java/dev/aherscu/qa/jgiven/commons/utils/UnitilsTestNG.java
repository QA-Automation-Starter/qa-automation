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
package dev.aherscu.qa.jgiven.commons.utils;

import static java.lang.ThreadLocal.*;

import java.lang.reflect.*;

import org.testng.*;
import org.testng.annotations.*;
import org.unitils.core.*;

import lombok.extern.slf4j.*;

/**
 * Base test class that will Unitils-enable your test. This base class will make
 * sure that the core unitils test listener methods are invoked in the expected
 * order. See {@link TestListener} for more information on the listener
 * invocation order.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Slf4j
public abstract class UnitilsTestNG implements IHookable {

    /* True if beforeTestSetUp was called */
    private final ThreadLocal<Boolean> beforeTestSetUpCalled =
        withInitial(() -> Boolean.FALSE);

    /**
     * Implementation of the hookable interface to be able to call
     * {@link TestListener#beforeTestMethod} and
     * {@link TestListener#afterTestMethod}.
     *
     * @param callBack
     *            the TestNG test callback, not null
     * @param testResult
     *            the TestNG test result, not null
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {
        Throwable beforeTestMethodException = null;
        try {
            log.trace("before test method");
            getTestListener().beforeTestMethod(this, testResult
                .getMethod()
                .getConstructorOrMethod()
                .getMethod());

        } catch (Throwable e) {
            // hold exception until later, first call afterTestMethod
            log.error("before test method failed: {}", e.toString());
            beforeTestMethodException = e;
        }

        Throwable testMethodException = null;
        if (beforeTestMethodException == null) {
            log.trace("running test method");
            callBack.runTestMethod(testResult);

            // Since TestNG calls the method using reflection, the exception is
            // wrapped in an InvocationTargetException
            testMethodException = testResult.getThrowable();
            if (testMethodException != null
                && testMethodException instanceof InvocationTargetException) {
                testMethodException =
                    ((InvocationTargetException) testMethodException)
                        .getTargetException();
                log.error("test method failed: {}",
                    testMethodException.toString());
            }
        }

        Throwable afterTestMethodException = null;
        try {
            log.trace("after test method");
            getTestListener().afterTestMethod(this, testResult
                .getMethod()
                .getConstructorOrMethod()
                .getMethod(),
                beforeTestMethodException != null
                    ? beforeTestMethodException
                    : testMethodException);

        } catch (Throwable e) {
            log.error("after test method failed: {}", e.toString());
            afterTestMethodException = e;
        }

        // if there were exceptions, make sure the exception that occurred first
        // is reported by TestNG
        if (beforeTestMethodException != null) {
            throwException(beforeTestMethodException);
        } else {
            // We don't throw the testMethodException, it is already registered
            // by TestNG and will be reported to the user
            if (testMethodException == null
                && afterTestMethodException != null) {
                throwException(afterTestMethodException);
            }
        }
    }

    /**
     * @return The Unitils test listener
     */
    protected TestListener getTestListener() {
        return getUnitils().getTestListener();
    }

    /**
     * Returns the default singleton instance of Unitils
     *
     * @return the Unitils instance, not null
     */
    protected Unitils getUnitils() {
        return Unitils.getInstance();
    }

    /**
     * Throws an unchecked excepton for the given throwable.
     *
     * @param throwable
     *            The throwable, not null
     */
    protected void throwException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable instanceof Error) {
            throw (Error) throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Called after all test tear down. This is where
     * {@link TestListener#afterTestTearDown} is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even
     * when an exception occurs during {@link #beforeMethodSetUp}.
     *
     * @param testMethod
     *            The test method, not null
     */
    @AfterMethod(alwaysRun = true)
    protected void afterMethodTearDown(Method testMethod) {
        // alwaysRun is enabled, extra test to ensure that
        // unitilsBeforeTestSetUp was called
        if (beforeTestSetUpCalled.get()) {
            log.trace("after test tear down");
            beforeTestSetUpCalled.set(false);
            getTestListener().afterTestTearDown(this, testMethod);
        } else {
            log.error("before test setup not called/completed");
        }
    }

    /**
     * Called before a test of a test class is run. This is where
     * {@link TestListener#afterCreateTestObject(Object)} is called.
     */
    @BeforeClass(alwaysRun = true)
    protected void beforeClass() {
        log.trace("beforeTestClass & afterCreateTestObject");
        getTestListener().beforeTestClass(this.getClass());
        getTestListener().afterCreateTestObject(this);
    }

    /**
     * Called before all test setup. This is where
     * {@link TestListener#beforeTestSetUp} is called.
     *
     * @param testMethod
     *            The test method, not null
     */
    @BeforeMethod(alwaysRun = true)
    protected void beforeMethodSetUp(Method testMethod) {
        log.trace("before test set up");
        beforeTestSetUpCalled.set(true);
        getTestListener().beforeTestSetUp(this, testMethod);
    }

}
