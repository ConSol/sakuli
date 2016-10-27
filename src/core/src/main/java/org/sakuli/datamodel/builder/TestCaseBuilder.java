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
