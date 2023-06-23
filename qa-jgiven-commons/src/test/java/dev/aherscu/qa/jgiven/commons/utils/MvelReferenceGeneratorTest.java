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

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mvel2.templates.TemplateCompiler.*;
import static org.mvel2.templates.TemplateRuntime.*;

import org.mvel2.*;
import org.testng.annotations.*;

import com.google.common.collect.*;

import dev.aherscu.qa.testing.utils.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;

public class MvelReferenceGeneratorTest {

    @Test
    public void shouldGenerateDescription() {
        System.setProperty("reference.templatelink",
            "@{StringUtilsExtensions.format("
                + "'<a target=\"_blank\" "
                + "href=\"https://somehost?item={0}\">"
                + "Click here to open {0}</a>', "
                + "'(\\\\d+)_(\\\\d+)', "
                + "value)}");
        assertThat(
            new ReferenceTagDescriptionGenerator()
                .generateDescription(null, null, "123_456"),
            is("<a target=\"_blank\" "
                + "href=\"https://somehost?item=123\">"
                + "Click here to open 123</a>"));
    }

    @Test
    public void shouldGenerateHref() {
        System.setProperty("reference.href",
            "@{StringUtilsExtensions.format("
                + "'https://somehost?item={0}', "
                + "'(\\\\d+)_(\\\\d+)', "
                + "value)}");
        assertThat(
            new ReferenceHrefGenerator()
                .generateHref(null, null, "123_456"),
            is("https://somehost?item=123"));
    }

    @Test
    public void shouldGenerateStringFromTemplate() {
        val template = "@{1+1}";
        val compiled = compileTemplate(template);
        assertThat(execute(compiled).toString(), is("2"));
    }

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "easier to read with small performance impact")
    @Test
    public void shouldGenerateStringFromTemplate2() {
        val template =
            "@{StringUtilsExtensions.format('{0}->{1}', '(\\\\d+)_(\\\\d+)', reference)}";
        val reference = "1323_89837";

        @java.lang.SuppressWarnings("serial")
        val compiled = compileTemplate(template, new ParserContext() {
            {
                addImport(StringUtilsExtensions.class.getSimpleName(),
                    StringUtilsExtensions.class);
            }
        });
        assertThat(
            execute(compiled,
                ImmutableMap.builder()
                    .put("reference", reference)
                    .build())
                .toString(),
            is(format("{0}->{1}", "(\\d+)_(\\d+)",
                reference)));
    }
}
