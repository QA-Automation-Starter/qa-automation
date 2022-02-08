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
package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.tester.utils.Base64Utils.*;
import static dev.aherscu.qa.tester.utils.config.AbstractConfiguration.*;
import static java.lang.Integer.*;
import static java.lang.Thread.*;
import static java.lang.ThreadLocal.*;
import static java.time.Instant.*;
import static java.util.UUID.*;
import static org.apache.commons.collections4.SetUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.lang.SuppressWarnings;
import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import javax.annotation.concurrent.*;

import org.apache.commons.beanutils.*;
import org.apache.commons.configuration.Configuration;
import org.jooq.lambda.*;
import org.reflections.*;
import org.testng.*;
import org.testng.annotations.*;
import org.unitils.core.*;

import com.github.rodionmoiseev.c10n.*;
import com.github.rodionmoiseev.c10n.annotations.*;
import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.actions.*;
import dev.aherscu.qa.jgiven.commons.fixtures.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.verifications.*;
import dev.aherscu.qa.tester.utils.config.AbstractConfiguration;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Enables <a href="http://unitils.org/tutorial-core.html">Unitils</a> on
 * JGiven/TestNG tests.
 *
 * <p>
 * NOTE: implementation copied from {@link org.unitils.UnitilsTestNG}
 * </p>
 *
 * @param <C>
 *            type of configuration used by scenario
 * @param <T>
 *            type of scenario
 * @param <GIVEN>
 *            the fixtures stage
 * @param <WHEN>
 *            the actions stage
 * @param <THEN>
 *            the verifications stage
 * @author aherscu
 */
