/*
 * Copyright 2022 Adrian Herscu
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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import com.google.common.collect.*;

public class SessionNameTest {
    private final Multimap<SessionName, String> sessions =
        // synchronization not really needed here; just to see if it works with
        Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @BeforeClass
    public void beforeClassInitializeSessions() {
        // drivers created during BeforeClass
        sessions.put(SessionName.from("C1:::I1:T1"), "session1");
        sessions.put(SessionName.from("C1:::I1:T2"), "session2");
        sessions.put(SessionName.from("C2:::I1:T3"), "session3");
        // drivers created during method
        sessions.put(SessionName.from("C2:M1:P1:I1:T4"), "session4");
        sessions.put(SessionName.from("C2:M1:P2:I1:T5"), "session5");
        sessions.put(SessionName.from("C2:M1:P2:I1:T6"), "session6");
        // the lookup is always on method completion either succeeded or failed
        // hence:
        // C1:M1:P1:I1: -> [session1, session2]
        // C2:M1:P1:I1: -> [session3, session4]
        // C2:M1:P2:I1: -> [session3, session5, session6]
    }

    @Test
    public void shouldBuildSessionName() {
        assertThat(SessionName.builder()
            .className("C")
            .methodName("M")
            .params("P")
            .id("I")
            .timestamp("T")
            .build()
            .toString(),
            is("C:M:P:I:T"));
    }

    @Test
    public void shouldFindClassSessions() {
        assertThat(
            sessions.get(SessionName.from("C1:M1:P1:I1:").asClassSession()),
            contains("session1", "session2"));
    }

    @Test
    public void shouldFindMultipleMethodSessions() {
        assertThat(sessions.get(SessionName.from("C2:M1:P2:I1:")),
            contains("session5", "session6"));
    }

    @Test
    public void shouldFindSingleMethodSessions() {
        assertThat(sessions.get(SessionName.from("C2:M1:P1:I1:")),
            contains("session4"));
    }

    @Test
    public void shouldParseSessionName() {
        assertThat(SessionName.from("C:M:P:I:").toString(),
            is(SessionName.builder()
                .className("C")
                .methodName("M")
                .params("P")
                .id("I")
                .build()
                .toString()));
    }

    @Test
    public void shouldRegisterAllSessions() {
        assertThat(sessions.values(),
            containsInAnyOrder("session1", "session2", "session3",
                "session4", "session5", "session6"));
    }
}
