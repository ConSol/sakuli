/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.datamodel.builder;

import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCase;

import java.util.Date;

/**
 * @author Tobias Schneck
 */
public class TestCaseBuilder implements Builder<TestCase> {
    private final String name;
    private final String id;

    public TestCaseBuilder(String name, String id) {
        this.name = name;
        this.id = id;
    }


    @Override
    public TestCase build() {
        TestCase newTestCase = new TestCase(name, id);
        newTestCase.setStartDate(new Date());
        return newTestCase;
    }
}
