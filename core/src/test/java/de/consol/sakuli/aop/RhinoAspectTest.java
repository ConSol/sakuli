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

import de.consol.sakuli.actions.TestCaseAction;
import de.consol.sakuli.actions.environment.Environment;
import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.actions.logging.Logger;
import de.consol.sakuli.actions.screenbased.RegionTestImpl;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.datamodel.actions.LogResult;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.loader.ScreenActionLoader;
import net.sf.sahi.playback.SahiScript;
import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.report.TestResult;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RhinoAspectTest extends AopBaseTest {

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

    @Test
    public void testGetRhinoScriptRunner() throws Exception {
        initMocks();

        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the option 'Execute After Make'.
         */
        assertNotNull(BeanLoader.loadBaseActionLoader().getSahiReport());
    }

    @Test(dataProvider = "resultTypes")
    public void testDoHandleRhinoException(ResultType resultTyp) throws Exception {
        initMocks();
        /**
         * If the test fails in your IDE, make sure that your compiler use the correct AspectJ-Compiler
         * For IntelliJ you can make a right click on the maven target 'aspectj:compile' and mark it
         * with the option 'Execute After Make'.
         */
        Report sahiReport = BeanLoader.loadBaseActionLoader().getSahiReport();
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
        Report sahiReport = BeanLoader.loadBaseActionLoader().getSahiReport();
        int lisSize = sahiReport.getListResult().size();
        assertNotNull(sahiReport);
        sahiReport.addResult("TEST-ENTRY", resultTyp.getName(), "bla", "TEST-ENTRY");
        verifySahiReport(resultTyp, lisSize);
    }

    private void initMocks() {
        SakuliExceptionHandler sakuliExceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
        reset(sakuliExceptionHandler);
        SahiScript sahiScriptMock = mock(SahiScript.class);
        when(sahiScriptMock.jsString()).thenReturn("");
        new RhinoScriptRunner(sahiScriptMock);
    }

    private void verifySahiReport(ResultType resultTyp, int initialListSize) {
        if (resultTyp != null) {
            Report sahiReport2 = BeanLoader.loadBaseActionLoader().getSahiReport();
            int newLisSize = sahiReport2.getListResult().size();
            assertEquals(newLisSize, initialListSize + 1);
            TestResult testResult = sahiReport2.getListResult().get(newLisSize - 1);
            assertNotNull(testResult);

            SakuliExceptionHandler sakuliExceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
            if (resultTyp.equals(ResultType.ERROR) || resultTyp.equals(ResultType.FAILURE)) {
                verify(sakuliExceptionHandler).handleException(any(LogResult.class));
            } else {
                verify(sakuliExceptionHandler, never()).handleException(any(LogResult.class));
            }
        }
    }

    @Test(dataProvider = "logLevel")
    public void testAddActionLog(LogLevel logLevel) throws Exception {
        initMocks();
        Report sahiReport = BeanLoader.loadBaseActionLoader().getSahiReport();
        final int lisSize = sahiReport.getListResult().size();

        final String classContent = "Test-Action-Content";
        final String className = TestCaseAction.class.getSimpleName();
        final String methodName = "actionMethod";
        final String sampleMessage = "sample-message-for-log";
        final String arg1 = "ARG1";
        final String arg2 = "NULL";

        TestCaseAction testAction = mock(TestCaseAction.class);
        when(testAction.toString()).thenReturn(classContent);

        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getTarget()).thenReturn(testAction);
        Signature signature = mock(Signature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(TestCaseAction.class);
        when(signature.getName()).thenReturn(methodName);
        when(signature.getDeclaringTypeName()).thenReturn(TestCaseAction.class.getName());
        when(jp.getArgs()).thenReturn(new Object[]{arg1, null});


        LogToResult logToResult = mock(LogToResult.class);
        when(logToResult.logClassInstance()).thenReturn(true);
        when(logToResult.message()).thenReturn(sampleMessage);
        when(logToResult.logArgs()).thenReturn(true);
        when(logToResult.level()).thenReturn(logLevel);
        RhinoAspect testling = new RhinoAspect();

        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                "\"" + classContent + "\" " + className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [" + arg1 + ", " + arg2 + "]"
        );
        verifySahiReport(logLevel.getResultType(), lisSize);

        //hide args
        when(logToResult.logArgs()).thenReturn(false);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                "\"" + classContent + "\" " + className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [****, ****]"
        );

        //without class values
        when(logToResult.logClassInstance()).thenReturn(false);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [****, ****]"
        );

        //without args
        when(jp.getArgs()).thenReturn(null);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "() - " + sampleMessage);

        //without message
        when(logToResult.message()).thenReturn(null);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "()");
    }


    @Test
    public void testDoEnvironmentLog() throws Exception {
        initMocks();
        ScreenActionLoader screenActionLoader = mock(ScreenActionLoader.class);
        Environment testAction = new Environment(false, screenActionLoader);
        testAction.sleep(1);

        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO, "Environment.sleep() - sleep and do nothing for x seconds with arg(s) [1]");
    }

    @Test
    public void testDoLoggingLog() throws Exception {
        initMocks();
        Logger.logInfo("INFO-LOG");
        assertLastLine(logFile, Logger.class.getSimpleName(), LogLevel.INFO, "Logger.logInfo() with arg(s) [INFO-LOG]");
    }

    @Test
    public void testdoScreenBasedActionLog() throws Exception {
        initMocks();
        RegionTestImpl.testLogMethod();

        assertLastLine(logFile, RegionTestImpl.class.getSimpleName(), LogLevel.WARNING, "RegionTestImpl.testLogMethod()");
    }

    @Test
    public void testDoTestCaseActionLog() throws Exception {
        initMocks();
        BaseActionLoader loader = mock(BaseActionLoader.class);
        TestCase sampleTc = new TestCase("test", "testID");
        when(loader.getCurrentTestCase()).thenReturn(sampleTc);
        TestCaseAction testAction = new TestCaseAction();
        ReflectionTestUtils.setField(testAction, "loader", loader, BaseActionLoader.class);
        testAction.init("testID", 3, 4, "imagefolder1", "imagefolder2");

        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO,
                "\"test case [" + sampleTc.getActionValueString() + "]\" TestCaseAction.init() - init a new test case with arg(s) [testID, 3, 4, [imagefolder1, imagefolder2]]");
    }


}