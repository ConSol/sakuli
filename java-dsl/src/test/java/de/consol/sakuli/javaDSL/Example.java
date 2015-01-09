/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.javaDSL;

import de.consol.sakuli.javaDSL.actions.Region;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Tobias Schneck
 */
public class Example extends AbstractSakuliTest {


    @Override
    protected String getTestSuiteFolder() {
        return getTestSuiteRootFolder() + File.separator + "example";
    }

    @Override
    protected TestCaseInitParameter getTestCaseInitParameter() throws Throwable {
        return new TestCaseInitParameter(
                "hurrican-test",
                getTestSuiteFolder() + File.separator + "test1")
                .withWarningTime(120)
                .withCriticalTime(140);
    }

    @Test
    public void example1() throws Exception {
        new Region().highlight();

    }
}
