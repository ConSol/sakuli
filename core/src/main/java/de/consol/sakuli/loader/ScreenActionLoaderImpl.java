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
import de.consol.sakuli.actions.screenbased.ScreenshotActions;
import de.consol.sakuli.actions.settings.ScreenBasedSettings;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.actions.ImageLib;
import de.consol.sakuli.datamodel.actions.Screen;
import de.consol.sakuli.exceptions.SakuliExceptionHandler;
import net.sf.sahi.report.Report;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Loads all necessary objects for the {@link de.consol.sakuli.actions.screenbased.ScreenshotActions}.
 *
 * @author tschneck
 *         Date: 18.10.13
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

    @Override
    public ScreenshotActions getScreenshotActions() {
        return screenshotActions;
    }

    @Override
    public ScreenBasedSettings getSettings() {
        return settings;
    }

    @Override
    public CipherUtil getCipherUtil() {
        return baseLoader.getCipherUtil();
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
    public ImageLib getImageLib() {
        return baseLoader.getImageLib();
    }

    @Override
    public RhinoScriptRunner getRhinoScriptRunner() {
        return baseLoader.getRhinoScriptRunner();
    }

    @Override
    public Report getSahiReport() {
        return baseLoader.getSahiReport();
    }
}
