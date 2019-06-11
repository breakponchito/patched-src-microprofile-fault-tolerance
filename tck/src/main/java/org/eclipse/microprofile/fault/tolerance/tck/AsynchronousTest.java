/*
 *******************************************************************************
 * Copyright (c) 2016-2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.eclipse.microprofile.fault.tolerance.tck;

import javax.inject.Inject;

import org.eclipse.microprofile.fault.tolerance.tck.asynchronous.AsyncClassLevelClient;
import org.eclipse.microprofile.fault.tolerance.tck.asynchronous.AsyncClient;
import org.eclipse.microprofile.fault.tolerance.tck.asynchronous.CompletableFutureHelper;
import org.eclipse.microprofile.fault.tolerance.tck.util.Connection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Verify the asynchronous invocation
 */
public class AsynchronousTest extends Arquillian {

    private @Inject
    AsyncClient client;

    private @Inject
    AsyncClassLevelClient clientClass;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "ftAsynchronous.jar")
            .addClasses(AsyncClient.class, AsyncClassLevelClient.class, Connection.class, CompletableFutureHelper.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class, "ftAsynchronous.war").addAsLibrary(testJar);
    }


    /**
     * Test that the future returned by calling an asynchronous method is not done if called right after the operation
     */
    @Test
    public void testAsyncIsNotFinished() {
        CompletableFuture<Void> waitingFuture = newWaitingFuture();
        Future<Connection> future = client.service(waitingFuture);
        Assert.assertFalse(future.isDone());
    }

    /**
     * Test that the future returned by calling an asynchronous method is done if called after waiting enough time to end the operation
     */
    @Test
    public void testAsyncIsFinished() {
        CompletableFuture<Void> waitingFuture = newWaitingFuture();
        Future<Connection> future = client.service(waitingFuture);
        await().atLeast(1000, TimeUnit.MILLISECONDS).atMost(2000, TimeUnit.MILLISECONDS)
            .untilAsserted(()-> Assert.assertTrue(future.isDone()));
    }


    /**
     * Test that the future returned by calling a method in an asynchronous class is not done if called right after the operation
     */
    @Test
    public void testClassLevelAsyncIsNotFinished() {
        CompletableFuture<Void> waitingFuture = newWaitingFuture();
        CompletionStage<Connection> resultFuture = clientClass.serviceCS(waitingFuture);
        Future<Connection> future = resultFuture.toCompletableFuture();
        await().atMost(400, TimeUnit.MILLISECONDS).untilAsserted(()-> Assert.assertFalse(future.isDone()));
    }

    /**
     * Test that the future returned by calling a method in an asynchronous class is done if called after waiting enough time to end the operation
     */
    @Test
    public void testClassLevelAsyncIsFinished() {
        CompletableFuture<Void> waitingFuture = newWaitingFuture();
        CompletionStage<Connection> resultFuture = clientClass.serviceCS(waitingFuture);
        Future<Connection> future = resultFuture.toCompletableFuture();
        await().atLeast(1000, TimeUnit.MILLISECONDS).untilAsserted(()-> Assert.assertTrue(future.isDone()));
    }

    /**
     * Use this method to obtain futures for passing to methods on
     * {@link AsyncClient}
     * <p>
     * Using this factory method ensures they will be completed at the end of
     * the test if your test fails.
     */
    private CompletableFuture<Void> newWaitingFuture() {
        return new CompletableFuture<>();
    }
}
