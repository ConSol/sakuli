/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.javaDSL;

import org.joda.time.DateTime;
import org.sakuli.actions.TestCaseAction;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseBuilder;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
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
import java.net.URL;
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

    abstract protected TestCaseInitParameter getTestCaseInitParameter() throws Throwable;

    protected abstract String getTestSuiteFolder();

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
        SakuliJavaPropertyPlaceholderConfigurer.JAVA_TEST_SUITE_FOLDER = getTestSuiteRootFolder();
        SakuliJavaPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = getSakuliMainFolder();
        SakuliJavaPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = getSahiFolder();
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
    public void initTC() throws Throwable {
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
        testSuite.setUiTest(true);
        testSuite.addTestCase(new TestCaseBuilder(testCaseName, initParameter.getTestCaseId()).build());
        testCaseAction = BeanLoader.loadTestCaseAction();

        //init the image folders and test case times
        initParameter.addImagePath(getTestCaseFolder().toString());
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
    public void stopTC() throws Throwable {
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
        URL test_suites = this.getClass().getResource("/test_suites");
        try {
            return Paths.get(test_suites.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("cannot find test-resource folder 'test-suites'", e);
        }
    }

    protected Path getTestCaseFolder() {
        return Paths.get(getTestSuiteFolder() + File.separator + initParameter.getTestCaseFolderName());
    }

    //TODO make separate maven module and centralize the default properties
    protected String getSakuliMainFolder() {
        return ".." + File.separator + "core" + File.separator + "src" + File.separator + "main";
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
