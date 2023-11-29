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

package dev.aherscu.qa.jgiven.jdbc.steps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;

import com.tngtech.jgiven.annotation.*;

import dev.aherscu.qa.jgiven.commons.steps.*;
import dev.aherscu.qa.jgiven.jdbc.model.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JdbcVerifications<SELF extends JdbcVerifications<SELF>>
    extends GenericVerifications<JdbcScenarioType, SELF> {

    @ExpectedScenarioState
    public final ThreadLocal<Integer>        result    = new ThreadLocal<>();

    @ExpectedScenarioState
    public final ThreadLocal<List<Object[]>> resultSet = new ThreadLocal<>();

    // public SELF the_result_matches(final Matcher<Stream<Object[]>> matcher) {
    // // TODO implement
    // return self();
    // }

    // DELETEME after implementing streaming as in above method
    public SELF the_result_matches(final Object[][] expected) {
        log.debug("verifying result-set");
        assertThat(resultSet.get()
            .toArray(new Object[resultSet.get().size()][]),
            is(expected));
        return self();
    }
}
