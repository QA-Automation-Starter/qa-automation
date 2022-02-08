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

import static dev.aherscu.qa.tester.utils.StringUtilsExtensions.*;
import static java.util.Arrays.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

import java.lang.SuppressWarnings;
import java.util.*;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Logs execution of JGiven step methods.
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@Aspect
@Slf4j
public class LoggingAspect {

    /**
     * Logs step execution.
     *
     * @param thisJoinPoint
     *            advised join point
     * @return forwarded from advised method
     * @throws Throwable
     *             re-thrown as received advised method
     * @see #stepMethod()
     */
    @Around("stepMethod()")
    @SuppressWarnings("static-method")
    public Object aroundStepMethod(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        log.trace(">>> {}:{}",
            thisJoinPoint.getSignature().getName(),
            stream(isNull(thisJoinPoint.getArgs())
                ? new Object[] {}
                : thisJoinPoint.getArgs())
                    .map(arg -> Objects.toString(arg, "null"))
                    .collect(joining(COMMA, "[", "]")));

        val retval = thisJoinPoint.proceed(thisJoinPoint.getArgs());

        log.trace("<<< {}",
            thisJoinPoint.getSignature().getName());

        return retval;
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
