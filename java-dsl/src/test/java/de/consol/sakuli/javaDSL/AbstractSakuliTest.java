/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.javaDSL;

import de.consol.sakuli.actions.TestCaseAction;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.builder.TestCaseBuilder;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import de.consol.sakuli.javaDSL.utils.SakuliJavaPropertyPlaceholderConfigurer;
import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.services.InitializingServiceHelper;
import de.consol.sakuli.services.ResultServiceHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.*;

import java.io.File;
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
@Test(groups = AbstractSakuliTest.SAKULI_TEST)
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

    @BeforeSuite
    public void setUp() throws Exception {
        executorService = Executors.newCachedThreadPool();
        BeanLoader.CONTEXT_PATH = "java-dsl-beanRefFactory.xml";
        SakuliJavaPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = getTestSuiteFolder();
        SakuliJavaPropertyPlaceholderConfigurer.JAVA_TEST_SUITE_FOLDER = getTestSuiteRootFolder();
        SakuliJavaPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = getIncludeFolder();
        InitializingServiceHelper.invokeInitializingServcies();
    }

    @BeforeClass
    public void initTC() throws Throwable {
        String testCaseName = this.getClass().getSimpleName();
        initParameter = getTestCaseInitParameter();
        if (initParameter == null) {
            throw new SakuliException("init parameter have to be set!");
        }
        testSuite = BeanLoader.loadBean(TestSuite.class);
        testSuite.addTestCase(new TestCaseBuilder(this.getClass().getSimpleName(), initParameter.getTestCaseId()).build());
        testSuite.setUiTest(true);
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

    @BeforeMethod
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
            for (Throwable e : exceptions) {
                logger.error("Sakuli-Exception:", e);
            }
            //return the first
//            throw exceptions.iterator().next();
        }

    }

    @AfterClass(alwaysRun = true)
    public void stopTC() throws Throwable {
        executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        String testCaseName = this.getClass().getSimpleName();
        logger.info("............................ STOP TEST-CASE '{}' - {}", initParameter.getTestCaseId(), testCaseName);
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
    protected String getIncludeFolder() {
        return ".." + File.separator + "core" + File.separator + "src" + File.separator + "main" + File.separator + "_include";
    }


    @AfterSuite(alwaysRun = true)
    public void tearDown() throws Exception {
        testSuite.setStopDate(DateTime.now().toDate());
        ResultServiceHelper.invokeResultServices();
        executorService.shutdownNow();
    }
}
