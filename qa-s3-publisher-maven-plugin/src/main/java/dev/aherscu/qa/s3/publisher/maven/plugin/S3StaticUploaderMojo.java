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

package dev.aherscu.qa.s3.publisher.maven.plugin;

import java.io.*;
import java.lang.SuppressWarnings;
import java.util.*;

import org.apache.maven.plugin.*;

import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;

import dev.aherscu.qa.s3.publisher.maven.plugin.config.*;
import dev.aherscu.qa.s3.publisher.maven.plugin.util.*;
import edu.umd.cs.findbugs.annotations.*;

/**
 * @prefix s3-static-uploader
 * @requiresProject true
 * @requiresOnline true
 * @goal upload
 * @phase prepare-package
 * @description Uploads static site content to AWS S3
 */
@SuppressWarnings("ClassWithTooManyFields")
public class S3StaticUploaderMojo extends AbstractMojo {

    public static final String S3_URL = "s3.amazonaws.com";

    /**
     * @parameter property="accessKey"
     * @required
     */
    private String             accessKey;

    /**
     * @parameter property="secretKey"
     * @required
     */
    private String             secretKey;

    /**
     * @parameter property="bucketName"
     * @required
     */
    private String             bucketName;

    /**
     * List of {@link Include} objects matching files to include.
     *
     * @parameter
     */
    private List<Include>      includes;

    /**
     * List of expressions matching files to exclude. Could be regular
     * expressions using the expression %regex[].
     *
     * @parameter
     */
    private List<String>       excludes;

    /**
     * List of {@link Metadata} objects defining metadata for files to include.
     *
     * @parameter
     */
    private List<Metadata>     metadatas;

    /**
     * The directory where the webapp is built.
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    private File               outputDirectory;

    /**
     * Single directory for extra files to include in the WAR
     *
     * @parameter default-value="${basedir}/src/main/webapp"
     * @required
     */
    private File               inputDirectory;

    /**
     * Directory to encode files before uploading
     *
     * @parameter default-value="${project.build.directory}/temp"
     * @required
     */
    private File               tmpDirectory;

    /**
     * Determines if the plugin will update Last-Modified and Expires headers
     * for those remote objects which remain unchanged but contain metadata with
     * expired timestamps. This is useful if you want to enlarge cache lifetime
     * for unchanged objects when you deploy new versions of the site.
     *
     * @parameter default-value= false
     */
    private boolean            refreshExpiredObjects;

    @SuppressFBWarnings(
        value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
        justification = "limitation of Maven container")
    private S3Uploader         uploader;

    @Override
    public void execute() throws MojoExecutionException {
        logParameters();
        validateParameters();

        uploader = new S3Uploader(
            new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)),
            getLog(), buildContentEncodersList(), bucketName, inputDirectory,
            tmpDirectory,
            refreshExpiredObjects);
        final List<ManagedFile> managedFiles = getManagedFiles();
        processManagedFiles(managedFiles);
    }

    private List<ManagedFileContentEncoder> buildContentEncodersList() {
        final List<ManagedFileContentEncoder> contentEncoders =
            new ArrayList<>();
        contentEncoders
            .add(new ManagedFileContentEncoderGZipImpl(tmpDirectory));
        contentEncoders.add(new ManagedFileContentEncoderPlainImpl());
        return contentEncoders;
    }

    private List<ManagedFile> getManagedFiles() throws MojoExecutionException {
        try {
            getLog().debug("determining files that should be uploaded");
            getLog().debug("");
            final IncludedFilesListBuilder includedFilesListBuilder =
                new IncludedFilesListBuilder(inputDirectory, includes, excludes,
                    metadatas);
            return includedFilesListBuilder.build();

        } catch (final IOException e) {
            throw new MojoExecutionException(
                "cannot determine the files to be processed", e);
        }
    }

    private void logParameters() {
        getLog().debug("tmpDirectory " + tmpDirectory.getPath());
        getLog().debug("inputDirectory " + inputDirectory.getPath());
        getLog().debug("outputDirectory " + outputDirectory.getPath());
        getLog().debug("includes " + includes);
        getLog().debug("excludes " + excludes);
        getLog().debug("metadatas " + metadatas);
    }

    private void processManagedFile(final ManagedFile managedFile)
        throws MojoExecutionException {
        getLog().debug("start processing file " + managedFile.getFilename()
            + " with metadata " + managedFile.getMetadata().toString());
        try {
            uploader.uploadManagedFile(managedFile);
        } catch (final Exception e) {
            throw new MojoExecutionException(
                "cannot process file " + managedFile.getFilename(), e);
        }
        getLog().debug("finnish processing file " + managedFile.getFilename());
        getLog().debug("");
    }

    private void processManagedFiles(final List<ManagedFile> managedFiles)
        throws MojoExecutionException {
        for (final ManagedFile managedFile : managedFiles) {
            processManagedFile(managedFile);
        }
    }

    private void validateParameters() throws MojoExecutionException {
        try {
            final ParametersValidator validator =
                new ParametersValidator(includes, metadatas);
            validator.validate();
        } catch (final Exception e) {
            throw new MojoExecutionException(
                "error found validating configuration", e);
        }
    }
}
