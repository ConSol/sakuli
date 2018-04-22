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

package org.sakuli.starter.sahi.action.logging;

import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.report.TestResult;
import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.logging.Logger;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.actions.screenbased.TypingUtil;
import org.sakuli.actions.testcase.JavaScriptTestCaseActionImpl;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.sakuli.starter.sahi.aop.AopBaseTest;
import org.sakuli.starter.sahi.exceptions.SahiActionException;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SahiLogToResultCallbackTest extends AopBaseTest {

    @DataProvider(name = "resultTypes")
    public static Object[][] resultTypes() {
        return new Object[][]{
                {ResultType.INFO},
                {ResultType.ERROR},
                {ResultType.SKIPPED},
                {ResultType.SUCCESS}
        };

    }

    @DataProvider(name = "logLevel")
    public static Object[][] logLevel() {
        return new Object[][]{
                {LogLevel.INFO},
                {LogLevel.DEBUG},
                {LogLevel.ERROR},
                {LogLevel.WARNING},
        };
    }


    /**
     * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler For IntelliJ you can
     * make a right click on the maven target 'aspectj:compile' and mark it with the option 'Execute After Make'.
     */
    @Test
    public void testCallEnvironmentMethod() throws Exception {
        ScreenActionLoader loaderMock = mock(ScreenActionLoader.class);
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);
        Environment env = new Environment(false);
        ReflectionTestUtils.setField(env, "typingUtil", typingUtil);
        ReflectionTestUtils.setField(env, "loader", loaderMock);
        env.type("BLA");
        assertLastLine(logFile, "Environment", LogLevel.INFO, "Environment.type() - type over system keyboard with arg(s) [BLA]");
        verifySahiReportLogEntry(ResultType.INFO, "Environment.type() - type over system keyboard with arg(s) [BLA]");
    }

    @Test
    public void testCallRegionMethod() throws Exception {
        TypingUtil typingUtil = mock(TypingUtil.class);
        when(typingUtil.type(anyString(), anyString())).thenReturn(null);
        Region region = new RegionTestMock();
        ReflectionTestUtils.setField(region, "typingUtil", typingUtil);
        region.type("BLA");
        assertLastLine(logFile, "Region", LogLevel.INFO, "Region.type() - type over system keyboard with arg(s) [BLA]");
        verifySahiReportLogEntry(ResultType.INFO, "Region.type() - type over system keyboard with arg(s) [BLA]");
    }

    @Test
    public void testGetRhinoScriptRunner() throws Exception {
        initMocks();

        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the option 'Execute After Make'.
         */
        assertNotNull(BeanLoader.loadBean(SahiActionLoader.class).getSahiReport());
    }

    @Test(dataProvider = "resultTypes")
    public void testDoHandleRhinoException(ResultType resultTyp) throws Exception {
        initMocks();
        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the option 'Execute After Make'.
         */
        Report sahiReport = BeanLoader.loadBean(SahiActionLoader.class).getSahiReport();
        int lisSize = sahiReport.getListResult().size();
        assertNotNull(sahiReport);
        sahiReport.addResult("TEST-ENTRY", resultTyp, "bla", "TEST-ENTRY");
        verifySahiReport(resultTyp, lisSize);
    }

    @Test(dataProvider = "resultTypes")
    public void testDoHandleRhinoExceptionResultString(ResultType resultTyp) throws Exception {
        initMocks();
        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the option 'Execute After Make'.
         */
        Report sahiReport = BeanLoader.loadBean(SahiActionLoader.class).getSahiReport();
        int lisSize = sahiReport.getListResult().size();
        assertNotNull(sahiReport);
        sahiReport.addResult("TEST-ENTRY", resultTyp.getName(), "bla", "TEST-ENTRY");
        verifySahiReport(resultTyp, lisSize);
    }

    @Test
    public void testDoEnvironmentLog() throws Exception {
        initMocks();
        Environment testAction = new Environment(false);
        testAction.sleep(1);

        verifySahiReportLogEntry(ResultType.INFO, "Environment.sleep() - sleep and do nothing for x seconds with arg(s) [1]");
    }

    @Test
    public void testDoLoggingLog() throws Exception {
        initMocks();
        Logger.logInfo("INFO-LOG");
        verifySahiReportLogEntry(ResultType.INFO, "INFO-LOG");
    }

    @Test
    public void testDoTestCaseActionLog() throws Exception {
        initMocks();
        BaseActionLoader loader = mock(BaseActionLoader.class);
        TestCase sampleTc = new TestCase("test", "testID");
        when(loader.getCurrentTestCase()).thenReturn(sampleTc);
        JavaScriptTestCaseActionImpl testAction = new JavaScriptTestCaseActionImpl();
        ReflectionTestUtils.setField(testAction, "loader", loader, BaseActionLoader.class);
        testAction.init("testID", 3, 4, "imagefolder1", "imagefolder2");

        verifySahiReportLogEntry(ResultType.INFO,
                "\"test case [" + sampleTc.getActionValueString() + "]\" AbstractTestCaseActionImpl.init() - init a new test case with arg(s) [testID, 3, 4, [imagefolder1, imagefolder2]]");
    }

    private void verifySahiReport(ResultType resultTyp, int initialListSize) {
        if (resultTyp != null) {
            Report sahiReport2 = BeanLoader.loadBean(SahiActionLoader.class).getSahiReport();
            int newLisSize = sahiReport2.getListResult().size();
            assertEquals(newLisSize, initialListSize + 1);
            TestResult testResult = sahiReport2.getListResult().get(newLisSize - 1);
            assertNotNull(testResult);

            SakuliExceptionHandler sakuliExceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
            if (resultTyp.equals(ResultType.ERROR) || resultTyp.equals(ResultType.FAILURE)) {
                verify(sakuliExceptionHandler).handleException(any(SahiActionException.class));
            } else {
                verify(sakuliExceptionHandler, never()).handleException(any(SahiActionException.class));
            }
        }
    }

    private void verifySahiReportLogEntry(ResultType resultTyp, String message) {
        if (resultTyp != null) {
            Report sahiReport2 = BeanLoader.loadBean(SahiActionLoader.class).getSahiReport();
            final List<TestResult> listResult = sahiReport2.getListResult();
            assertTrue(listResult.size() > 0, "List of TestResult is empty!");
            TestResult testResult = listResult.stream()
                    .filter(r -> ((String) ReflectionTestUtils.getField(r, "message")).contains(message))
                    .findFirst().orElseGet(null);
            assertNotNull(testResult, "no result in sahi report found, which contains:" + message);
            assertEquals(ReflectionTestUtils.getField(testResult, "type"), resultTyp);

            SakuliExceptionHandler sakuliExceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
            if (resultTyp.equals(ResultType.ERROR) || resultTyp.equals(ResultType.FAILURE)) {
                verify(sakuliExceptionHandler).handleException(any(SahiActionException.class));
            } else {
                verify(sakuliExceptionHandler, never()).handleException(any(SahiActionException.class));
            }
        }
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