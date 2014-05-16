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

package de.consol.sakuli.exceptions;

import de.consol.sakuli.actions.screenbased.RegionImpl;
import de.consol.sakuli.aop.RhinoAspect;
import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.actions.LogResult;
import de.consol.sakuli.loader.ScreenActionLoader;
import net.sf.sahi.report.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author tschneck
 *         Date: 12.07.13
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@Component
public class SakuliExceptionHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScreenActionLoader loader;

    public static String getAllExceptionMessages(Throwable e) {
        if (e != null) {
            String msg = format(e.getMessage());
            //add suppressed exceptions
            for (Throwable ee : e.getSuppressed()) {
                msg += "\n\t\t Suppressed EXCEPTION: " + format(ee.getMessage());
            }
            return msg;
        } else {
            return null;
        }
    }

    private static String format(String message) {
        if (message.contains(":")) {
            return message.substring(message.indexOf(":") + 1);
        }
        return message;
    }

    /**
     * Checks if a {@link Throwable} is an instance of the {@link SakuliExceptionWithScreenshot}.
     *
     * @param e any {@link Throwable}
     * @return If true the method returns a valid {@link Path}.
     */
    public static Path getScreenshotFile(Throwable e) {
        if (e instanceof SakuliExceptionWithScreenshot) {
            if (((SakuliExceptionWithScreenshot) e).getScreenshot() != null) {
                return ((SakuliExceptionWithScreenshot) e).getScreenshot();
            }
        }
        if (e != null && e.getSuppressed() != null) {
            for (Throwable ee : e.getSuppressed()) {
                if (ee instanceof SakuliExceptionWithScreenshot) {
                    return ((SakuliExceptionWithScreenshot) ee).getScreenshot();
                }
            }
        }
        //retrun null if nothing matches
        return null;
    }

    /**
     * handleException methode for Eception, where no testcase could be identified;
     * The default value for non {@link SakuliException} is there that the Execution of the tescase will stop!
     *
     * @param e any Throwable
     */
    public void handleException(Throwable e) {
        //Proxy Exception should only be handled if no other exceptions have been added
        if (!(e instanceof SakuliProxyException) ||
                (!containsException(loader.getTestSuite()))) {
            //if the exception have been already handled do no exception handling!
            if (!e.getMessage().contains(RhinoAspect.ALREADY_HANDELED)
                    && !e.getMessage().contains(("Logging exception:"))) {

                SakuliException sakuliException = transformException(e);

                //Do different exception handling for different use cases:
                if (sakuliException.resumeOnException
                        && loader.getSakuliProperties().isLogResumOnException()) {
                    logger.error(sakuliException.getMessage(), sakuliException);
                    saveException(sakuliException);
                    addExceptionToSahiReport(sakuliException);
                } else if (sakuliException.resumeOnException &&
                        !loader.getSakuliProperties().isLogResumOnException()) {
                    logger.debug(sakuliException.getMessage(), sakuliException);
                } else {
                    logger.error(sakuliException.getMessage(), sakuliException);
                    saveException(sakuliException);

                    /**
                     * don't do this for errors from Sahi see implemenation of
                     * {@link RhinoAspect#
                     */
                    if (!(e instanceof SahiActionException)) {
                        stopExecutionAndAddExceptionToSahiReport(sakuliException);
                    }
                }
            } else {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    private boolean containsException(TestSuite testSuite) {
        if (testSuite.getException() != null) {
            return true;
        }
        if (!CollectionUtils.isEmpty(testSuite.getTestCases())) {
            for (TestCase tc : testSuite.getTestCases().values()) {
                if (tc.getException() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * save the exception to the current testcase.
     * If the current testcase is not reachale, then the exception will be saved to the test suite.
     *
     * @param e any {@link SakuliException}
     */
    private void saveException(SakuliException e) {
        if (loader.getCurrentTestCase() != null) {
            loader.getCurrentTestCase().addException(e);
        } else {
            loader.getTestSuite().addException(e);
        }
        loader.getTestSuite().refreshState();
    }

    /**
     * save the exception to the current sahi report (HTML Report in the log folder).
     *
     * @param e any {@link SakuliException}
     */
    private void addExceptionToSahiReport(SakuliException e) {
        loader.getSahiReport().addResult(
                e.getMessage(),
                ResultType.ERROR,
                e.getStackTrace().toString(),
                e.getMessage() + RhinoAspect.ALREADY_HANDELED);
    }

    /**
     * stops the execution of the current test case and add the exception to the sahi report (HTML Report in the log folder).
     *
     * @param e any {@link SakuliException}
     */
    private void stopExecutionAndAddExceptionToSahiReport(SakuliException e) {
        if (loader.getRhinoScriptRunner() != null
                && !e.resumeOnException) {

            loader.getRhinoScriptRunner().setStopOnError(e.resumeOnException);
            loader.getRhinoScriptRunner().setHasError();
            throw new RuntimeException(RhinoAspect.ALREADY_HANDELED + e.getMessage());
        }
    }


    /**
     * transforms any {@link Throwable} to SakuliException.
     * If the property 'sakuli.takeScreenShots.onErrors=true' is set, the methods add a Screenshot.
     *
     * @param e a {@link Throwable}
     * @return <EX>  {@link SakuliException} or any child.
     */
    private SakuliException transformException(Throwable e) {
        boolean resumeOnException = false;
        if (e instanceof SakuliException) {
            resumeOnException = ((SakuliException) e).resumeOnException;
        }
        if (loader.getActionProperties().isTakeScreenshots()) {
            //try to get a screenshot
            try {
                Path screenshot = null;
                if (e instanceof SakuliActionException) {
                    screenshot = loader.getScreenshotActions().takeScreenshotAndHighlight(
                            e.getMessage(),
                            loader.getActionProperties().getScreenShotFolder(),
                            ((SakuliActionException) e).getLastRegion()
                    );
                }
                if (screenshot == null) {
                    screenshot = loader.getScreenshotActions().takeScreenshot(
                            e.getMessage(),
                            loader.getActionProperties().getScreenShotFolder());
                }
                return new SakuliExceptionWithScreenshot(e, screenshot, resumeOnException);
            } catch (IOException e2) {
                logger.error("Screenshot could not be created", e2);
                e.addSuppressed(e2);
            }
        }
        return new SakuliException(e, resumeOnException);
    }


    public void handleException(Throwable e, boolean resumeOnException) {
        handleException(new SakuliException(e, resumeOnException));
    }

    public void handleException(String exceptionMessage, boolean resumeOnException) {
        handleException(new SakuliException(exceptionMessage, resumeOnException));
    }

    public void handleException(String exceptionMessage, RegionImpl lastRegion, boolean resumeOnException) {
        handleException(new SakuliActionException(exceptionMessage, lastRegion, resumeOnException));
    }

    public void handleException(Throwable e, RegionImpl lastRegion, boolean resumeOnException) {
        handleException(new SakuliActionException(e, lastRegion, resumeOnException));
    }

    public void handleException(LogResult logResult) {
        handleException(new SahiActionException(logResult));
    }
}
