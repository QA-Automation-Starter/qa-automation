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

package dev.aherscu.qa.jgiven.reporter.maven.plugin;

import java.io.*;
import java.util.*;

import org.apache.maven.*;
import org.apache.maven.execution.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.testing.*;
import org.apache.maven.project.*;
import org.eclipse.aether.*;
import org.eclipse.aether.internal.impl.*;
import org.eclipse.aether.repository.*;

import lombok.*;

/**
 * Use this as you would {@link AbstractMojoTestCase}, where you want more of
 * the standard maven defaults to be set (and where the
 * {@link AbstractMojoTestCase} leaves them as null or empty). This includes:
 * <ul>
 * <li>local repo, repo sessions and managers configured
 * <li>maven default remote repos installed (NB: this does not use your ~/.m2
 * local settings)
 * <li>system properties are copies
 * </ul>
 * <p>
 * No changes to subclass code is needed; this simply intercepts the
 * {@link #newMavenSession(MavenProject)} method used by the various
 * {@link #lookupMojo(String, File)} methods.
 * <p>
 * This also provides new methods, {@link #newMavenSession()} to conveniently
 * create a maven session, and {@link #lookupConfiguredMojo(String, File)} so
 * you don't have to always build the project yourself.
 */

// from
// https://github.com/ahgittin/license-audit-maven-plugin/blob/master/src/test/java/org/heneveld/maven/license_audit/BetterAbstractMojoTestCase.java
public abstract class BetterAbstractMojoTestCase extends AbstractMojoTestCase {

    protected static final String PLUGIN_CONFIG = "plugin-config.xml";
    protected static final File   OUT_DIR       =
        new File(getBasedir(), "target/test-classes");

    protected static File outdir(final String relativePath) {
        return new File(OUT_DIR, relativePath);
    }

    @SneakyThrows
    protected MavenSession newMavenSession() {
        val request = new DefaultMavenExecutionRequest();
        // populate sensible defaults, including repository basedir and
        // remote repos
        val populator = getContainer()
            .lookup(MavenExecutionRequestPopulator.class);
        populator.populateDefaults(request);

        // this is needed to allow java profiles to get resolved; i.e. avoid
        // during project builds:
        // [ERROR] Failed to determine Java version for profile
        // java-1.5-detected @ org.apache.commons:commons-parent:22,
        // /Users/alex/.m2/repository/org/apache/commons/commons-parent/22/commons-parent-22.pom,
        // line 909, column 14
        request.setSystemProperties(System.getProperties());

        // and this is needed so that the repo session in the maven session
        // has a repo manager, and it points at the local repo
        // (cf MavenRepositorySystemUtils.newSession() which is what is
        // otherwise done)
        val maven =
            (DefaultMaven) getContainer().lookup(Maven.class);
        val repoSession =
            (DefaultRepositorySystemSession) maven
                .newRepositorySession(request);
        repoSession.setLocalRepositoryManager(
            new SimpleLocalRepositoryManagerFactory().newInstance(
                repoSession,
                new LocalRepository(
                    request.getLocalRepository().getBasedir())));

        @SuppressWarnings("deprecation")
        val session = new MavenSession(getContainer(),
            repoSession,
            request, new DefaultMavenExecutionResult());
        return session;
    }

    /**
     * Extends the super to use the new {@link #newMavenSession()} introduced
     * here which sets the defaults one expects from maven; the standard test
     * case leaves a lot of things blank
     */
    @Override
    protected MavenSession newMavenSession(MavenProject project) {
        MavenSession session = newMavenSession();
        session.setCurrentProject(project);
        session.setProjects(Collections.singletonList(project));
        return session;
    }

    protected Mojo lookupConfiguredMojo(String goal) {
        return lookupConfiguredMojo(goal, outdir(PLUGIN_CONFIG));
    }

    /**
     * As {@link #lookupConfiguredMojo(MavenProject, String)} but taking the pom
     * file and creating the {@link MavenProject}.
     * 
     * @param goal
     *            goal
     * @param pom
     *            pom
     * @return mojo
     */
    @SneakyThrows
    protected Mojo lookupConfiguredMojo(String goal, File pom) {
        assertNotNull(pom);
        assertTrue(pom.exists());

        ProjectBuildingRequest buildingRequest =
            newMavenSession().getProjectBuildingRequest();
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project =
            projectBuilder.build(pom, buildingRequest).getProject();

        return lookupConfiguredMojo(project, goal);
    }
}
