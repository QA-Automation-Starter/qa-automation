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

import static org.mvel2.templates.TemplateCompiler.*;
import static org.mvel2.templates.TemplateRuntime.*;

import java.util.*;

import org.mvel2.*;
import org.mvel2.templates.*;

import com.google.common.collect.*;

import dev.aherscu.qa.testing.utils.*;
import edu.umd.cs.findbugs.annotations.*;

public abstract class MvelReferenceGenerator {

    protected final CompiledTemplate compiled(final String template) {
        return compileTemplate(template, parserContext());
    }

    protected Map<Object, Object> contextFor(final Object value) {
        return ImmutableMap.builder()
            .put("value", value)
            .build();
    }

    protected final String generateWith(final Object value) {
        return execute(compiled(template()), contextFor(value)).toString();
    }

    @java.lang.SuppressWarnings("serial")
    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "easier to read with small performance impact")
    protected ParserContext parserContext() {
        return new ParserContext() {
            {
                addImport(StringUtilsExtensions.class.getSimpleName(),
                    StringUtilsExtensions.class);
            }
        };
    }

    protected abstract String template();
}
