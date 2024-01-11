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
package dev.aherscu.qa.jgiven.commons.steps;

import static dev.aherscu.qa.testing.utils.FileUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.ThreadUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.*;
import java.time.*;
import java.util.function.*;

import javax.annotation.concurrent.*;

import org.apache.commons.io.filefilter.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.base.*;

import dev.aherscu.qa.jgiven.commons.formatters.*;
import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Generic actions.
 *
 * @param <SELF>
 *            the type of the subclass
 * @param <T>
 *            type of scenario
 * @author aherscu
 */
@ThreadSafe
@Slf4j
public class GenericActions<T extends AnyScenarioType, SELF extends GenericActions<T, SELF>>
    extends StageEx<SELF>
    implements ScenarioType<T> {

    /**
     * Logs the construction of this stage.
     */
    public GenericActions() {
        log.trace("when stage {} constructed", this); //$NON-NLS-1$
    }

    /**
     * Comment to be reported.
     *
     * @param comment
     *            comment to be added to generated report
     * @return {@link #self()}
     *
     * @deprecated use {@link ScenarioTestBase#section(String)}
     */
    @Override
    @Deprecated
    public SELF comment(final String comment) {
        return self();
    }

    /**
     * Concatenates all files in specified source directory recursing into
     * sub-directories.
     *
     * @param fileFilter
     *            a file filter
     * @param sourceDirectory
     *            the source directory
     * @param targetFile
     *            the target file into which to concatenate
     * @return {@link #self()}
     */
    @SneakyThrows(IOException.class)
    public SELF concatenate_$_files_from_$_into(
        final IOFileFilter fileFilter,
        final File sourceDirectory,
        final File targetFile) {

        for (val sourceFile : listFiles(
            sourceDirectory, fileFilter, FileFilterUtils.trueFileFilter())) {
            log.debug("concatenating {} into {}", sourceFile, targetFile); //$NON-NLS-1$
            append(sourceFile, targetFile);
        }

        return self();
    }

    /**
     * Deletes directory from specified path.
     *
     * @param directory
     *            the directory path
     * @return {@link #self()}
     */
    @SneakyThrows(IOException.class)
    public SELF deleting_directory(final File directory) {
        log.debug("deleting directory {}", directory.toString()); //$NON-NLS-1$
        deleteDirectory(directory);
        return self();
    }

    /**
     * Does nothing.
     *
     * @return {@link #self()}
     */
    public SELF doing_nothing() {
        return doing_nothing(0);
    }

    /**
     * Does nothing for specified duration.
     *
     * @param millis
     *            how many milliseconds to do nothing
     * @return {@link #self()}
     */
    @SuppressWarnings("boxing")
    public SELF doing_nothing(
        @UnitFormatter.Annotation("ms") final long millis) {
        log.debug("sleeping {} millis", millis);
        sleep(millis);
        return self();
    }

    /**
     * Does nothing for specified duration.
     *
     * @param duration
     *            the duration to do nothing
     * @return {@link #self()}
     */
    @NestedSteps
    public SELF doing_nothing(final Duration duration) {
        return doing_nothing(duration.toMillis());
    }

    /**
     * Failing with specified throwable for self-testing purposes.
     *
     * @param throwable
     *            the throwable
     * @return {@link #self()}
     */
    @SneakyThrows
    public SELF failing_on_purpose_with(final Throwable throwable) {
        log.debug("about to throw {}", throwable.toString());
        throw throwable;
    }

    /**
     * Retries specified step according to pre-configured {@link #retryPolicy}.
     *
     * @param step
     *            the step to retry
     * @return {@link #self()}
     * @see #beforeScenarioConfigurePolling()
     */
    public final SELF retrying(final Function<SELF, SELF> step) {
        return retrying(new StepWithDescription<>(EMPTY, step));
    }

    /**
     * Retries specified step according to pre-configured {@link #retryPolicy}.
     *
     * @param step
     *            the step to retry
     * @return {@link #self()}
     * @see #beforeScenarioConfigurePolling()
     */
    public final SELF retrying(final StepWithDescription<SELF> step) {
        return retry(() -> step.apply(self()));
    }

    /**
     * Safely executes specified step, swallowing all exceptions.
     *
     * <p>
     * <strong>IMPORTANT:</strong>must not be called from step method that is
     * annotated with {@link NestedSteps} since this interferes with exception
     * handling.
     * </p>
     *
     * @param step
     *            the step to execute
     * @return {@link #self()}
     */
    @Hidden
    public final SELF safely(final Function<SELF, SELF> step) {
        return safely(new StepWithDescription<>(EMPTY, step));
    }

    /**
     * Safely executes specified step, swallowing all exceptions.
     *
     * <p>
     * <strong>IMPORTANT:</strong>must not be called from step method that is
     * annotated with {@link NestedSteps} since this interferes with exception
     * handling.
     * </p>
     *
     * @param step
     *            the step to execute
     * @return {@link #self()}
     */
    @Hidden
    public final SELF safely(final StepWithDescription<SELF> step) {
        return safely(() -> step.apply(self()));
    }
}
