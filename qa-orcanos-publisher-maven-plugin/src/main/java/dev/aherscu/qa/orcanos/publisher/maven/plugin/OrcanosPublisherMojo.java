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

package dev.aherscu.qa.orcanos.publisher.maven.plugin;

import static dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter.*;
import static dev.aherscu.qa.testing.utils.FileUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.UriUtils.*;
import static java.text.MessageFormat.*;
import static java.util.Objects.*;
import static java.util.UUID.*;
import static java.util.regex.Pattern.*;
import static javax.ws.rs.client.Entity.*;
import static javax.ws.rs.core.MediaType.*;
import static org.apache.commons.io.IOUtils.*;
import static org.glassfish.jersey.client.ClientProperties.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import javax.ws.rs.*;
import javax.ws.rs.client.*;

import org.apache.commons.io.filefilter.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.glassfish.jersey.client.authentication.*;
import org.glassfish.jersey.client.rx.rxjava2.*;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.*;
import org.jooq.lambda.*;

import dev.aherscu.qa.orcanos.publisher.maven.plugin.model.*;
import dev.aherscu.qa.testing.utils.*;
import dev.aherscu.qa.testing.utils.rest.*;
import lombok.*;

/**
 * Orcanos {@code publish} goal should be configured as in
 * {@code plugin-config.xml} file used for testing.
 */
