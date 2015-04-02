/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.example;

import org.sakuli.javaDSL.AbstractSakuliTest;
import org.sakuli.javaDSL.TestCaseInitParameter;
import org.sakuli.javaDSL.actions.Region;
import org.testng.annotations.Test;

/**
 * @author Tobias Schneck
 */
public class Example extends AbstractSakuliTest {

    @Override
    protected TestCaseInitParameter getTestCaseInitParameter() throws Throwable {
        return new TestCaseInitParameter(
                "my-example-test", "test1")
                .withWarningTime(5)
                .withCriticalTime(10);
    }

    @Test
    public void example1() throws Exception {
        new Region().highlight();

    }
}
