/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.services.forwarder.json;

import org.sakuli.BaseTest;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.builder.TestSuiteExampleBuilder;
import org.sakuli.datamodel.TestAction;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.loader.ScreenActionLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class GsonOutputBuilderTest {
    private GsonOutputBuilder testling;
    private TestSuite testSuite;
    private JsonProperties jsonProperties;

    @BeforeMethod
    public void setUp() throws Exception {
        testling = new GsonOutputBuilder();
        testSuite = new TestSuiteExampleBuilder().buildExample();
        jsonProperties = new JsonProperties();
        jsonProperties.setOutputJsonDir(".");
        ReflectionTestUtils.setField(testling, "testSuite", testSuite);
        ReflectionTestUtils.setField(testling, "jsonProperties", jsonProperties);
    }

    @Test
    public void testCreateOutput() throws Exception {
        TestCase testcaseData = new TestCase("testling", "123");
        testcaseData.addAction(TestAction.createSakuliTestAction(
                "Region",
                "dragAndDrop",
                new Object[]{
                        new RegionTestMock(),
                        null
                },
                "test recursive parsing",
                "docu"));


        List<TestAction> testActions = testcaseData.getTestActions();
        assertNotNull(testActions);
        assertNotNull(testSuite);
        testSuite.addTestCase(testcaseData);
        assertEquals(testActions.size(), 1);

        final String json = testling.createOutput();
        BaseTest.assertContains(json, "\"Mock for RegionImpl,");
        BaseTest.assertContains(json, "test recursive parsing");

        testcaseData.addAction(TestAction.createSakuliTestAction(
                "NullAction",
                "nullArg",
                null,
                null,
                "docu"));
        assertEquals(testActions.size(), 2);

        final String json2 = testling.createOutput();
        BaseTest.assertContains(json2, "\"NullAction");
        BaseTest.assertContains(json2, "nullArg");
        BaseTest.assertContains(json2, "\"NullAction\",\"method\":\"nullArg\",\"args\":[],\"message\":null,");

    }

    @Test
    public void testCreateOutputArrays() throws Exception {
        TestCase testcaseData = new TestCase("testling", "123");
        testcaseData.addAction(TestAction.createSakuliTestAction(
                "TestPath",
                "testPath",
                new Object[]{
                        new Object[]{Paths.get("test-url"), Paths.get("test-url2")},
                        null
                },
                "test recursive parsing",
                "docu"));


        List<TestAction> testActions = testcaseData.getTestActions();
        assertNotNull(testActions);
        assertNotNull(testSuite);
        testSuite.addTestCase(testcaseData);
        assertEquals(testActions.size(), 1);

        final String json = testling.createOutput();
        BaseTest.assertContains(json, "\"args\":[\"test-url,test-url2\"]");
    }

    private class RegionTestMock extends Region {

        public RegionTestMock() {
            super(mock(RegionImpl.class));
        }

        @Override
        public ScreenActionLoader getScreenActionLoader() {
            return mock(ScreenActionLoader.class);
        }

    }


}