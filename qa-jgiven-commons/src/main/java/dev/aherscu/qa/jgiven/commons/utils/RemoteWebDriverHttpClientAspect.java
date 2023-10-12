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

import static dev.aherscu.qa.testing.utils.ExecutorUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.jooq.lambda.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.http.*;
import org.testng.*;

import com.google.common.util.concurrent.*;

import dev.aherscu.qa.testing.utils.*;
import edu.umd.cs.findbugs.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;

/**
 * Limits Selenium's remote low-level HTTP requests to configured by
 * {@code poll.timeout} system property (in seconds). This is required in order
 * to deal with remote requests hanging indefinitely.
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@Aspect
@Slf4j
public class RemoteWebDriverHttpClientAspect implements ISuiteListener {
    /**
     * Instruments {@link HttpClient#execute(HttpRequest)}.
     *
     * @param thisJoinPoint
     *            the execution
     *
     * @return the {@link HttpResponse}
     * @throws Throwable
     *             any of below
     * @throws TimeoutException
     *             if the time limit is reached
     * @throws InterruptedException
     *             if the current thread was interrupted during execution
     * @throws ExecutionException
     *             if a checked exception was thrown
     * @throws UncheckedExecutionException
     *             if a {@code RuntimeException} was thrown
     * @throws ExecutionError
     *             if an {@code Error} was thrown
     */
    @Around("executeHttpRequestMethod()")
    public Object aroundExecuteHttpRequestMethod(
        final ProceedingJoinPoint thisJoinPoint)
        throws Throwable {
        // NOTE: HttpClient#execute(HttpRequest) has exactly one argument
        val httpRequest = (HttpRequest) thisJoinPoint.getArgs()[0];
        log.trace("executing {} {}",
            httpRequest.getMethod(), httpRequest.getUri());
        try {
            val httpResponse =
                shouldExecuteWithoutTimeout(httpRequest)
                    ? (HttpResponse) thisJoinPoint
                        .proceed(thisJoinPoint.getArgs())
                    : timeout(Unchecked
                        .callable(() -> (HttpResponse) thisJoinPoint
                            .proceed(thisJoinPoint.getArgs())),
                        shouldExecuteFast(httpRequest)
                            ? StageEx.pollTimeout.dividedBy(2)
                            : StageEx.pollTimeout);
            log.trace("executed {} {} -> {}",
                httpRequest.getMethod(), httpRequest.getUri(),
                httpResponse.getStatus());
            return httpResponse;
        } catch (final ExecutionException | UncheckedExecutionException e) {
            log.error("failed due to ", e.getCause());
            throw e.getCause();
        } catch (final TimeoutException e) {
            log.error("timed-out");
            // NOTE possible error detection:
            // send a status request with a few seconds timeout
            // if it does not return throw a TestRuntimeException
            // val instance = thisJoinPoint.getThis();
            // val clientField = instance.getClass().getDeclaredField("client");
            // val baseUrlField =
            // instance.getClass().getDeclaredField("baseUrl");
            // clientField.setAccessible(true);
            // baseUrlField.setAccessible(true);
            // val client = (okhttp3.OkHttpClient) clientField.get(instance);
            // val baseUrl = (URL) baseUrlField.get(instance);
            // val response = client.newCall(new Request.Builder()
            // .url(UriBuilder.fromUri(baseUrl.toURI()).path("/status").build()
            // .toURL())
            // .build())
            // .execute();
            // val statusReady = new JSONObject(response.body().string())
            // .getBoolean("ready");
            throw e;
        }
    }

    /**
     * Marks the execution of {@link HttpClient#execute(HttpRequest)}, which is
     * the low level SPI of Selenium's RemoteWebDriver HTTP connectivity.
     */
    @Pointcut("execution("
        + "org.openqa.selenium.remote.http.HttpResponse "
        + "org.openqa.selenium.remote.http.HttpClient+.execute("
        + "org.openqa.selenium.remote.http.HttpRequest))")
    public void executeHttpRequestMethod() {
        // nothing to do here -- just defines a pointcut matcher
    }

    @Override
    public void onStart(final ISuite suite) {
        log.trace("using time-out of {}", StageEx.pollTimeout);
    }

    @Override
    public void onFinish(final ISuite suite) {
        log.trace("shutting down executor serivce");
        ExecutorUtils.EXECUTOR_SERVICE.shutdownNow();
    }

    private boolean shouldExecuteFast(final HttpRequest httpRequest) {
        switch (httpRequest.getMethod()) {
        case GET:
            return true;
        case DELETE:
            return false;
        case POST:
            return httpRequest.getUri().endsWith("/element")
                || httpRequest.getUri().endsWith("/elements");
        // TODO should somehow detect scrolling scripts
        // || httpRequest.getUri().contains("/execute/")
        // && httpRequest.get;
        default:
            throw new InvalidArgumentException(
                httpRequest.getMethod() + SPACE + httpRequest.getUri());
        }
    }

    private boolean shouldExecuteWithoutTimeout(final HttpRequest httpRequest) {
        return "/session".equals(httpRequest.getUri());
    }
}
