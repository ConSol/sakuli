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
import org.sakuli.exceptions.SakuliExceptionHandler;

import java.nio.file.Path;

/**
 * @author Tobias Schneck
 */
public interface BaseActionLoader {

    SakuliExceptionHandler getExceptionHandler();

    TestSuite getTestSuite();

    TestCase getCurrentTestCase();

    /**
     * Sets the current {@link TestCase} during execution of a {@link TestSuite}.
     */
    void setCurrentTestCase(TestCase testCase);

    /**
     * @return the first Step in state {@link org.sakuli.datamodel.state.TestCaseStepState#INIT}
     * or {@link org.sakuli.datamodel.state.TestCaseStepState#ERRORS} in the current {@link TestCase},
     * if there is one present.
     */
    TestCaseStep getCurrentTestCaseStep();

    ImageLib getImageLib();

    RhinoScriptRunner getRhinoScriptRunner();

    void setRhinoScriptRunner(RhinoScriptRunner scriptRunner);

    Report getSahiReport();

    CipherUtil getCipherUtil();

    /**
     * init method to signalise the context that a new {@link TestCase} starts.
     *
     * @param testCaseID {@link TestCase#id}
     * @param imagePaths paths to the located image patterns
     */
    void init(String testCaseID, String... imagePaths);

    /**
     * init method to signalise the context that a new {@link TestCase} starts.
     *
     * @param testCaseID {@link TestCase#id}
     * @param imagePaths paths to the located image patterns
     */
    void init(String testCaseID, Path... imagePaths);

    SakuliProperties getSakuliProperties();

    ActionProperties getActionProperties();

    SahiProxyProperties getSahiProxyProperties();

    TestSuiteProperties getTestSuitePropeties();
}
