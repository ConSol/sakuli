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

package de.consol.sakuli.actions.environment;

import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.actions.screenbased.Region;
import de.consol.sakuli.loader.ScreenActionLoader;
import org.sikuli.script.App;

import java.util.concurrent.TimeUnit;

/**
 * @author Tobias Schneck
 */

public class Application extends App {
    protected final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private final boolean resumeOnException;

    private ScreenActionLoader loader;
    private Integer sleepMillis = 1000;

    /**
     * Creates a new Application of the name or path to the executable.
     *
     * @param appName           name of path of the application.
     * @param resumeOnException if true the execution of the script wont't stop at an exception
     */
    // TODO: add aspect for constructor
    @LogToResult(message = "create application", logClassInstance = false)
    public Application(String appName, boolean resumeOnException, ScreenActionLoader loader) {
        super(appName);
        this.loader = loader;
        this.resumeOnException = resumeOnException;
    }

    /**
     * Opens the created application.
     * For loadtime intensiv application change the default sleep time with {@link #setSleepTime(Integer)}.
     *
     * @return this {@link Application}.
     */
    @LogToResult(message = "open application")
    @Override
    public Application open() {
        App app = super.open();
        sleep(sleepMillis);
        if (app == null) {
            loader.getExceptionHandler().handleException("Application '" + getName() + " could not be opend! ... Please check the application name or path!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * focus the current application, if the application is in the background.
     *
     * @return this {@link Application}.
     */
    @LogToResult(message = "focus application")
    @Override
    public Application focus() {
        App app = super.focus();
        sleep(sleepMillis);
        if (app == null) {
            loader.getExceptionHandler().handleException("Application '" + getName() + "\" could not be focused! ... Please check if the application has been opened before or is already focused!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * focus a specific window of the application.
     *
     * @param windowNumber indemnifies the window
     * @return this {@link Application}.
     */
    @LogToResult(message = "focus application in window")
    public Application focusWindow(Integer windowNumber) {
        logger.info("Focus window \"" + windowNumber + "\" in application \"" + getName() + "\".");
        App app = super.focus(windowNumber);
        sleep(sleepMillis);
        if (app == null) {
            loader.getExceptionHandler().handleException("Application '" + getName() + " with window \"" + windowNumber + "\" could not be focused! ... Please check if the application has been opened before!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * close the already existing application.
     *
     * @return this {@link Application}.
     */
    @LogToResult
    public Application closeApp() {
        logger.info("Close application with name or path \"" + getName() + "\".");
        int retValue = super.close();
        sleep(sleepMillis);
        if (retValue < 0) {
            loader.getExceptionHandler().handleException("Application '" + getName() + " could not be closed! ... Please check if the application has been opened before!", resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * sets the sleep time in seconds of the application actions to handle with long loading times.
     * The default sleep time is set to 1 seconds
     *
     * @param seconds sleep time in seconds
     * @return this {@link Application}.
     */
    @LogToResult
    public Application setSleepTime(Integer seconds) {
        this.sleepMillis = (int) TimeUnit.SECONDS.toMillis(seconds);
        return this;
    }

    /**
     * creats and returns a {@link Region} object from the application.
     *
     * @return this {@link Application}
     */
    @LogToResult(message = "get a Region object from the application")
    public Region getRegion() {
        Region region = new Region(super.window(), resumeOnException, loader);
        if (region == null) {
            loader.getExceptionHandler().handleException("Could not identify Region for application \"" + getName() + "\"", resumeOnException);
            return null;
        }
        return region;
    }

    /**
     * creats and returns a {@link Region} object from the specific window of the application.
     *
     * @param windowNumber indemnifies the window
     * @return this {@link Application}
     */
    @LogToResult(message = "get a Region object from the window of the application ")
    public Region getRegionForWindow(int windowNumber) {
        Region region = new Region(super.window(windowNumber), resumeOnException, loader);
        if (region == null) {
            loader.getExceptionHandler().handleException("Could not identify Region for window \"" + windowNumber + "\" of application \"" + getName() + "\"", resumeOnException);
            return null;
        }
        return region;
    }

    /**
     * @return the name of the current application as {@link String}.
     */
    public String getName() {
        return super.name();
    }


    private void sleep(Integer milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
