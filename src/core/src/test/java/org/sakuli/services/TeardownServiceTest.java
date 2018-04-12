/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.services;

import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.testng.annotations.Test;

import java.util.Optional;

public class TeardownServiceTest {

    private TeardownService testling = () -> 0;

    @Test
    public void testTearDownNoCalls() {
        //if some method would be called the default exception will be thrown
        testling.tearDown(Optional.empty());
        testling.tearDown(Optional.of(new AbstractTestDataEntity() {
            @Override
            public void refreshState() {
                //stub
            }
        }));
    }

    @Test(expectedExceptions = SakuliRuntimeException.class,
            expectedExceptionsMessageRegExp = "Method 'teardownTestSuite' is not implemented.*")
    public void testTeardownTestSuite() {
        testling.tearDown(Optional.of(new TestSuite()));
    }

    @Test(expectedExceptions = SakuliRuntimeException.class,
            expectedExceptionsMessageRegExp = "Method 'teardownTestCase' is not implemented.*")
    public void testTeardownTestCase() {
        testling.tearDown(Optional.of(new TestCase("test", "test")));
    }

    @Test(expectedExceptions = SakuliRuntimeException.class,
            expectedExceptionsMessageRegExp = "Method 'teardownTestCaseStep' is not implemented.*")
    public void testTeardownTestCaseStep() {
        testling.tearDown(Optional.of(new TestCaseStep()));
    }
}