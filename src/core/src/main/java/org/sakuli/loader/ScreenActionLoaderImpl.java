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
import org.sakuli.actions.screenbased.ScreenshotActions;
import org.sakuli.actions.settings.ScreenBasedSettings;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.ImageLib;
import org.sakuli.datamodel.actions.Screen;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.datamodel.properties.TestSuiteProperties;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Loads all necessary objects for the {@link org.sakuli.actions.screenbased.ScreenshotActions}.
 *
 * @author tschneck Date: 18.10.13
 */
@Component
public class ScreenActionLoaderImpl implements ScreenActionLoader {

    @Qualifier(BaseActionLoaderImpl.QUALIFIER)
    @Autowired
    private BaseActionLoader baseLoader;

    @Autowired
    private ScreenBasedSettings settings;
    @Autowired
    private ScreenshotActions screenshotActions;
    @Autowired
    private Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public ScreenshotActions getScreenshotActions() {
        return screenshotActions;
    }

    public void setScreenshotActions(ScreenshotActions screenshotActions) {
        this.screenshotActions = screenshotActions;
    }

    @Override
    public BaseActionLoader getBaseLoader() {
        return baseLoader;
    }

    public void setBaseLoader(BaseActionLoader baseLoader) {
        this.baseLoader = baseLoader;
    }

    @Override
    public void loadSettingDefaults() {
        getSettings().setDefaults();
    }

    @Override
    public ScreenBasedSettings getSettings() {
        return settings;
    }

    public void setSettings(ScreenBasedSettings settings) {
        this.settings = settings;
    }

    @Override
    public CipherUtil getCipherUtil() {
        return baseLoader.getCipherUtil();
    }

    @Override
    public void init(String testCaseID, String... imagePaths) {
        baseLoader.init(testCaseID, imagePaths);
    }

    @Override
    public void init(String testCaseID, Path... imagePaths) {
        baseLoader.init(testCaseID, imagePaths);
    }

    @Override
    public SakuliProperties getSakuliProperties() {
        return baseLoader.getSakuliProperties();
    }

    @Override
    public ActionProperties getActionProperties() {
        return baseLoader.getActionProperties();
    }

    @Override
    public SahiProxyProperties getSahiProxyProperties() {
        return baseLoader.getSahiProxyProperties();
    }

    @Override
    public TestSuiteProperties getTestSuitePropeties() {
        return baseLoader.getTestSuitePropeties();
    }

    @Override
    public SakuliExceptionHandler getExceptionHandler() {
        return baseLoader.getExceptionHandler();
    }

    @Override
    public TestSuite getTestSuite() {
        return baseLoader.getTestSuite();
    }

    @Override
    public TestCase getCurrentTestCase() {
        return baseLoader.getCurrentTestCase();
    }

    @Override
    public void setCurrentTestCase(TestCase testCase) {
        baseLoader.setCurrentTestCase(testCase);
    }

    @Override
    public TestCaseStep getCurrentTestCaseStep() {
        return baseLoader.getCurrentTestCaseStep();
    }

    @Override
    public ImageLib getImageLib() {
        return baseLoader.getImageLib();
    }

    @Override
    public RhinoScriptRunner getRhinoScriptRunner() {
        return baseLoader.getRhinoScriptRunner();
    }

    @Override
    public void setRhinoScriptRunner(RhinoScriptRunner scriptRunner) {
        baseLoader.setRhinoScriptRunner(scriptRunner);
    }

    @Override
    public Report getSahiReport() {
        return baseLoader.getSahiReport();
    }
}
