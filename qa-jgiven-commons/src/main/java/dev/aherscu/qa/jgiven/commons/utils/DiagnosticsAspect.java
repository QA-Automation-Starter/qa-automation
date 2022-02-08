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

import java.lang.SuppressWarnings;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

/**
 * TBD
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@Aspect
@Slf4j
public class DiagnosticsAspect {

    public static void selfTest() {
        log.debug("self test");
        throw new AssertionError("there was an error");
    }

    @Around("selfTestExecution()")
    @SuppressWarnings({ "static-method", "unused" })
    public Object aroundSelfTest(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        log.debug("before");
        try {
            log.debug("thisJoinPoint {}", thisJoinPoint);
            log.debug("args {}", thisJoinPoint.getArgs());
            return thisJoinPoint.proceed(thisJoinPoint.getArgs());
        } finally {
            log.debug("after");
        }
    }

    // see
    // https://stackoverflow.com/questions/62250100/aspectj-execution-pointcut-on-same-method-throwing-npe
    // https://stackoverflow.com/questions/58626598/aspectj-exceptions-handling-on-multiple-matching-advices
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=552687
    @Around("selfTestExecution2()")
    @SuppressWarnings({ "static-method", "unused" })
    public Object aroundSelfTest2(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        log.debug("before2");
        try {
            log.debug("thisJoinPoint2 {}", thisJoinPoint);
            log.debug("args2 {}", thisJoinPoint.getArgs());
            return thisJoinPoint.proceed(thisJoinPoint.getArgs());
        } finally {
            log.debug("after2");
        }
    }

    @Pointcut("execution(void DiagnosticsAspect.selfTest())")
    @SuppressWarnings({ "unused" })
    public void selfTestExecution() {
        // nothing to do here -- just defines a pointcut matcher
    }

    @Pointcut("execution(void DiagnosticsAspect.selfTest())")
    @SuppressWarnings({ "unused" })
    public void selfTestExecution2() {
        // nothing to do here -- just defines a pointcut matcher
    }
}
