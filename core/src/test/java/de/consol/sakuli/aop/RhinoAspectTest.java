/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.aop;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import net.sf.sahi.playback.SahiScript;
import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.report.TestResult;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RhinoAspectTest {

    @BeforeMethod
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = BaseTest.TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = BaseTest.INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "aopTest-beanRefFactory.xml";
    }


    @Test
    public void testGetRhinoScriptRunner() throws Exception {

        SahiScript sahiScriptMock = mock(SahiScript.class);
        when(sahiScriptMock.jsString()).thenReturn("");
        new RhinoScriptRunner(sahiScriptMock);

        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the opiton 'Execute After Make'.
         */
        assertNotNull(BeanLoader.loadBaseActionLoader().getSahiReport());


    }

    @Test
    public void testDoHandleRhinoException() throws Exception {

        SahiScript sahiScriptMock = mock(SahiScript.class);
        when(sahiScriptMock.jsString()).thenReturn("");
        new RhinoScriptRunner(sahiScriptMock);

        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the opiton 'Execute After Make'.
         */
        Report sahiReport = BeanLoader.loadBaseActionLoader().getSahiReport();
        int lisSize = sahiReport.getListResult().size();
        assertNotNull(sahiReport);
        sahiReport.addResult("TEST-ENTRY", ResultType.INFO, "bla", "TEST-ENTRY");

        Report sahiReport2 = BeanLoader.loadBaseActionLoader().getSahiReport();
        int newLisSize = sahiReport2.getListResult().size();
        assertEquals(newLisSize, lisSize + 1);
        TestResult testResult = sahiReport2.getListResult().get(newLisSize - 1);
        assertNotNull(testResult);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }

}