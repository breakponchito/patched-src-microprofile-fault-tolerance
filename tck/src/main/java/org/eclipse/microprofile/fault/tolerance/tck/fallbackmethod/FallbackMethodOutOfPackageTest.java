/*
 *******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.microprofile.fault.tolerance.tck.fallbackmethod;

import org.eclipse.microprofile.fault.tolerance.tck.fallbackmethod.beans.FallbackMethodOutOfPackageBeanA;
import org.eclipse.microprofile.fault.tolerance.tck.fallbackmethod.beans.subpackage.FallbackMethodOutOfPackageBeanB;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

/**
 * Test that a package scoped fallback method in a superclass from a different package cannot be called
 */
public class FallbackMethodOutOfPackageTest extends Arquillian {

    @Deployment
    @ShouldThrowException()
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "ftFallbackMethodOutOfPackage.jar")
                .addClasses(FallbackMethodOutOfPackageBeanA.class, FallbackMethodOutOfPackageBeanB.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "ftFallbackMethodOutOfPackage.war")
                .addAsLibrary(testJar);
        return war;
    }

    @Test
    public void fallbackMethodOutOfPackage() {
        // Nothing to test, deployment should throw exception
    }

}
