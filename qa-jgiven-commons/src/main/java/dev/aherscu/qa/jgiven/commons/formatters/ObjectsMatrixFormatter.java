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
package dev.aherscu.qa.jgiven.commons.formatters;

import static dev.aherscu.qa.testing.utils.StringUtilsExtensions.*;

import java.lang.annotation.*;
import java.util.*;

import javax.annotation.concurrent.*;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.*;

/**
 * Formats a two dimensional array of {@link Object}s such that each row appears
 * on separate line and each cell is separated from next one by a comma.
 *
 * @author aherscu
 *
 */
@ThreadSafe
public class ObjectsMatrixFormatter implements
    ArgumentFormatter<Object[][]> {

    @Override
    public String format(
        final Object[][] argumentToFormat,
        final String... annotationArguments) {
        final List<String> rows = new LinkedList<>();
        for (final Object[] row : argumentToFormat) {
            final List<String> cells = new LinkedList<>();
            for (final Object cell : row) {
                cells.add(
                    abbreviateMiddle(
                        Objects.toString(cell), ELLIPSIS,
                        Integer.parseInt(annotationArguments[0])));
            }
            rows.add(join(cells, COMMA));
        }
        return join(rows, CR + LF);
    }

    /**
     * Formatter annotation for {@link Object}[][] objects.
     *
     * <p>
     * The length of each cell can be controlled by adding it as an argument,
     * like: {@code @ObjectsMatrixFormatter.Annotation(args={"30"})}; the
     * default is 20.
     * </p>
     *
     * <p>
     * If the argument cannot be parsed by {@link Integer#parseInt(String)},
     * then a {@link NumberFormatException} will occur.
     * </p>
     *
     * @author aherscu
     */
    @Format(value = ObjectsMatrixFormatter.class, args = { "20" })
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    public @interface Annotation {
        /**
         * @return the arguments defined
         */
        String[] args() default { "20" };
    }
}
