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

import static org.apache.commons.lang3.StringUtils.*;

import java.lang.SuppressWarnings;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

import com.tngtech.jgiven.annotation.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

/**
 * Monitors execution of JGiven step methods. If {@code dryrun} system property
 * is not blank then it shortcuts the actual method execution.
 * 
 * <p>
 * Other approach for dry running is available via JGiven 0.18.2 library see
 * https://github.com/TNG/JGiven/issues/418
 * </p>
 * <p>
 * Update: planned for 1.0.0; see https://github.com/TNG/JGiven/pull/435
 * </p>
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@Aspect
@Slf4j
public class DryRunAspect {
    public static final boolean dryRun =
        isNotBlank(System.getProperty("dryrun"));

    /**
     * Matches the execution of methods annotated with
     * {@link org.testng.annotations.AfterClass}.
     */
    @Pointcut("execution("
        + "@org.testng.annotations.AfterClass "
        + "* *(..))")
    public void afterClass() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with
     * {@link org.testng.annotations.AfterMethod}.
     */
    @Pointcut("execution("
        + "@org.testng.annotations.AfterMethod "
        + "* *(..))")
    public void afterMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with {@link AfterScenario}.
     */
    @Pointcut("execution("
        + "@com.tngtech.jgiven.annotation.AfterScenario "
        + "* *(..))")
    public void afterScenario() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with {@link AfterStage}.
     */
    @Pointcut("execution("
        + "@com.tngtech.jgiven.annotation.AfterStage "
        + "* *(..))")
    public void afterStage() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Prevents execution if in {@code dryrun} mode.
     *
     * @param thisJoinPoint
     *            advised join point
     * @return forwarded from advised method
     * @throws Throwable
     *             re-thrown as received advised method
     * @see #stageMethod()
     * @see #stepMethod()
     */
    @Around("notUnitilsScenarioTestMethod() && beforeOrAfterMethod()")
    @SuppressWarnings("static-method")
    public Object aroundBeforeOrAfterMethod(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        // FIXME duplicated from above
        if (dryRun) {
            log.trace("dryrun -- skipping {}:{}",
                thisJoinPoint.getTarget(),
                thisJoinPoint.getSignature().getName());
            return thisJoinPoint.getTarget();
        }

        return thisJoinPoint.proceed(thisJoinPoint.getArgs());
    }

    /**
     * Prevents execution if in {@code dryrun} mode.
     *
     * @param thisJoinPoint
     *            advised join point
     * @return forwarded from advised method
     * @throws Throwable
     *             re-thrown as received advised method
     * @see #stageMethod()
     * @see #stepMethod()
     */
    @Around("stageMethod() && stepMethod()")
    @SuppressWarnings("static-method")
    public Object aroundStepMethod(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        if (dryRun) {
            log.trace("dryrun -- skipping {}:{}",
                thisJoinPoint.getTarget(),
                thisJoinPoint.getSignature().getName());
            return thisJoinPoint.getTarget();
        }

        return thisJoinPoint.proceed(thisJoinPoint.getArgs());
    }

    /**
     * Matches the execution of methods annotated with
     * {@link org.testng.annotations.BeforeClass}.
     */
    @Pointcut("execution("
        + "@org.testng.annotations.BeforeClass "
        + "* *(..))")
    public void beforeClass() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with
     * {@link org.testng.annotations.BeforeMethod}.
     */
    @Pointcut("execution("
        + "@org.testng.annotations.BeforeMethod "
        + "* *(..))")
    public void beforeMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of before or after methods.
     */
    @Pointcut("beforeMethod() || afterMethod()"
        + " || beforeClass() || afterClass()"
        + " || beforeStage() || afterStage()"
        + " || beforeScenario() || afterScenario()")
    public void beforeOrAfterMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with {@link BeforeScenario}.
     */
    @Pointcut("execution("
        + "@com.tngtech.jgiven.annotation.BeforeScenario "
        + "* *(..))")
    public void beforeScenario() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of methods annotated with {@link BeforeStage}.
     */
    @Pointcut("execution("
        + "@com.tngtech.jgiven.annotation.BeforeStage "
        + "* *(..))")
    public void beforeStage() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of any method not within
     * {@link dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest}.
     */
    @Pointcut("within(!dev.aherscu.qa.jgiven.commons.utils.UnitilsScenarioTest)")
    public void notUnitilsScenarioTestMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of any method within a sub-type of
     * {@link com.tngtech.jgiven.base.StageBase}.
     */
    @Pointcut("within(com.tngtech.jgiven.base.StageBase+)")
    public void stageMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    /**
     * Matches the execution of any method returning SELF (a subclass of
     * {@link com.tngtech.jgiven.base.StageBase}.
     */
    @Pointcut("execution(com.tngtech.jgiven.base.StageBase+ *(..))")
    public void stepMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }
}
