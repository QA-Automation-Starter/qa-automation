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

package dev.aherscu.qa.testrail.reporter;

import com.samskivert.mustache.*;
import com.tngtech.jgiven.report.model.*;

import dev.aherscu.qa.jgiven.reporter.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;

@SuperBuilder
@Slf4j
public class TestRailReportModel extends QaJGivenReportModel<ScenarioModel> {

    // ISSUE not called by Mustache template engine
    public final Mustache.Lambda save = (frag, out) -> {
        log.trace(">>>>>>>>>>>>>>");
        out.write(frag.execute().hashCode());
    };

    // ISSUE tried to debug, but @SuperBuilder already generates a constructor
    // public TestRailReportModel(final TestRailReportModelBuilder b) {
    // super(b);
    // log.trace("using TestRail report model");
    // }
}
