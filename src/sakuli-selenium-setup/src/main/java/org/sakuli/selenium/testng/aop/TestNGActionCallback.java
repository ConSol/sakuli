/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
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

package org.sakuli.selenium.testng.aop;

import org.sakuli.datamodel.TestCase;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.loader.ActionLoaderCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Call for some Java based action handling.
 *
 * @author tschneck Date: 17.10.13
 */
@Component
public class TestNGActionCallback implements ActionLoaderCallback {
    @Autowired
    private SakuliExceptionHandler exceptionHandler;

    @Override
    public void initTestCase(TestCase testCase) {

    }

    /**
     * Throw the handled Exception as {@link AssertionError} after {@link SakuliExceptionHandler#processException(Throwable)} to stop the current
     * test case execution of an JAVA test.
     */
    @Override
    public void handleException(SakuliException transformedException) {
        if (transformedException == null || exceptionHandler.resumeToTestExcecution(transformedException)) {
            return;
        }
        throw new AssertionError(transformedException);
    }

    @Override
    public void releaseContext() {

    }
}
