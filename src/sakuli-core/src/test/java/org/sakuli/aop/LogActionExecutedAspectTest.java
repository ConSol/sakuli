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

package org.sakuli.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.sakuli.actions.environment.Environment;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.actions.logging.LogToResultCallback;
import org.sakuli.actions.logging.Logger;
import org.sakuli.actions.screenbased.RegionTestImpl;
import org.sakuli.actions.testcase.JavaScriptTestCaseActionImpl;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class LogActionExecutedAspectTest extends AopBaseTest {

    @DataProvider(name = "logLevel")
    public static Object[][] logLevel() {
        return new Object[][]{
                {LogLevel.INFO},
                {LogLevel.DEBUG},
                {LogLevel.ERROR},
                {LogLevel.WARNING},
        };
    }

    @Test(dataProvider = "logLevel")
    public void testAddActionLog(LogLevel logLevel) throws Exception {
        initMocks();

        final String classContent = "Test-Action-Content";
        final String className = JavaScriptTestCaseActionImpl.class.getSimpleName();
        final String methodName = "actionMethod";
        final String sampleMessage = "sample-message-for-log";
        final String arg1 = "ARG1";
        final String arg2 = "NULL";

        JavaScriptTestCaseActionImpl testAction = mock(JavaScriptTestCaseActionImpl.class);
        when(testAction.toString()).thenReturn(classContent);

        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getTarget()).thenReturn(testAction);
        Signature signature = mock(Signature.class);
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(JavaScriptTestCaseActionImpl.class);
        when(signature.getName()).thenReturn(methodName);
        when(signature.getDeclaringTypeName()).thenReturn(JavaScriptTestCaseActionImpl.class.getName());
        when(jp.getArgs()).thenReturn(new Object[]{arg1, null});


        LogToResult logToResult = mock(LogToResult.class);
        when(logToResult.logClassInstance()).thenReturn(true);
        when(logToResult.message()).thenReturn(sampleMessage);
        when(logToResult.logArgs()).thenReturn(true);
        when(logToResult.level()).thenReturn(logLevel);
        LogActionExecutedAspect testling = BeanLoader.loadBean(LogActionExecutedAspect.class);

        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                "\"" + classContent + "\" " + className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [" + arg1 + ", " + arg2 + "]"
        );
        final Collection<LogToResultCallback> callbacks = BeanLoader.loadMultipleBeans(LogToResultCallback.class).values();
        assertEquals(callbacks.size(), 1);
        final LogToResultCallback cb = callbacks.stream().findFirst().get();
        verify(cb).doActionLog(any(), eq(logToResult));

        //hide args
        when(logToResult.logArgs()).thenReturn(false);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                "\"" + classContent + "\" " + className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [****, ****]"
        );
        verify(cb, times(2)).doActionLog(any(), eq(logToResult));


        //without class values
        when(logToResult.logClassInstance()).thenReturn(false);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "() - " + sampleMessage +
                        " with arg(s) [****, ****]"
        );
        verify(cb, times(3)).doActionLog(any(), eq(logToResult));

        //without args
        when(jp.getArgs()).thenReturn(null);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "() - " + sampleMessage);
        verify(cb, times(4)).doActionLog(any(), eq(logToResult));


        //without message
        when(logToResult.message()).thenReturn(null);
        testling.addActionLog(jp, logToResult);
        assertLastLine(logFile, className, logLevel,
                className + "." + methodName + "()");
        verify(cb, times(5)).doActionLog(any(), eq(logToResult));
    }

    @Test
    public void testDoEnvironmentLog() throws Exception {
        initMocks();
        ScreenActionLoader screenActionLoader = mock(ScreenActionLoader.class);
        Environment testAction = new Environment(false);
        ReflectionTestUtils.setField(testAction, "loader", screenActionLoader);
        testAction.sleep(1);

        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO, "Environment.sleep() - sleep and do nothing for x seconds with arg(s) [1]");
    }

    @Test
    public void testDoLoggingLog() throws Exception {
        initMocks();
        Logger.logInfo("INFO-LOG");
        assertLastLine(logFile, "INFO-", LogLevel.INFO, "INFO-LOG");
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
        JavaScriptTestCaseActionImpl testAction = new JavaScriptTestCaseActionImpl();
        ReflectionTestUtils.setField(testAction, "loader", loader, BaseActionLoader.class);
        testAction.init("testID", 3, 4, "imagefolder1", "imagefolder2");

        assertLastLine(logFile, "AbstractTestCaseActionImpl", LogLevel.INFO,
                "\"test case [" + sampleTc.getActionValueString() + "]\" AbstractTestCaseActionImpl.init() - init a new test case with arg(s) [testID, 3, 4, [imagefolder1, imagefolder2]]");
    }

    @Test
    public void testDoJavaScriptTestCaseActionLog() throws Exception {
        initMocks();
        BaseActionLoader loader = mock(BaseActionLoader.class);
        TestCase sampleTc = new TestCase("test-js", "test-js-ID");
        sampleTc.setLastURL("last-url");
        when(loader.getCurrentTestCase()).thenReturn(sampleTc);
        JavaScriptTestCaseActionImpl testAction = new JavaScriptTestCaseActionImpl();
        ReflectionTestUtils.setField(testAction, "loader", loader, BaseActionLoader.class);
        assertEquals(testAction.getLastURL(), "last-url");

        assertLastLine(logFile, testAction.getClass().getSimpleName(), LogLevel.INFO,
                "\"test case [" + sampleTc.getActionValueString() + "]\" JavaScriptTestCaseActionImpl.getLastURL() - return 'lastURL'");
    }

    @Test
    public void testCreateLoggingString() throws Exception {
        JoinPoint jp = mock(JoinPoint.class);
        when(jp.getArgs()).thenReturn(new Object[]{"TEST", "arguments"});
        LogToResult annotation = mock(LogToResult.class);
        when(annotation.logArgsOnly()).thenReturn(true);
        when(annotation.logArgs()).thenReturn(true);
        StringBuilder result = BaseSakuliAspect.createLoggingString(jp, annotation);
        assertEquals(result.toString(), "TEST, arguments");
    }
}