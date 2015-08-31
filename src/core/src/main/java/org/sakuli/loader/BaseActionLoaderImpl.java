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

package org.sakuli.loader;

import net.sf.sahi.report.Report;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.sakuli.actions.environment.CipherUtil;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.ImageLib;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * @author Tobias Schneck
 */
@Qualifier(BaseActionLoaderImpl.QUALIFIER)
@Component
public class BaseActionLoaderImpl implements BaseActionLoader {

    public final static String QUALIFIER = "baseLoader";
    public static final Logger LOGGER = LoggerFactory.getLogger(BaseActionLoader.class);


    @Autowired
    private SakuliExceptionHandler exceptionHandler;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private TestSuiteProperties testSuiteProperties;
    @Autowired
    private CipherUtil cipherUtil;
    @Autowired
    private SakuliProperties sakuliProperties;
    @Autowired
    private ActionProperties actionProperties;
    @Autowired
    private SahiProxyProperties sahiProxyProperties;

    /**
     * ** Fields which will be filled at runtime ***
     */
    private TestCase currentTestCase;
    private RhinoScriptRunner rhinoScriptRunner;
    private ImageLib imageLib;

    @Override
    public void init(String testCaseID, String... imagePaths) {
        List<Path> pathList = new ArrayList<>();
        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                pathList.add(Paths.get(imagePath));
            }
        }
        init(testCaseID, pathList.toArray(new Path[pathList.size()]));
    }

    /**
     * init function, which should be called on the beginning of every test case.
     *
     * @param imagePaths paths to relevant pictures
     * @param testCaseID id of the corresponding test case
     */
    @Override
    public void init(String testCaseID, Path... imagePaths) {
        try {
            //set the current test case
            if (testSuite.getTestCase(testCaseID) == null) {
                throw new SakuliException("Can't identify current test case in function init() in class SakuliBasedAction");
            }
            this.currentTestCase = testSuite.getTestCase(testCaseID);

            //load the images for the screenbased actions
            if (imagePaths == null || imagePaths.length <= 0) {
                throw new SakuliException("To init the internal image library, the imagePaths have to be not null and have at least one file path!");
            }
            this.imageLib = new ImageLib();
            imageLib.addImagesFromFolder(imagePaths);

            if (sakuliProperties.isLoadJavaScriptEngine()) {
                //add the "sakuli-delay-active" var to the script runner context
                if (rhinoScriptRunner == null || rhinoScriptRunner.getSession() == null) {
                    //could be possible if the aspectj compiler won't worked correctly, see RhinoAspect#getRhinoScriptRunner
                    throw new SakuliException(String.format("cannot init rhino script runner with sakuli custom delay variable '%s'",
                            SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR));
                }
                String isRequestDelayActive = String.valueOf(sahiProxyProperties.isRequestDelayActive());
                rhinoScriptRunner.getSession().setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_ACTIVE_VAR, isRequestDelayActive);
                LOGGER.info("set isRequestDelayActive={}", isRequestDelayActive);
            }
        } catch (SakuliException | IOException e) {
            exceptionHandler.handleException(e);
        }
    }

    @Override
    public SakuliProperties getSakuliProperties() {
        return this.sakuliProperties;
    }

    public void setSakuliProperties(SakuliProperties sakuliProperties) {
        this.sakuliProperties = sakuliProperties;
    }

    @Override
    public ActionProperties getActionProperties() {
        return this.actionProperties;
    }

    public void setActionProperties(ActionProperties actionProperties) {
        this.actionProperties = actionProperties;
    }

    @Override
    public SahiProxyProperties getSahiProxyProperties() {
        return this.sahiProxyProperties;
    }

    public void setSahiProxyProperties(SahiProxyProperties sahiProxyProperties) {
        this.sahiProxyProperties = sahiProxyProperties;
    }

    @Override
    public TestSuiteProperties getTestSuitePropeties() {
        return this.testSuiteProperties;
    }

    public void setTestSuiteProperties(TestSuiteProperties testSuiteProperties) {
        this.testSuiteProperties = testSuiteProperties;
    }

    @Override
    public SakuliExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(SakuliExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    @Override
    public TestCase getCurrentTestCase() {
        return currentTestCase;
    }

    public void setCurrentTestCase(TestCase currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    @Override
    public TestCaseStep getCurrentTestCaseStep() {
        if (currentTestCase != null) {
            SortedSet<TestCaseStep> steps = currentTestCase.getStepsAsSortedSet();
            if (!steps.isEmpty()) {
                for (TestCaseStep step : steps) {
                    step.refreshState();
                    //find first step with init state and returns it
                    if (!step.getState().isFinishedWithoutErrors()) {
                        return step;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ImageLib getImageLib() {
        return imageLib;
    }

    public void setImageLib(ImageLib imageLib) {
        this.imageLib = imageLib;
    }

    @Override
    public RhinoScriptRunner getRhinoScriptRunner() {
        return rhinoScriptRunner;
    }

    public void setRhinoScriptRunner(RhinoScriptRunner rhinoScriptRunner) {
        this.rhinoScriptRunner = rhinoScriptRunner;

    }

    @Override
    public Report getSahiReport() {
        return rhinoScriptRunner == null ? null : rhinoScriptRunner.getReport();
    }

    @Override
    public CipherUtil getCipherUtil() {
        return cipherUtil;
    }

    public void setCipherUtil(CipherUtil cipherUtil) {
        this.cipherUtil = cipherUtil;
    }
}
