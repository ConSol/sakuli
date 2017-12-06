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

package org.sakuli.actions.environment;

import org.sakuli.actions.logging.LogToResult;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.loader.BeanLoader;
import org.sakuli.loader.ScreenActionLoader;
import org.sikuli.basics.Settings;
import org.sikuli.natives.CommandExecutorHelper;
import org.sikuli.script.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.sakuli.utils.SystemHelper.sleep;

/**
 * @author Tobias Schneck
 */

public class Application extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final boolean resumeOnException;

    private ScreenActionLoader loader;
    private Long sleepMillis = 1000L;

    /**
     * Creates a new Application of the name or path to the executable.
     *
     * @param appName name of path of the application.
     */
    public Application(String appName) {
        this(appName, false);
    }

    /**
     * Creates a new Application of the name or path to the executable.
     *
     * @param appName           name of path of the application.
     * @param resumeOnException if true the execution of the script wont't stop at an exception
     */
    // TODO: add aspect for constructor
    @LogToResult(message = "create application", logClassInstance = false)
    public Application(String appName, boolean resumeOnException) {
        super(appName);
        this.loader = BeanLoader.loadScreenActionLoader();
        this.resumeOnException = resumeOnException;
    }

    /**
     * Opens the created application. For loadtime intensiv application change the default sleep time with {@link
     * #setSleepTime(Integer)}.
     *
     * @return this {@link Application}.
     */
    @LogToResult(message = "open application")
    @Override
    public Application open() {
        App app = super.open();
        if (app == null) {
            loader.getExceptionHandler().handleException("Application '" + this.getName() + " could not be opened! ... Please check the application name or path!", resumeOnException);
            return null;
        }
        /*** automatic sleep for long loading applications, see {@link #setSleepTime(Integer)} **/
        sleep(sleepMillis);

        final String appID = "" + app.getName() +
                ((app.getPID() != null && app.getPID() > 0) ? " PID:" + app.getPID() : "");
        final int tries = 5;
        LOGGER.debug("verify app " + appID + " is running");
        if (!app.isRunning(tries)) {
            LOGGER.warn("verified {} times if application '" + appID + "' is running", tries);
            loader.getExceptionHandler().handleException("Application '" + appID + "' is not running", resumeOnException);
        }
        LOGGER.info("Application '" + appID + "' is running");
        return this;
    }

    /**
     * focus the current application, if the application is in the background.
     *
     * @return this {@link Application}.
     */
    @Override
    public Application focus() {
        return focusWindow(0);
    }

    /**
     * focus a specific window of the application.
     *
     * @param windowNumber indemnifies the window
     * @return this {@link Application}.
     */
    @LogToResult(message = "focus application in window")
    public Application focusWindow(Integer windowNumber) {
        LOGGER.debug("Focus window \"" + windowNumber + "\" in application \"" + getName() + "\".");
        App app = super.focus(windowNumber);
        if (app == null) {
            LOGGER.warn("Application '{}' could not be focused! ... Please check if the application has been opened before or is already focused!", getName());
            return this;
        }
        /*** automatic sleep for long loading applications, see {@link #setSleepTime(Integer)} **/
        sleep(sleepMillis);
        return this;
    }

    /**
     * close the already existing application.
     *
     * @return this {@link Application}.
     */
    public Application closeApp() {
        return closeApp(false);
    }

    /**
     * close the already existing application.
     *
     * @param silent if true, no exception  will be thrown on errors and stop the test execution.
     * @return this {@link Application}.
     */
    @LogToResult
    public Application closeApp(boolean silent) {
        LOGGER.info("Close application with name or path \"" + getName() + "\".");
        int retValue = -1;
        try {
            retValue = super.close();
        } catch (Throwable e) {
            LOGGER.error("ERROR in closing Application", e);
        }
        if (!silent && retValue != 0) {
            loader.getExceptionHandler().handleException("Application '" + getName() + " could not be closed! ... Please check if the application has been opened before!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * Kill the already existing application hardly
     *
     * @return this {@link Application}.
     */
    public Application kill() {
        return kill(false);
    }

    /**
     * Kill the already existing application hardly.
     *
     * @param silent if true, no exception  will be thrown on errors.
     * @return this {@link Application}.
     */
    @LogToResult
    public Application kill(boolean silent) {
        try {
            if (getPID() < 1) {
                return killAppName(getName());
            }
            return killAppPID(getPID());
        } catch (SakuliException e) {
            if (!silent) {
                loader.getExceptionHandler().handleException(e);
                return null;
            }
            LOGGER.debug(e.getMessage(), e);
        }
        return this;
    }

    private Application killAppName(String name) throws SakuliException {
        try {
            String cmd = String.format(
                    Settings.isWindows() ? "Taskkill /IM \"%s\" /F" : "pkill \"%s\""
                    , name);
            CommandExecutorHelper.execute(cmd, 0);
        } catch (Exception e) {
            throw new SakuliException(e, String.format("could not kill application with name '%s'.", name));
        }
        return this;
    }

    private Application killAppPID(Integer pid) throws SakuliException {
        try {
            String cmd = String.format(
                    Settings.isWindows() ? "Taskkill /PID %d /F" : "kill -9 %d"
                    , pid);
            CommandExecutorHelper.execute(cmd, 0);
        } catch (Exception e) {
            throw new SakuliException(e, String.format("could not kill application with PID '%d'.", pid));
        }
        return this;
    }

    /**
     * sets the sleep time in seconds of the application actions to handle with long loading times. The default sleep
     * time is set to 1 seconds
     *
     * @param seconds sleep time in seconds
     * @return this {@link Application}.
     */
    @LogToResult
    public Application setSleepTime(Integer seconds) {
        this.sleepMillis = TimeUnit.SECONDS.toMillis(seconds);
        return this;
    }

    /**
     * creats and returns a {@link Region} object from the application.
     *
     * @return this {@link Application}
     */
    @LogToResult(message = "get a Region object from the application")
    public Region getRegion() {
        org.sikuli.script.Region window = super.window();
        if (window == null) {
            loader.getExceptionHandler().handleException("Could not identify Region for application \"" + getName() + "\"", resumeOnException);
            return null;
        }
        return new Region(window, resumeOnException);
    }

    /**
     * creats and returns a {@link Region} object from the specific window of the application.
     *
     * @param windowNumber indemnifies the window
     * @return this {@link Application}
     */
    @LogToResult(message = "get a Region object from the window of the application ")
    public Region getRegionForWindow(int windowNumber) {
        org.sikuli.script.Region window = super.window(windowNumber);
        if (window == null) {
            loader.getExceptionHandler().handleException("Could not identify Region for window \"" + windowNumber + "\" of application \"" + getName() + "\"", resumeOnException);
            return null;
        }
        return new Region(window, resumeOnException);
    }

    /**
     * @return the name of the current application as {@link String}.
     */
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
