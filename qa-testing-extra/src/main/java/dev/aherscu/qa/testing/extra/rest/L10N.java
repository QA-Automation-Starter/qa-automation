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
package dev.aherscu.qa.testing.extra.rest;

import com.github.rodionmoiseev.c10n.*;
import com.github.rodionmoiseev.c10n.annotations.*;

class L10N {
    public static final Messages MESSAGES = C10N.get(Messages.class);

    @C10NMessages
    public interface Messages {

        @En("got {1} ({2}) while accessing {0}")
        String httpError(
            final String host, final String reason, final Integer code);
    }
}
