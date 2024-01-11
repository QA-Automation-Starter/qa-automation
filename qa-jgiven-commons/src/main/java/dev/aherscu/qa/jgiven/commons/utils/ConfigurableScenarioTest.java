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

import static dev.aherscu.qa.testing.utils.Base64Utils.*;
import static dev.aherscu.qa.testing.utils.config.AbstractConfiguration.*;
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

import com.github.rodionmoiseev.c10n.*;
import com.github.rodionmoiseev.c10n.annotations.*;
import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.testing.utils.config.AbstractConfiguration;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Enables Apache Configuration on JGiven/TestNG tests.
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
public abstract class ConfigurableScenarioTest<C extends AbstractConfiguration<? extends Configuration>, T extends AnyScenarioType, GIVEN extends GenericFixtures<T, ?> & ScenarioType<T>, WHEN extends GenericActions<T, ?> & ScenarioType<T>, THEN extends GenericVerifications<T, ?> & ScenarioType<T>>
    extends TypedScenarioTest<T, GIVEN, WHEN, THEN> {
    @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
    private static class Configurations extends
        ConcurrentHashMap<Class<? extends AbstractConfiguration<?>>, AbstractConfiguration<?>> {
        // NOTE: just to make further declaration shorter...
    }

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
     */
    protected final ThreadLocal<Instant> startTime             =
        withInitial(Instant::now);
    /**
     * Stores a random string per thread.
     *
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
    protected ConfigurableScenarioTest(final Class<C> configurationType) {
        this.configurationType = configurationType;
    }

    /**
     * When running repeated tests in Jenkins we must write something into the
     * console otherwise connection is lost.
     *
     * @param testMethod
     *            The test method, not null
     */
    @AfterMethod(alwaysRun = true)
    protected final void afterMethodKeepJenkinsAlive(final Method testMethod) {
        log.info("test {}:{} ended", //$NON-NLS-1$
            testMethod.getDeclaringClass().getName(),
            testMethod.getName());
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
     */
    protected final String randomId() {
        return randomId.get();
    }

    /**
     * <p>
     * Renames current thread as {@code <class-simple-name>}, as returned by
     * {@link Class#getSimpleName()}. This means that code running in
     * {@link BeforeClass} or {@link BeforeTest} annotated methods will have its
     * thread named such this.
     * </p>
     */
    @BeforeClass(alwaysRun = true)
    protected final void beforeClassInitializeSession() {
        currentThread().setName(SessionName.builder()
            .className(getClass().getSimpleName())
            .id(String.valueOf(currentThread().getId()))
            .build()
            .toString());

        // IMPORTANT: when running repeated tests in Jenkins we must write
        // something into the console otherwise connection is lost
        log.info("starting test class {}", getClass().getSimpleName());
    }

    /**
     * <p>
     * If the {@link #randomId} for {@code current thread} was not already set,
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
     *
     * @param testMethod
     *            The test method, not null
     * @param parameters
     *            the parameters used to invoke this test method instance
     */
    @BeforeMethod(alwaysRun = true)
    protected final void beforeMethodInitializeSession(
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
    }
}
