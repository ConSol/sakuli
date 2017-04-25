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

package org.sakuli.selenium.testng;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
import org.sakuli.selenium.JavaTestResourceHelper;
import org.sakuli.selenium.actions.testcase.TestCaseInitParameter;
import org.sakuli.selenium.testng.utils.SakuliSePropertyPlaceholderConfigurer;
import org.sakuli.services.InitializingServiceHelper;
import org.sakuli.services.TeardownServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * TODO TS cleanup and docu
 *
 * @author tschneck
 *         Date: 4/13/17
 */
public class SakuliSeTest implements ITestListener, IInvokedMethodListener2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(SakuliSeTest.class);

    protected static String extractTestCaseId(ITestResult testResult) {
        return testResult.getInstanceName() + '#' + testResult.getMethod().getMethodName();
    }

    protected static boolean isMethodSakuliTestMethod(IInvokedMethod method) {
        return method.isTestMethod() && getSakuliTestCaseAnnotation(method).isPresent();
    }

    protected static Optional<SakuliTestCase> getSakuliTestCaseAnnotation(IInvokedMethod method) {
        return Stream.of(getMethodAnnotations(method))
                .filter(a -> a instanceof SakuliTestCase)
                .map(sa -> (SakuliTestCase) sa)
                .findFirst();
    }

    protected static Annotation[] getMethodAnnotations(IInvokedMethod method) {
        if (method.getTestMethod() != null &&
                method.getTestMethod().getConstructorOrMethod() != null &&
                method.getTestMethod().getConstructorOrMethod().getMethod() != null) {
            return method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotations();
        }
        return new Annotation[]{};
    }

    @Override
    public void onTestStart(ITestResult result) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null) {
            SakuliExceptionHandler exceptionHandler = BeanLoader.loadBaseActionLoader().getExceptionHandler();
            exceptionHandler.handleException(result.getThrowable(), true);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
        initTestSuite(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        tearDownSuite(context);
    }

    /**
     * Initialize the Spring context of the Sakuli test suite and invokes all configured Initializing Services.
     *
     * @param context
     */
    protected void initTestSuite(ITestContext context) {
        LOGGER.info("............................INITIALIZE SAKULI-CONTEXT");
        BeanLoader.CONTEXT_PATH = "sakuli-se-beanRefFactory.xml";
        SakuliSePropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = JavaTestResourceHelper.getTestSuiteRootFolder().toString();
        SakuliSePropertyPlaceholderConfigurer.setSakuliProperty(SakuliProperties.LOG_FOLDER, getTestSuiteOutputFolder(context).toString());
        SakuliSePropertyPlaceholderConfigurer.setTestSuiteProperty(TestSuiteProperties.SUITE_ID, context.getSuite().getName());
        try {
            InitializingServiceHelper.invokeInitializingServcies();
        } catch (FileNotFoundException e) {
            throw new SakuliRuntimeException(e);
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (isMethodSakuliTestMethod(method)) {
            TestCaseInitParameter initParameter = new TestCaseInitParameter(resolveTestCaseId(method, testResult));

            //add the the testcase folder to the Builder and add the image folder
            JavaTestResourceHelper.getTestCaseFolder(testResult.getMethod().getRealClass())
                    .filter(Files::exists)
                    .ifPresent(initParameter::withTestCaseFolder);

            getSakuliTestCaseAnnotation(method).ifPresent(sa -> initParameter
                    .withWarningTime(sa.warningTime())
                    .withCriticalTime(sa.criticalTime())
                    //adds additional image paths
                    .addImagePath(
                            JavaTestResourceHelper.resolveImagePath(testResult.getMethod().getRealClass(), sa.additionalImagePaths())
                    )
            );

            if (testResult.getStartMillis() > 0) {
                initParameter.withStartDate(new Date(testResult.getStartMillis()));
            }
            initTestCaseAction(initParameter);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (isMethodSakuliTestMethod(method)) {
            String testCaseId = resolveTestCaseId(method, testResult);

            LOGGER.info("............................ SAVE RESULTS OF TEST-CASE '{}'", testCaseId);
            final BaseActionLoader loader = BeanLoader.loadBaseActionLoader();
            if (loader.getCurrentTestCase() == null) {
                loader.getExceptionHandler().handleException("Can't load current testcase: Id ?:" + testCaseId, false);
            }
            if (!loader.getCurrentTestCase().getId().equals(testCaseId)) {
                loader.getExceptionHandler().handleException(
                        "testcaseID '" + testCaseId + "' to save the test case Result ist is not matching with: " + loader.getCurrentTestCase().getId(), false);
            }
            //set TestCase vars
            TestCase tc = loader.getCurrentTestCase();
            tc.setStopDate(new Date()); // use enddate of TestNG?
            LOGGER.debug("test case duration = " + tc.getDuration());
            tc.refreshState();
            //release current test case -> indicates that this case is finished
            loader.setCurrentTestCase(null);
        }
    }

    protected String resolveTestCaseId(IInvokedMethod method, ITestResult testResult) {
        return getSakuliTestCaseAnnotation(method)
                .map(SakuliTestCase::testCaseName)
                .filter(StringUtils::isNoneBlank)
                .orElse(extractTestCaseId(testResult));
    }

//    private String resolveImagePath(Class testClass, String imagePaths) {
//        return javaTestResourceHelper.resolveImagePath(testClass, imagePaths);
//    }
//
//    private Optional<Path> getTestCaseFolder(Class realTestClass) {
//        return javaTestResourceHelper.getTestCaseFolder(realTestClass);
//    }

    /**
     * Initialize the image folders, warning time and critical time for the current testcase with the assigned
     * initParameter.
     *
     * @param initParameter a initialized object of {@link TestCaseInitParameter}.
     */
    protected void initTestCaseAction(TestCaseInitParameter initParameter) {
        BeanLoader.loadBaseActionLoader().getTestSuite()
                .addTestCase(new TestCase(initParameter.getTestCaseId(), initParameter.getTestCaseId()));
        List<Path> imagePaths = initParameter.getImagePaths();
        BeanLoader.loadTestCaseAction().initWithPaths(initParameter.getTestCaseId(),
                initParameter.getWarningTime(),
                initParameter.getCriticalTime(),
                imagePaths.toArray(new Path[imagePaths.size()])
        );
        BeanLoader.loadBaseActionLoader().getCurrentTestCase().setStartDate(initParameter.getStartDate());
        BeanLoader.loadBaseActionLoader().getCurrentTestCase().setTcFolder(initParameter.getTestCaseFolder());
    }


//    @BeforeMethod(alwaysRun = true)
//    public void initTcStep() throws Exception {
//        counter++;
//        startTime = DateTime.now();
//    }
/*
    @AfterMethod(alwaysRun = true)
    public void saveTcStep() throws Throwable {
        testCaseAction.addTestCaseStep("step " + counter,
                String.valueOf(startTime.getMillis()),
                String.valueOf(DateTime.now().getMillis()),
                0
        );
    }*/
/*
    @AfterClass(alwaysRun = true)
    public void stopTC() throws Exception {
        if (executorService != null) {
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        String testCaseName = this.getClass().getSimpleName();
        LOGGER.info("............................ SAVE RESULTS OF TEST-CASE '{}' - {}", initParameter.getTestCaseId(), testCaseName);
        testCaseAction.saveResult(initParameter.getTestCaseId(),
                String.valueOf(startTimeCase.getMillis()),
                String.valueOf(DateTime.now().getMillis()),
                null,
                null
        );
        if (browser != null) {
            browser.close();
        }
    }*/
//
//    protected String getTestSuiteRootFolder() {
//        return javaTestResourceHelper.getTestSuiteRootFolder();
//    }

    /**
     * @param context
     * @return the matching resource folder of the package of the current class!
     */
    protected Path getTestSuiteOutputFolder(ITestContext context) {
        Path outputFolder = Paths.get(context.getOutputDirectory()).resolve("sakuli_result").normalize().toAbsolutePath();
        if (!Files.exists(outputFolder)) {
            try {
                FileUtils.forceMkdir(outputFolder.toFile());
            } catch (IOException e) {
                throw new SakuliRuntimeException(String.format("Cannot create folder '%s'", outputFolder.toString()), e);
            }
        }
        return outputFolder;

    }

    protected void tearDownSuite(ITestContext context) {
        TestSuite testSuite = BeanLoader.loadBaseActionLoader().getTestSuite();
        if (testSuite != null) {
            LOGGER.info("========== TEAR-DOWN SAKULI TEST SUITE '{}' ==========", testSuite.getId());
            testSuite.setStopDate(context.getEndDate() == null ? DateTime.now().toDate() : context.getEndDate());
            TeardownServiceHelper.invokeTeardownServices();
        }
    }


    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }

    /*    protected Path getTestCaseFolder() {
        return Paths.get(getTestSuiteOutputFolder() + File.separator + initParameter.getTestCaseFolderName());
    }*/


}
