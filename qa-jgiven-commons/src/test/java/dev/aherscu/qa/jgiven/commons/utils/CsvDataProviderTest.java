/*
 * Copyright 2024 Adrian Herscu
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

import static dev.aherscu.qa.jgiven.commons.utils.AbstractCsvDataProvider.*;
import static dev.aherscu.qa.testing.utils.ClassUtilsExtensions.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.startsWith;

import java.io.*;

import org.testng.annotations.*;

import com.google.common.collect.*;
import com.opencsv.bean.*;

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@Slf4j
public class CsvDataProviderTest {
    @ToString
    @SuperBuilder
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(force = true)
    public static final class MyBean {
        @RequiredArgsConstructor
        @ToString
        public enum MyEnum {
            v1_v2(ImmutableSet.of("v1", "v2")),
            other(ImmutableSet.of("other"));

            public final ImmutableSet<String> v;
        }

        @ToString
        @SuperBuilder
        @Getter
        @EqualsAndHashCode
        @NoArgsConstructor(force = true)
        public static final class InnerBean {
            @CsvBindByName
            public final String value2;
            @CsvBindByName
            public final MyEnum myEnum;
        }

        @CsvBindByName
        public final String    value1;
        @CsvRecurse
        public final InnerBean innerBean;
    }

    public static final class MyBeanCsvDataProvider
        extends AbstractCsvDataProvider {
        @Override
        protected Class<?> type() {
            return MyBean.class;
        }
    }

    @Test
    @SneakyThrows
    public void selfTest() {
        try (val csvReader = new InputStreamReader(
            getRelativeResourceAsStream(getClass(),
                csvFileFor(getClass().getMethod("selfTest"))))) {
            assertThat(
                new CsvToBeanBuilder<>(csvReader)
                    .withType(MyBean.class)
                    .build()
                    .stream(),
                anyMatch(is(MyBean.builder()
                    .value1("line2a")
                    .innerBean(MyBean.InnerBean.builder()
                        .value2("line2b")
                        .myEnum(MyBean.MyEnum.other)
                        .build())
                    .build())));
        }
    }

    // NOTE: there must be a CsvDataProviderTest.shouldReadFromCSV.csv file
    @Test(dataProviderClass = MyBeanCsvDataProvider.class, dataProvider = DATA)
    public void shouldReadFromCsv(final MyBean value) {
        assertThat(value, hasProperty("value1", startsWith("line")));
    }
}