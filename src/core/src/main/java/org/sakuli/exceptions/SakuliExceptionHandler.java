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

package org.sakuli.exceptions;

import net.sf.sahi.report.ResultType;
import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.aop.RhinoAspect;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestCaseStep;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.LogResult;
import org.sakuli.loader.ScreenActionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tschneck Date: 12.07.13
 */
@Component
public class SakuliExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScreenActionLoader loader;
    private List<Exception> processedExceptions = new ArrayList<>();
    private List<Exception> resumeExceptions = new ArrayList<>();

    /**
     * @return all exceptions from the test suite and there underlying test cases.
     */
    public static List<Exception> getAllExceptions(TestSuite testSuite) {
        List<Exception> result = new ArrayList<>();
        if (testSuite.getException() != null) {
            result.add(testSuite.getException());
        }
        if (testSuite.getTestCases() != null) {
            for (TestCase tc : testSuite.getTestCases().values()) {
                if (tc.getException() != null) {
                    result.add(tc.getException());
                }
                for (TestCaseStep step : tc.getSteps()) {
                    if (step.getException() != null) {
                        result.add(step.getException());
                    }
                }

            }
        }

        return result;
    }

    /**
     * Checks if a {@link Exception} is an instance of the {@link SakuliExceptionWithScreenshot}.
     *
     * @param e any {@link Exception}
     * @return If true the method returns a valid {@link Path}.
     */
    public static Path getScreenshotFile(Exception e) {
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

    static boolean containsException(TestSuite testSuite) {
        if (testSuite.getException() != null) {
            return true;
        }
        if (!CollectionUtils.isEmpty(testSuite.getTestCases())) {
            for (TestCase tc : testSuite.getTestCases().values()) {
                if (tc.getException() != null) {
                    return true;
                }
                for (TestCaseStep step : tc.getSteps()) {
                    if (step.getException() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Exception castTo(SakuliException e) {
        if (e instanceof Exception) {
            return (Exception) e;
        }
        throw new ClassCastException("SakuliException should extend from Exception class!");
    }

    /**
     * handleException method for exception where no testcase could be identified; The default value for non {@link
     * SakuliException} is there that the Execution of the tescase will stop!
     *
     * @param e any Exception
     */
    public void handleException(Exception e) {
        //avoid nullpointer for missing messages
        if (e.getMessage() == null) {
            e = new SakuliCheckedException(e, e.getClass().getSimpleName());
        }

        //e.g. Proxy Exception should only be handled if no other exceptions have been added
        if (!(e instanceof SakuliInitException) ||
                (!containsException(loader.getTestSuite()))) {
            //if the exception have been already processed do no exception handling!
            if (isAlreadyProcessed(e)) {
                logger.debug("ALREADY PROCESSED: " + e.getMessage(), e);
            } else {
                processException(e);
            }
        }
    }

    /**
     * Finally transfromes and saves the exception. This method should be called if the exception should really be
     * processed.
     *
     * @param e any {@link Exception}
     */
    protected void processException(Exception e) {
        SakuliException transformedException = transformException(e);

        //Do different exception handling for different use cases:
        if (!resumeToTestExecution(e)) {
            //normal handling
            logger.error(transformedException.getMessage(), transformedException);
            saveException(transformedException.castTo());

            // a {@link SakuliForwarderException}, should only added to the report and not stop sahi, because
            // this error types only on already started the tear down of test suite.
            if (e instanceof SakuliForwarderException) {
                addExceptionToSahiReport(transformedException);
            }
            //stop the execution and add to report if the exception is not caused by Sahi
            else if (!(e instanceof SahiActionException)) {
                stopExecutionAndAddExceptionToSahiReport(transformedException);
            }
        }
        //if exceptions should not stop the test case execution
        else if (!loader.getSakuliProperties().isSuppressResumedExceptions()) {
            // if suppressResumedExceptions == false
            logger.error(e.getMessage(), transformedException);
            saveException(transformedException.castTo());
            addExceptionToSahiReport(transformedException);
        } else {
            //if suppressResumedExceptions == true
            logger.debug(transformedException.getMessage(), transformedException);
        }
        processedExceptions.add(e);
        processedExceptions.add(transformedException.castTo());
    }

    /**
     * @return true if the exception have been already processed by Sakuli
     */
    public boolean isAlreadyProcessed(Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        return message.contains(RhinoAspect.ALREADY_PROCESSED)
                || message.contains(("Logging exception:")) || processedExceptions.contains(e);
    }

    /**
     * @return true if, the exception should NOT stop the test case execution
     */
    public boolean resumeToTestExecution(Exception e) {
        return resumeExceptions.contains(e);
    }

    /**
     * save the exception to the current testcase. If the current testcase is not reachale, then the exception will be
     * saved to the test suite.
     *
     * @param e any {@link Exception}
     */
    void saveException(Exception e) {
        if (e instanceof SakuliException) {
            if (((SakuliException) e).getAsyncTestDataRef().isPresent()) {
                ((SakuliException) e).getAsyncTestDataRef().get().addException(e);
                //skip test handling tasks
                return;
            }
        }

        if (loader.getCurrentTestCase() != null) {
            if (loader.getCurrentTestCaseStep() != null) {
                loader.getCurrentTestCaseStep().addException(e);
                loader.getCurrentTestCaseStep().addActions(loader.getCurrentTestCase().getAndResetTestActions());
            } else {
                loader.getCurrentTestCase().addException(e);
            }
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
        if (loader.getSahiReport() != null) {
            loader.getSahiReport().addResult(
                    e.getMessage(),
                    ResultType.ERROR,
                    e.castTo().getStackTrace().toString(),
                    e.getMessage() + RhinoAspect.ALREADY_PROCESSED);
        }
    }

    /**
     * stops the execution of the current test case and add the exception to the sahi report (HTML Report in the log
     * folder).
     *
     * @param e any {@link SakuliException}
     */
    private void stopExecutionAndAddExceptionToSahiReport(SakuliException e) {
        if (loader.getRhinoScriptRunner() != null) {
            loader.getRhinoScriptRunner().setStopOnError(true);
            loader.getRhinoScriptRunner().setHasError();
            throw new RuntimeException(RhinoAspect.ALREADY_PROCESSED + e.getMessage());
        }
    }

    /**
     * transforms any {@link Exception} to SakuliException. If the property 'sakuli.screenshot.onError=true'
     * is set, the method add a screenshot.
     *
     * @param e a {@link Exception}
     * @return {@link SakuliException} or any child.
     */
    SakuliException transformException(Exception e) {
        if (!(e instanceof NonScreenshotException)
                && loader.getActionProperties().isTakeScreenshots()) {
            //try to get a screenshot
            try {
                Path screenshot = loader.getScreenshotActions().takeScreenshotWithTimestampThrowIOException(
                        e.getMessage(),
                        loader.getActionProperties().getScreenShotFolder(),
                        null,
                        null);
                return addResumeOnException(new SakuliExceptionWithScreenshot(e, screenshot), resumeToTestExecution(e));
            } catch (IOException e2) {
                logger.error("Screenshot could not be created", e2);
                e.addSuppressed(e2);
            }
        }
        if (SakuliCheckedException.class.isAssignableFrom(e.getClass())) {
            return addResumeOnException(((SakuliCheckedException) e), resumeToTestExecution(e));
        }
        if (e instanceof SakuliRuntimeException) {
            return addResumeOnException(((SakuliRuntimeException) e), resumeToTestExecution(e));
        }
        //not kind of SakuliException -> wrap it
        if (e instanceof RuntimeException) {
            return addResumeOnException(new SakuliRuntimeException(e), resumeToTestExecution(e));
        }
        return addResumeOnException(new SakuliCheckedException(e), resumeToTestExecution(e));
    }


    public void handleException(Exception e, boolean resumeOnException) {
        handleException(addResumeOnException(e, resumeOnException));
    }

    public void handleException(String exceptionMessage, boolean resumeOnException) {
        handleException(new SakuliCheckedException(exceptionMessage), resumeOnException);
    }

    public void handleException(String exceptionMessage, RegionImpl lastRegion, boolean resumeOnException) {
        handleException(addResumeOnException(
                new SakuliActionException(exceptionMessage, lastRegion),
                resumeOnException
        ));
    }

    public void handleException(Exception e, RegionImpl lastRegion, boolean resumeOnException) {
        handleException(addResumeOnException(
                lastRegion != null ? new SakuliActionException(e, lastRegion) : e,
                resumeOnException
        ));
    }

    public void handleException(LogResult logResult) {
        handleException(new SahiActionException(logResult));
    }

    private <T extends Exception> T addResumeOnException(T e, boolean resumeOnException) {
        if (resumeOnException
                //Forwarder error should never stop the test executions
                || e instanceof SakuliForwarderException
                //also exception within an async execution should not stop the execution
                || (e instanceof SakuliException && ((SakuliException) e).getAsyncTestDataRef().isPresent())
                ) {
            resumeExceptions.add(e);
        }
        return e;
    }
}
