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

package de.consol.sakuli.loader;

import de.consol.sakuli.actions.environment.CipherUtil;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.actions.ImageLib;
import de.consol.sakuli.datamodel.properties.ActionProperties;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import net.sf.sahi.report.Report;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Tobias Schneck
 */
@Qualifier(BaseActionLoaderImpl.QUALIFIER)
@Component
public class BaseActionLoaderImpl implements BaseActionLoader {

    public final static String QUALIFIER = "baseLoader";


    @Autowired
    private SakuliExceptionHandler exceptionHandler;
    @Autowired
    private TestSuite testSuite;
    @Autowired
    private CipherUtil cipherUtil;
    @Autowired
    private SakuliProperties sakuliProperties;
    @Autowired
    private ActionProperties actionProperties;
    /**
     * ** Fields which will be filled at runtime ***
     */
    private TestCase currentTestCase;
    private RhinoScriptRunner rhinoScriptRunner;
    private ImageLib imageLib;


    /**
     * init function, which should be called on the beginning of every test case.
     *
     * @param imagePaths paths to relevant pictures
     * @param testCaseID id of the corresponding test case
     */
    public void init(String testCaseID, String... imagePaths) {
        try {
            if (testSuite.getTestCase(testCaseID) != null) {
                this.currentTestCase = testSuite.getTestCase(testCaseID);
            } else {
                throw new SakuliException("Can't identify current test case in function init() in class SakuliBasedAction");
            }
            if (imagePaths != null && imagePaths.length > 0) {
                this.imageLib = new ImageLib();
                imageLib.addImagesFromFolder(imagePaths);
            } else {
                throw new SakuliException("To init the internal image library, the imagePaths have to be not null and have at least one file path!");
            }
        } catch (SakuliException | IOException e) {
            exceptionHandler.handleException(e);
        }
    }

    @Override
    public SakuliProperties getSakuliProperties() {
        return this.sakuliProperties;
    }

    @Override
    public ActionProperties getActionProperties() {
        return this.actionProperties;
    }

    public void setActionProperties(ActionProperties actionProperties) {
        this.actionProperties = actionProperties;
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
        return rhinoScriptRunner.getReport();
    }

    @Override
    public CipherUtil getCipherUtil() {
        return cipherUtil;
    }

    public void setCipherUtil(CipherUtil cipherUtil) {
        this.cipherUtil = cipherUtil;
    }
}