@ThreadSafe
@Slf4j
public abstract class UnitilsScenarioTest<C extends AbstractConfiguration<? extends Configuration>, T extends AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN extends GenericActions<T, ?> & ScenarioType<T>, THEN extends GenericVerifications<T, ?> & ScenarioType<T>>
    extends TypedScenarioTest<T, GIVEN, WHEN, THEN> implements IHookable {
    /**
     * The internal data provider name.
     */
    public static final String          INTERNAL_DATA_PROVIDER = "data"; // $NON-NLS-1$
    /**
     * Concurrency achieved so far.
     */
    public static final AtomicInteger   concurrency            =
        new AtomicInteger(0);
    /**
     * List of generated random identifiers.
     */
    public static final Set<String>     issuedRandomIds        =
        synchronizedSet(new HashSet<>());
    /**
     * The first parameter to be used with {@link CaseAs} annotation.
     */
    protected static final String       USE_FIRST_PARAM        = "$1";   // $NON-NLS-1$
    private static final Configurations CACHED_CONFIGURATIONS  =
        new Configurations();

    static {
        // TODO add a test for the string resources infrastructure
        C10N.configure(new C10NConfigBase() {
            @Override
            public void configure() {
                install(new DefaultC10NAnnotations());
                bindAnnotation(Root.class).toLocale(Locale.ROOT);
            }
        });

        // NOTE: the generic CSV data provider feature is not ready for prime
        // time yet; use -Dcsvtyperesolvers to enable it
        // see also http://databene.org/feed4testng
        if (null != System.getProperty("csvtyperesolvers")) { //$NON-NLS-1$
            try {
                val typesAnnotatedWithBeanUtilsConverter = new Reflections(
                    "dev.aherscu.qa.jgiven.commons.scenarios") //$NON-NLS-1$
                        .getTypesAnnotatedWith(
                            BeanUtilsConverter.class);
                for (val clazz : typesAnnotatedWithBeanUtilsConverter) {
                    val annotation = clazz
                        .getAnnotation(BeanUtilsConverter.class);
                    if (null == annotation)
                        // NOTE: this may happen only if the BeanUtilsConverter
                        // annotation has no Runtime retention; but we know it
                        // has
                        throw new InternalError(
                            "should not happen"); //$NON-NLS-1$
                    val converter = annotation.value();
                    log.trace("registering {} for {}", converter,
                        clazz); // $NON-NLS-1$
                    ConvertUtils.register(converter.newInstance(), clazz);
                }
            } catch (final InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The configuration type of this scenario.
     */
    protected final Class<C>             configurationType;
    /**
     * Start time of execution thread.
     *
     * @see #unitilsBeforeMethod(Method, Object[])
     */
    protected final ThreadLocal<Instant> startTime             =
        withInitial(Instant::now);
    /**
     * Stores a random string per thread.
     *
     * @see #unitilsBeforeMethod(Method, Object[])
     * @see #randomId()
     */
    protected final ThreadLocal<String>  randomId              =
        withInitial(() -> EMPTY);
    private final ThreadLocal<Boolean>   beforeTestSetUpCalled =
        withInitial(() -> Boolean.FALSE);

    /**
     * Initializes the configuration type of this scenario by
     * {@value AbstractConfiguration#CONFIGURATION_SOURCES}.
     *
     * @param configurationType
     *            type of configuration
     */
    protected UnitilsScenarioTest(final Class<C> configurationType) {
        this.configurationType = configurationType;
    }

    private static TestListener getTestListener() {
        return Unitils.getInstance().getTestListener();
    }

    @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
    private static void throwException(final Throwable throwable) {
        if (throwable instanceof RuntimeException)
            throw (RuntimeException) throwable;
        else if (throwable instanceof Error)
            throw (Error) throwable;
        else
            throw new RuntimeException(throwable);
    }

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
    // implementation copied from UnitilsTestNG
    @Override
    public final void run(
        final IHookCallBack callBack,
        final ITestResult testResult) {
        // ISSUE seems that UnitilsScenarioTest derived test classes
        // prevents IHookable#run to be invoked, hence cannot use this method
        // for handling pre/post method execution.
        // UnitilsScenarioTest implements IHookable#run in order to integrate
        // with the Unitils framework; perhaps this implementation is faulty.

        log.trace("running {}", testResult.getName());

        Throwable beforeTestMethodException = null;
        try {
            getTestListener()
                .beforeTestMethod(this,
                    testResult.getMethod().getConstructorOrMethod()
                        .getMethod());

        } catch (final Throwable e) {
            // hold exception until later, first call afterTestMethod
            beforeTestMethodException = e;
        }

        Throwable testMethodException = null;
        if (beforeTestMethodException == null) {
            callBack.runTestMethod(testResult);

            // Since TestNG calls the method using reflection, the exception is
            // wrapped in an InvocationTargetException
            testMethodException = testResult.getThrowable();
            if (testMethodException instanceof InvocationTargetException) {
                testMethodException =
                    ((InvocationTargetException) testMethodException)
                        .getTargetException();
            }
        }

        Throwable afterTestMethodException = null;
        try {
            getTestListener()
                .afterTestMethod(
                    this,
                    testResult.getMethod().getConstructorOrMethod().getMethod(),
                    beforeTestMethodException != null
                        ? beforeTestMethodException
                        : testMethodException);

        } catch (final Throwable e) {
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
     * On first call initializes the configuration for this scenario type and
     * caches it; on further calls will return the cached configuration.
     *
     * @return the configuration for this type
     *
     * @throws RuntimeException
     *             if the configuration type cannot be instantiated or the
     *             {@value AbstractConfiguration#CONFIGURATION_SOURCES} refer to
     *             a non-existing resource.
     */
    @SuppressWarnings("unchecked")
    protected C configuration() {
        return (C) CACHED_CONFIGURATIONS.computeIfAbsent(
            configurationType,
            Unchecked.function((type) -> type
                .getConstructor(Configuration[].class)
                .newInstance((Object) new Configuration[] {
                    defaultConfiguration() })));
    }

    /**
     * Generates a Base64, URL-friendly, encoding of a random UUID. It is called
     * once per thread initialization.
     * <p>
     * Override to provide different behavior.
     * </p>
     *
     * @return random identifier to be used during the entire test session
     */
    protected String generateRandomId() {
        return encode(randomUUID());
    }

    /**
     * @return a random string; by default, initialized before each method.
     * @see #unitilsBeforeMethod(Method, Object[])
     */
    protected final String randomId() {
        return randomId.get();
    }

    /**
     * Called after all test tear down. This is where
     * {@link TestListener#afterTestTearDown} is called.
     * <p/>
     * NOTE: alwaysRun is enabled to be sure that this method is called even
     * when an exception occurs during {@link #unitilsBeforeMethod}.
     *
     * @param testMethod
     *            The test method, not null
     */
    @AfterMethod(alwaysRun = true)
    protected final void unitilsAfterMethod(final Method testMethod) {
        // alwaysRun is enabled, extra test to ensure that
        // unitilsBeforeTestSetUp was called
        try {
            if (beforeTestSetUpCalled.get().booleanValue()) {
                beforeTestSetUpCalled.set(Boolean.FALSE);
                // NOTE: sometimes no transaction is started by Unitils, but it
                // always tries to commit the transaction associated with
                // current test. When trying to commit a not existent
                // transaction a NullPointerException occurs. This seems like a
                // bug in Unitils; see DefaultUnitilsTransactionManager#137.
                getTestListener().afterTestTearDown(this, testMethod);
            }
        } catch (final Exception e) {
            log.warn("after test got {}", e.getMessage()); //$NON-NLS-1$
        } finally {
            // IMPORTANT: when running repeated tests in Jenkins we must write
            // something into the console otherwise connection is lost
            log.info("test {}:{} ended", //$NON-NLS-1$
                testMethod.getDeclaringClass().getName(),
                testMethod.getName());
        }
    }

    /**
     * <p>
     * Renames current thread as {@code <class-simple-name>}, as returned by
     * {@link Class#getSimpleName()}. This means that code running in
     * {@link BeforeClass} or {@link BeforeTest} annotated methods will have its
     * thread named such this.
     * </p>
     * <p>
     * Finally, notifies <tt>Unitils</tt> about being before class via
     * {@link TestListener#beforeTestClass(Class)} and
     * {@link TestListener#afterCreateTestObject(Object)}.
     * </p>
     */
    @BeforeClass(alwaysRun = true)
    protected final void unitilsBeforeClass() {
        currentThread().setName(SessionName.builder()
            .className(getClass().getSimpleName())
            .id(String.valueOf(currentThread().getId()))
            .build()
            .toString());

        // IMPORTANT: when running repeated tests in Jenkins we must write
        // something into the console otherwise connection is lost
        log.info("starting test class {}", getClass().getSimpleName());

        getTestListener().beforeTestClass(this.getClass());
        getTestListener().afterCreateTestObject(this);
    }

    /**
     * Called before every test method. This is where
     * {@link TestListener#beforeTestSetUp} is called.
     *
     * <p>
     * If the {@link #randomId} for <tt>current thread</tt> was not already set,
     * then generates one and sets it. The generated random id is URL-friendly.
     * Thread starting time is stored in {@link #startTime}.
     * </p>
     *
     * <p>
     * In addition, renames current thread as
     * {@code <class-simple-name>:<method-name>:<hash-of-params>@<thread-id>},
     * where:
     * <dl>
     * <dt>class-simple-name</dt>
     * <dd>as returned by {@link Class#getSimpleName()}</dd>
     * <dt>method-name</dt>
     * <dd>as returned by {@link Method#getName()}</dd>
     * <dt>class-simple-name</dt>
     * <dd>hex string of whatever is returned by
     * {@link Arrays#hashCode(Object[])}, wherein {@code Object[]} is the
     * parameters of the test method</dd>
     * <dt>thread-id</dt>
     * <dd>identifier of thread as of {@link Thread#getId()}
     * </dl>
     * This means code running in {@link Test} annotated methods will have its
     * thread named such this.
     * </p>
     * <p>
     * Finally, notifies <tt>Unitils</tt> about being before method via
     * {@link TestListener#beforeTestSetUp(Object, Method)}
     * </p>
     *
     * @param testMethod
     *            The test method, not null
     * @param parameters
     *            the parameters used to invoke this test method instance
     */
    @BeforeMethod(alwaysRun = true)
    protected final void unitilsBeforeMethod(
        final Method testMethod,
        final Object[] parameters) {
        currentThread().setName(SessionName.builder()
            .className(testMethod.getDeclaringClass().getSimpleName())
            .methodName(testMethod.getName())
            .params(toHexString(Arrays.hashCode(parameters)))
            .id(String.valueOf(currentThread().getId()))
            .build()
            .toString());

        log.info("starting test method {} with {} active threads",
            currentThread().getName(),
            concurrency.updateAndGet(
                previousCount -> max(previousCount, activeCount())));

        if (isBlank(randomId.get())) {
            val temporaryRandomId = generateRandomId();
            if (issuedRandomIds.add(temporaryRandomId)) {
                startTime.set(now());
                randomId.set(temporaryRandomId);
                log.debug("running on fresh thread, new random id {} set",
                    randomId.get());
            } else {
                throw new TestRuntimeException(
                    "Internal error: duplicate random identifier found");
            }
        } else {
            log.debug("running on previous thread, reusing random id {}",
                randomId.get());
        }

        beforeTestSetUpCalled.set(Boolean.TRUE);

        // NOTE: should fail fast if not able to connect to database
        // try {
        getTestListener().beforeTestSetUp(this, testMethod);
        // } catch (final Exception e) {
        // log.warn("before test got {}", e.getMessage()); //$NON-NLS-1$
        // }
    }

    @SuppressWarnings("serial")
    @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
    private static class Configurations extends
        ConcurrentHashMap<Class<? extends AbstractConfiguration<?>>, AbstractConfiguration<?>> {
        // NOTE: just to make further declaration shorter...
    }
}
