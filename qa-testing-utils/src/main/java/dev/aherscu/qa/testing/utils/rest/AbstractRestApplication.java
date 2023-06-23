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
package dev.aherscu.qa.testing.utils.rest;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.reflections.*;

import lombok.extern.slf4j.*;

/**
 * Holds the classes comprising a REST application.
 * 
 * @author aherscu
 *
 */
@Slf4j
public abstract class AbstractRestApplication extends Application {

    private final Set<Class<?>> classes;

    /**
     * Sets up an REST application by scanning a given package for {@link Path}
     * annotated classes.
     * 
     * @param packageName
     *            the name of the package to scan
     */
    protected AbstractRestApplication(final String packageName) {
        log.debug("initiating REST application for package {}", packageName); //$NON-NLS-1$
        classes = new Reflections(packageName)
            .getTypesAnnotatedWith(Path.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(classes);
    }

}
