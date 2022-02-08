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

import com.tngtech.jgiven.report.model.*;

import edu.umd.cs.findbugs.annotations.*;
import lombok.extern.slf4j.*;

/**
 * Patches {@link com.tngtech.jgiven.report.model.StepModel} in order to allow
 * correct reporting of step execution duration.
 * <p>
 * https://github.com/TNG/JGiven/issues/755
 * </p>
 *
 * <p>
 * <strong>IMPORTANT:</strong> requires having
 * <code>com.tngtech.jgiven:jgiven-core</code> as a weave dependency.
 * </p>
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@Aspect
@Slf4j
public class StepModelPatchAspect {

    /**
     * Monitors attempts to set step method's duration. If the duration is
     * already set, then overrides to do nothing.
     *
     * @param stepModel
     *            instance of {@link StepModel}
     *
     * @see #setDurationInNanos(StepModel)
     */
    @Around(value = "setDurationInNanos(stepModel)",
        argNames = "thisJoinPoint,stepModel") // for debugging info
    @SuppressWarnings("static-method")
    public void aroundSetDurationInNanos(
        final ProceedingJoinPoint thisJoinPoint,
        final StepModel stepModel) throws Throwable {
        if (0 == stepModel.getDurationInNanos())
            thisJoinPoint.proceed(); // as duration is not set, otherwise ovoid
    }

    /**
     * Matches the execution of {@link StepModel#setDurationInNanos(long)}
     *
     * @param stepModel
     *            instance of {@link StepModel}
     */
    @Pointcut("execution("
        + "void com.tngtech.jgiven.report.model.StepModel.setDurationInNanos(long))"
        + "&& target(stepModel)")
    public void setDurationInNanos(final StepModel stepModel) {
        // nothing to do here -- just defines a pointcut matcher
    }
}
