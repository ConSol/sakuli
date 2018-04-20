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

package org.sakuli.javaDSL;/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2015
 */

import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.loader.BeanLoader;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * @author Tobias Schneck
 */
public class SakuliExceptionListener extends TestListenerAdapter {

    private static Exception castTo(Throwable throwable) {
        return (throwable instanceof Exception) ? (Exception) throwable : new Exception(throwable);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        SakuliExceptionHandler exceptionHandler = BeanLoader.loadBaseActionLoader().getExceptionHandler();
        if (exceptionHandler != null && !exceptionHandler.isAlreadyProcessed(castTo(tr.getThrowable()))) {
            exceptionHandler.handleException(castTo(tr.getThrowable()));
        }
        super.onTestFailure(tr);
    }
}
