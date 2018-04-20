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

import net.sf.sahi.client.Browser;
import net.sf.sahi.test.ProcessHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.sakuli.actions.TestCaseAction;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.builder.TestCaseBuilder;
import org.sakuli.exceptions.SakuliCheckedException;
import org.sakuli.exceptions.SakuliRuntimeException;
import org.sakuli.javaDSL.service.SahiInitializingService;
import org.sakuli.javaDSL.utils.SakuliJavaPropertyPlaceholderConfigurer;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.InitializingServiceHelper;
import org.sakuli.services.TeardownServiceHelper;
import org.sakuli.utils.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Tobias Schneck
 */
@Listeners(value = SakuliExceptionListener.class)
public abstract class AbstractSakuliTest {
    public static final String SAKULI_TEST = "sakuli-test";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSakuliTest.class);
    protected ExecutorService executorService;
    protected TestCaseAction testCaseAction;
    protected Browser browser;
    private int counter;
    private DateTime startTime;
    private DateTime startTimeCase;
    private TestCaseInitParameter initParameter;
    private TestSuite testSuite;
    private String browserProcessName;

    public static Path resolveResource(Class<?> aClass, String resourceName) {
        try {
            return ResourceHelper.getClasspathResource(aClass, resourceName, "cannot resolve resource '" + resourceName + "' from classpath!");
        } catch (NoSuchFileException e) {
            throw new SakuliRuntimeException("cannot resolve resource '" + resourceName + "' from classpath!", e);
        }
    }

    private static Object getField(Object target, String name) {
        if (target != null) {
            Field field = ReflectionUtils.findField(target.getClass(), name);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return ReflectionUtils.getField(field, target);
            }
        }
        return null;
    }

    abstract protected TestCaseInitParameter getTestCaseInitParameter() throws Exception;

    /**
     * Initialize the Spring context of the Sakuli test suite and invokes all configured Initializing Services.
     *
     * @throws FileNotFoundException
     */
    protected void initTestSuiteParameter() throws FileNotFoundException {
        LOGGER.info("............................INITIALIZE SAKULI-CONTEXT");
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
        String packageName = "/sahi";
        Path sahHomeFolder = resolveResource(AbstractSakuliTest.class, packageName);
        if (Files.exists(sahHomeFolder)) {
            return sahHomeFolder.normalize().toAbsolutePath().toString();
        }
        throw new SakuliRuntimeException("Cannot load SAHI_HOME folder! Should be normally under 'target/classes/" + packageName + "'");
    }

    @BeforeClass(alwaysRun = true)
    public void initTC() throws Exception {
        if (testSuite == null) {
            initTestSuiteParameter();
        }
        LOGGER.info("............................INITIALIZE TEST-CASE with {}", initParameter);
        String testCaseName = this.getClass().getSimpleName();
        initParameter = getTestCaseInitParameter();
        if (initParameter == null) {
            throw new SakuliCheckedException("init parameter have to be set!");
        }
        testSuite = BeanLoader.loadBean(TestSuite.class);

        //start sahi controlled browser if needed
        initSahiBrowser();

        testSuite.addTestCase(new TestCaseBuilder(testCaseName, initParameter.getTestCaseId()).build());
        testCaseAction = BeanLoader.loadTestCaseAction();

        //add the the testcase folder as image folder
        if (Files.exists(getTestCaseFolder())) {
            initParameter.addImagePath(getTestCaseFolder().toString());
        }
        initTestCaseAction(initParameter);
        LOGGER.info("............................START TEST-CASE '{}' - {}", initParameter.getTestCaseId(), testCaseName);
        counter = 0;
        startTimeCase = DateTime.now();
    }

    private void initSahiBrowser() {
        browser = BeanLoader.loadBean(SahiInitializingService.class).getBrowser();
        browserProcessName = String.valueOf(getField(browser, "browserProcessName"));
    }

    /**
     * Initialize the image folders, warning time and critical time for the current testcase with the assigned
     * initParameter.
     *
     * @param initParameter a initialized object of {@link TestCaseInitParameter}.
     */
    protected void initTestCaseAction(TestCaseInitParameter initParameter) {
        List<Path> imagePaths = initParameter.getImagePaths();
        testCaseAction.initWithPaths(this.initParameter.getTestCaseId(),
                this.initParameter.getWarningTime(),
                this.initParameter.getCriticalTime(),
                imagePaths.toArray(new Path[imagePaths.size()])
        );
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
                0,
                0,
                false
        );
    }

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
                null,
                false
        );
        if (browser != null) {
            browser.close();
        }
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
            LOGGER.info("========== TEAR-DOWN SAKULI TEST SUITE '{}' ==========", testSuite.getId());
            testSuite.setStopDate(DateTime.now().toDate());
            TeardownServiceHelper.invokeTeardownServices(testSuite);
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }

        if (StringUtils.isNotEmpty(browserProcessName) && !browserProcessName.equals("null")) {
            LOGGER.info("kill browser process '{}'", browserProcessName);
            ProcessHelper.killAll(browserProcessName);
        }
    }

}
