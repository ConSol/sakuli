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

    @Override
    public void onTestFailure(ITestResult tr) {
        SakuliExceptionHandler exceptionHandler = BeanLoader.loadBaseActionLoader().getExceptionHandler();
        if (!exceptionHandler.isAlreadyProcessed(tr.getThrowable())) {
            exceptionHandler.handleException(tr.getThrowable());
        }
        super.onTestFailure(tr);
    }
}
