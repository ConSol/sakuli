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

package org.sakuli.javaDSL;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.sakuli.actions.TestCaseAction;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseBuilder;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.javaDSL.utils.SakuliJavaPropertyPlaceholderConfigurer;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.InitializingServiceHelper;
import org.sakuli.services.ResultServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Tobias Schneck
 */
public abstract class AbstractSakuliTest {
    public static final String SAKULI_TEST = "sakuli-test";
    protected static final Logger logger = LoggerFactory.getLogger(AbstractSakuliTest.class);
    protected ExecutorService executorService;
    protected TestCaseAction testCaseAction;
    private int counter;
    private DateTime startTime;
    private DateTime startTimeCase;
    private TestCaseInitParameter initParameter;
    private TestSuite testSuite;

    public static Path resolveResource(Class<?> aClass, String resourceName) {
        try {
            //should be the common src/test/resources folder
            return Paths.get(aClass.getResource(resourceName).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            throw new SakuliRuntimeException("cannot resolve resource '" + resourceName + "' from classpath!", e);
        }
    }

    abstract protected TestCaseInitParameter getTestCaseInitParameter() throws Exception;

    /**
     * Initialize the Spring context of the Sakuli test suite and invokes all configured Initializing Services.
     *
     * @throws FileNotFoundException
     */
    protected void initTestSuiteParameter() throws FileNotFoundException {
        logger.info("............................INITIALIZE SAKULI-CONTEXT");
        executorService = Executors.newCachedThreadPool();
        BeanLoader.CONTEXT_PATH = "java-dsl-beanRefFactory.xml";
        SakuliJavaPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestSuiteFolder();
        SakuliJavaPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = getSakuliHomeFolder();
        SakuliJavaPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = getSahiFolder();
        InitializingServiceHelper.invokeInitializingServcies();
    }

    /**
     * Override this method to specify a custom Sahi installation folder!
     *
     * @return the installation folder of Sahi
     */
    protected String getSahiFolder() {
        return null;
    }

    @BeforeClass(alwaysRun = true)
    public void initTC() throws Exception {
        if (testSuite == null) {
            initTestSuiteParameter();
        }
        logger.info("............................INITIALIZE TEST-CASE with {}", initParameter);
        String testCaseName = this.getClass().getSimpleName();
        initParameter = getTestCaseInitParameter();
        if (initParameter == null) {
            throw new SakuliException("init parameter have to be set!");
        }
        testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.addTestCase(new TestCaseBuilder(testCaseName, initParameter.getTestCaseId()).build());
        testCaseAction = BeanLoader.loadTestCaseAction();

        //init the image folders and test case times
        if (Files.exists(getTestCaseFolder())) {
            initParameter.addImagePath(getTestCaseFolder().toString());
        }
        List<Path> imagePaths = initParameter.getImagePaths();
        testCaseAction.initWithPaths(initParameter.getTestCaseId(),
                initParameter.getWarningTime(),
                initParameter.getCriticalTime(),
                imagePaths.toArray(new Path[imagePaths.size()])
        );
        logger.info("............................START TEST-CASE '{}' - {}", initParameter.getTestCaseId(), testCaseName);
        counter = 0;
        startTimeCase = DateTime.now();
    }

    @BeforeMethod(alwaysRun = true)
    public void initTcStep() throws Exception {
        counter++;
        startTime = DateTime.now();
    }

    @AfterMethod(alwaysRun = true)
    public void saveTcStep() throws Throwable {
        testCaseAction.addTestCaseStep("step " + counter,
                String.valueOf(startTime.getMillis()),
                String.valueOf(DateTime.now().getMillis()),
                0
        );
        List<Throwable> exceptions = SakuliExceptionHandler.getAllExceptions(testSuite);
        if (!CollectionUtils.isEmpty(exceptions)) {
            //return the first
            throw exceptions.iterator().next();
        }

    }

    @AfterClass(alwaysRun = true)
    public void stopTC() throws Exception {
        if (executorService != null) {
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        String testCaseName = this.getClass().getSimpleName();
        logger.info("............................ SAVE RESULTS OF TEST-CASE '{}' - {}", initParameter.getTestCaseId(), testCaseName);
        testCaseAction.saveResult(initParameter.getTestCaseId(),
                String.valueOf(startTimeCase.getMillis()),
                String.valueOf(DateTime.now().getMillis()),
                null,
                null
        );
    }

    protected String getTestSuiteRootFolder() {
        Path resourcePath = resolveResource(this.getClass(), "/");
        if (Files.exists(resourcePath)) {
            return resourcePath.normalize().toAbsolutePath().toString();
        }
        throw new SakuliRuntimeException("Cannot load test suites root folder! Should be at normal test 'src/test/resources'");
    }

    /**
     * @return the matching resource folder of the package of the current class!
     */
    protected String getTestSuiteFolder() {
        Path suiteFolder = resolveResource(this.getClass(), ".");
        if (Files.exists(suiteFolder)) {
            return suiteFolder.normalize().toAbsolutePath().toString();
        }
        throw new SakuliRuntimeException(String.format("Cannot load test suite folder from classpath! Should be at normal test 'src/test/resources/%s'",
                StringUtils.replace(this.getClass().getCanonicalName(), ".", "/")));
    }

    protected Path getTestCaseFolder() {
        return Paths.get(getTestSuiteFolder() + File.separator + initParameter.getTestCaseFolderName());
    }

    protected String getSakuliHomeFolder() {
        String packageName = "/org/sakuli/common";
        Path sakuliHomeFolder = resolveResource(AbstractSakuliTest.class, packageName);
        if (Files.exists(sakuliHomeFolder)) {
            return sakuliHomeFolder.normalize().toAbsolutePath().toString();
        }
        throw new SakuliRuntimeException("Cannot load SAKULI_HOME folder! Should be normally under 'target/classes/" + packageName + "'");
    }


    @AfterSuite(alwaysRun = true)
    public void tearDown() throws Exception {
        if (testSuite != null) {
            logger.info("............................ TEAR-DOWN SAKULI TEST SUITE '{}'", testSuite.getId());
            testSuite.setStopDate(DateTime.now().toDate());
            ResultServiceHelper.invokeResultServices();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
