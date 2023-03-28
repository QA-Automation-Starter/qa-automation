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

package dev.aherscu.qa.tester.utils;

import static dev.aherscu.qa.tester.utils.FileUtilsExtensions.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.io.*;
import java.nio.charset.*;

import org.testng.annotations.*;

import com.samskivert.mustache.*;

import lombok.*;

@SuppressWarnings({ "javadoc", "static-method" })
@Test
public class TemplateTest {

    public static final String MUSTACHE_OUT = "mustache.out";

    public void shouldRunLambdasFromSubclass() {
        class Model {
            final String data = "kuku";
        }

        class ModelEx extends Model {
            final Mustache.Lambda lambda =
                (frag, out) -> out.write(">>" + frag.execute());
        }

        assertThat(Mustache.compiler()
            .compile("{{#lambda}}{{data}}{{/lambda}}")
            .execute(new ModelEx())) // works the same even if cast into Model
                .isEqualTo(">>kuku");
    }

    public void shouldMapFlatObjectViaTemplate() {
        assertThat(TemplateUtils
            .using(Mustache.compiler())
            .loadFrom("flat-object.mustache")
            .execute(FlatObject.builder()
                .id(7)
                .data("blah")
                .build()))
                    .isEqualTo("7-blah");
    }

    public void shouldMapHierarchicalObjectViaTemplate() {
        assertThat(TemplateUtils
            .using(Mustache.compiler())
            .loadFrom("hierarchical-object.mustache")
            .execute(HierarchicalObject.builder()
                .id(7)
                .data(FlatObject.builder()
                    .id(3)
                    .data("trah")
                    .build())
                .build()))
                    .isEqualTo("7-3-trah");
    }

    @SneakyThrows(IOException.class)
    public void shouldWriteToFile() {
        try (val writer = fileWriter(new File(MUSTACHE_OUT))) {
            TemplateUtils
                .using(Mustache.compiler())
                .loadFrom("flat-object.mustache")
                .execute(FlatObject.builder()
                    .id(7)
                    .data("blah")
                    .build(),
                    writer);
        }

        assertThat(
            readFileToString(new File(MUSTACHE_OUT), StandardCharsets.UTF_8))
                .isEqualTo("7-blah");
    }

    @Builder
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
        justification = "referenced from JMustache template at runtime")
    public static final class FlatObject {
        public final int    id;
        public final String data;
    }

    @Builder
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
        justification = "referenced from JMustache template at runtime")
    public static final class HierarchicalObject {
        public final int         id;
        @Getter
        private final FlatObject data;
    }
}