@SuppressWarnings("ClassWithTooManyFields")
@Mojo(name = "publish",
    threadSafe = true,
    defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class OrcanosPublisherMojo extends AbstractMojo {
    static final String GET_EXECUTION_RUN_DETAILS =
        "Get_Execution_Run_Details_XML";
    static final String PROJECT_ID                = "ProjectID";
    static final String RECORD_EXECUTION_RESULT   =
        "Record_Execution_Results_New";
    static final String UPLOAD_ATTACHMENT         = "QW_Add_MiscAttachment";

    static {
        // NOTE: enables SSL without root validation
        TrustAllX509TrustManager.disableSslCertificateValidation();
    }

    @Parameter(property = "orcanos.skip", defaultValue = "false")
    protected boolean    skip;
    @Parameter(
        defaultValue = "${project.build.directory}/jgiven-reports/qa-html")
    protected File       reportsDirectory;
    /**
     * Groups to match in report file to be published as follows:
     * <ol>
     * <li>absolute path to file</li>
     * <li>execution status</li>
     * <li>class name</li>
     * <li>method name</li>
     * </ol>
     * Currently, only the execution status is used.
     */
    @Parameter(
        defaultValue = "(.*[\\\\|/])([^\\\\|/]+)-([^\\\\|/]+)-([^\\\\|/]+)$")
    protected String     filePattern;
    /**
     * Files having this extension will be scanned and uploaded if matched
     * correctly by {@link #filePattern}.
     */
    @Parameter(defaultValue = ".html")
    protected String     reportFileExtension;
    @Parameter(required = true)
    protected URI        orcanosUrl;
    @Parameter(required = true)
    protected String     orcanosProjectId;
    @Parameter(defaultValue = "10000")
    protected int        connectTimeoutMs;
    @Parameter(defaultValue = "10000")
    protected int        readTimeoutMs;
    @Parameter(defaultValue = "3")
    protected int        retriesLimit;
    @Parameter
    protected String     tag;
    @Parameter
    protected Properties additionalExecutionFields;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("skipping");
            return;
        }

        getLog().info("publishing to Orcanos from: " + reportsDirectory);
        getLog().info("using file pattern: " + filePattern);
        getLog().info("for files ending with: " + reportFileExtension);
        getLog().info("Orcanos endpoint URL: " + orcanosUrl);
        getLog().info("Orcanos Project ID: " + orcanosProjectId);
        getLog().info("Connection timeout (ms): " + connectTimeoutMs);
        getLog().info("Read timeout (ms): " + readTimeoutMs);
        getLog().info("Retries limit: " + retriesLimit);
        getLog().info("Tag: " + tag);
        getLog().info("Additional execution fields: "
            + additionalExecutionFields);

        val compiledReferencePattern = compile(filePattern);

        try (val client = orcanosClient()) {
            listReportFiles()
                // ISSUE cannot parallelize because Orcanos reports same run;
                // the "runs" are identified with second granularity timestamp
                .stream()
                .peek(file -> getLog().info("found report: " + file.getName()))
                .map(file -> compiledReferencePattern
                    .matcher(file.getAbsolutePath()))
                .filter(Matcher::find)
                .map(Unchecked.function(matcher -> ReportHandle.builder()
                    .sourceFile(new File(matcher.group(0)))
                    .status(matcher.group(2))
                    .attributes(readAttributesOf(new File(matcher.group(0))))
                    .tag(tag)
                    .build()))
                .peek(reportHandle -> getLog()
                    .info("parsed meta-info: " + reportHandle.toString()))
                .filter(ReportHandle::hasSupportedStatus)
                .filter(ReportHandle::hasSupportedReference)
                .peek(reportHandle -> getLog().info("meta-info supported"))
                .forEach(reportHandle -> report(client.target(orcanosUrl),
                    reportHandle));
        } catch (final Throwable t) {
            throw new MojoExecutionException("configuration error", t);
        }
    }

    private Collection<File> listReportFiles() {
        val reportFiles = listFiles(reportsDirectory,
            new SuffixFileFilter(reportFileExtension),
            null);

        if (reportFiles.isEmpty())
            throw new RuntimeException("no reports to upload");

        return reportFiles;
    }

    private AutoCloseableClient orcanosClient() {
        return new AutoCloseableClient(LoggingClientBuilder
            .newClient()
            .register(RxFlowableInvokerProvider.class)
            .register(MultiPartFeature.class)
            .register(HttpAuthenticationFeature.basicBuilder()
                .credentials(
                    usernameFrom(orcanosUrl),
                    passwordFrom(orcanosUrl))
                .build())
            .property(CONNECT_TIMEOUT, connectTimeoutMs)
            .property(READ_TIMEOUT, readTimeoutMs));
    }

    private GenericResponse recordExecutionResults(
        final WebTarget orcanosEndpointTarget,
        final ReportHandle reportHandle,
        final ExecutionSetRunResultsEx results,
        final GenericResponse uploadConfirmation) {
        getLog().info(format("reporting run {0} of test case {1} "
            + "on execution set {2} with status {3}",
            requireNonNull(results.getRun(),
                "couldn't get execution run result, check orcanos credentials")
                .getName(),
            reportHandle.testId(),
            reportHandle.executionSetId(),
            reportHandle.status()));

        return orcanosEndpointTarget
            .path(RECORD_EXECUTION_RESULT)
            .request(APPLICATION_JSON)
            .rx(RxFlowableInvoker.class)
            .post(json(RecordExecutionResults.builder()
                .executionSetRunResults(results
                    .withAdditionalFields(additionalExecutionFields)
                    .withDeviceInfo(
                        reportHandle.deviceName(),
                        reportHandle.platformName(),
                        reportHandle.platformVersion())
                    .withStatus(reportHandle.status().name())
                    .withAttachment("click to open automation report",
                        uploadConfirmation.href()))
                .build()),
                GenericResponse.class)
            .retry(retriesLimit, e -> e instanceof ProcessingException)
            .blockingFirst();
    }

    private void report(
        final WebTarget orcanosEndpointTarget,
        final ReportHandle reportHandle) {

        val attachmentId = randomUUID();
        try (val formDataMultiPart = new FormDataMultiPart();
            val inputStream = openInputStream(reportHandle.sourceFile());
            val bufferedInputStream = buffer(inputStream);
            val reportToUpload =
                formDataMultiPart.bodyPart(new StreamDataBodyPart(
                    attachmentId.toString(),
                    bufferedInputStream,
                    attachmentId + reportFileExtension,
                    TEXT_HTML_TYPE))) {

            getLog().info(
                recordExecutionResults(
                    orcanosEndpointTarget,
                    reportHandle,
                    retrieveExecutionSetRunResults(orcanosEndpointTarget,
                        reportHandle),
                    uploadReport(orcanosEndpointTarget, reportToUpload))
                    .toString());
        } catch (final Throwable t) {
            getLog().error(reportHandle.toString(), t);
        }
    }

    private ExecutionSetRunResultsEx retrieveExecutionSetRunResults(
        final WebTarget orcanosEndpointTarget,
        final ReportHandle reportHandle) {
        return orcanosEndpointTarget
            .register(ExecutionSetRunResultsProvider.class)
            // ISSUE if the credentials are wrong
            // the requests still returns with 200 (success)
            // but with an "error" response body like this
            // <Error xmlns="">
            // <ErrorStatus>1</ErrorStatus>
            // <ErrorInfo>Email / User Name or password are
            // incorrect</ErrorInfo>
            // <ErrorTrace>Initialize_new</ErrorTrace>
            // </Error>
            // Perhaps the best way to deal with this is to change
            // the Execution_Set_Run_Results schema in order to allow such
            // alternative structure. It will still require a verification,
            // but will allow access to error message.
            // Meanwhile, we will just throw an NPE with a generic message,
            // see recordExecutionResults above.
            .path(GET_EXECUTION_RUN_DETAILS)
            .request(APPLICATION_XML)
            .rx(RxFlowableInvoker.class)
            .post(json(ExecutionSet.builder()
                .testId(reportHandle.testId())
                .executionSetId(reportHandle.executionSetId())
                .build()),
                ExecutionSetRunResultsEx.class)
            .retry(retriesLimit, e -> e instanceof ProcessingException)
            .blockingFirst();
    }

    private GenericResponse uploadReport(
        final WebTarget orcanosEndpointTarget,
        final MultiPart attachmentMultiPart) {
        return orcanosEndpointTarget
            .path(UPLOAD_ATTACHMENT)
            .queryParam(PROJECT_ID, orcanosProjectId)
            .request(APPLICATION_JSON)
            .rx(RxFlowableInvoker.class)
            .post(entity(attachmentMultiPart,
                attachmentMultiPart.getMediaType()),
                GenericResponse.class)
            .retry(retriesLimit, e -> e instanceof ProcessingException)
            .blockingFirst();
    }

    private static final class ExecutionSetRunResultsProvider
        extends AbstractJaxbReadableProvider<ExecutionSetRunResults> {
        // nothing special
    }
}
