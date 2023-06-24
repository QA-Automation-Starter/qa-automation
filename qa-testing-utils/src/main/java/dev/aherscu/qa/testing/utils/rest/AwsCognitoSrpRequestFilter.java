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

import static javax.ws.rs.core.HttpHeaders.*;

import javax.annotation.concurrent.*;
import javax.ws.rs.client.*;

import lombok.*;

/**
 * Adds an AWS Cognito token to each request.
 *
 * <p>
 * NOTE: this is a naive implementation; the token is not saved for subsequent
 * requests. May have performance implications.
 * </p>
 *
 * @see <a href=
 *      "https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-authentication-flow.html">User
 *      Pool Authentication Flow</a>
 * @author aherscu
 *
 */
@ThreadSafe
@AllArgsConstructor
@ToString
public class AwsCognitoSrpRequestFilter implements ClientRequestFilter {

    private final AwsCognitoSrpAuthenticator srpAuthenticationClient;
    private final String                     username, password;

    @Override
    public final void filter(final ClientRequestContext requestContext) {
        requestContext
            .getHeaders()
            .add(AUTHORIZATION,
                srpAuthenticationClient
                    .authChallengeResult(username, password)
                    .getAuthenticationResult()
                    .getIdToken());
    }
}
