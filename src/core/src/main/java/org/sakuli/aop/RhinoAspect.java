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

package org.sakuli.aop;

import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.datamodel.TestAction;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.actions.LogResult;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;

/**
 * Aspect for the External Sahi Library {@link net.sf.sahi}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class RhinoAspect extends BaseSakuliAspect {

    public static final String ALREADY_PROCESSED = "{{SAKULI_EX}}";
    protected final static Logger logger = LoggerFactory.getLogger(RhinoAspect.class);

    /**
     * Aspect to fetch the {@link RhinoScriptRunner} at the start of a test case script. The {@link RhinoScriptRunner}
     * will then saved in the {@link BaseActionLoaderImpl} for forther usage.
     *
     * @param joinPoint injected joinPoint of the execution
     */
    @After("execution(* net.sf.sahi.rhino.RhinoScriptRunner.setReporter*(*))")
    public void getRhinoScriptRunner(JoinPoint joinPoint) {
        BaseActionLoader environmentLoader = BeanLoader.loadBaseActionLoader();
        if (joinPoint.getTarget() instanceof RhinoScriptRunner) {
            logger.info("Add RhinoScriptRunner to the JavaBackEnd");
            environmentLoader.setRhinoScriptRunner((RhinoScriptRunner) joinPoint.getTarget());
        } else {
            logger.warn(joinPoint.getTarget().getClass().getName() + " could not added to the JavaBackEnd!");
        }
    }

    /**
     * Pointcut for the {@link org.sakuli.actions.TestCaseAction} class to do an {@link
     * #addActionLog(org.aspectj.lang.JoinPoint, org.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* org.sakuli.actions.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doTestCaseActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link org.sakuli.actions.environment} classes to do an {@link
     * #addActionLog(org.aspectj.lang.JoinPoint, org.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* org.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doScreenBasedActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link org.sakuli.actions.environment} classes to do an {@link
     * #addActionLog(org.aspectj.lang.JoinPoint, org.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* org.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doEnvironmentLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link org.sakuli.actions.logging} classes to do an {@link #addActionLog(org.aspectj.lang.JoinPoint,
     * org.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* org.sakuli.actions.logging.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doLoggingLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Method to do all Logs for the action classes annotated with {@link org.sakuli.actions.logging.LogToResult}. A log
     * entry will created at the sakuli log files and at the sahi HTML {@link net.sf.sahi.report.Report}.
     *
     * @param joinPoint   {@link JoinPoint} object of the calling aspect
     * @param logToResult {@link LogToResult} Annotation
     */
    protected void addActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        Logger logger = getLogger(joinPoint);
        if (logToResult != null) {
            StringBuilder message = createLoggingString(joinPoint, logToResult);

            addActionsToCurrentTestCase(extractTestAction(joinPoint, logToResult));

            //log the action to log file and print
            switch (logToResult.level()) {
                case ERROR:
                    logger.error(message.toString());
                    break;
                case INFO:
                    logger.info(message.toString());
                    break;
                case DEBUG:
                    logger.debug(message.toString());
                    break;
                case WARNING:
                    logger.warn(message.toString());
                    message.insert(0, "WARNING: ");
                    break;
            }
            if (logToResult.level().getResultType() != null) {
                Report sahiReport = BeanLoader.loadBaseActionLoader().getSahiReport();
                if (sahiReport != null) {
                    sahiReport.addResult(
                            message.toString(),
                            logToResult.level().getResultType(),
                            joinPoint.getSignature().getDeclaringTypeName(),
                            "");
                }
            }
        }
    }

    protected void addActionsToCurrentTestCase(TestAction currentTestAction) {
        TestCase currentTestCase = BeanLoader.loadBaseActionLoader().getCurrentTestCase();
        if (currentTestAction != null && currentTestCase != null) {
            currentTestCase.addAction(currentTestAction);
        }
    }

    protected TestAction extractTestAction(JoinPoint joinPoint, LogToResult logToResult) {
        if (logToResult.logArgsOnly()) {
            return null;
        }
        return TestAction.createSakuliTestAction(
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        joinPoint.getArgs(),
                        logToResult.message(),
                        BeanLoader.loadBaseActionLoader().getSakuliProperties().getSakuliDocBaseUrl()
                );
    }

    /**
     * @return based on the different arguments of the {@link LogToResult} annotation an different output {@link String}
     */
    protected StringBuilder createLoggingString(JoinPoint joinPoint, LogToResult logToResult) {
        if (logToResult.logArgsOnly()) {
            return new StringBuilder(removeEnd(removeStart(printArgs(joinPoint, logToResult.logArgs()), "["), "]"));
        }

        StringBuilder message = new StringBuilder();
        //log class instance?
        if (logToResult.logClassInstance() && joinPoint.getTarget() != null) {
            message.append("\"").append(joinPoint.getTarget().toString()).append("\" ");
        }
        message.append(getClassAndMethodAsString(joinPoint));

        //add message if needed
        if (isNotEmpty(logToResult.message())) {
            message.append(" - ").append(logToResult.message());
        }
        //add args if needed
        if (ArrayUtils.isNotEmpty(joinPoint.getArgs())) {
            message.append(" with arg(s) ").append(printArgs(joinPoint, logToResult.logArgs()));
        }
        return message;
    }

    /**
     * Aspect to fetch all Logs from the Sahi-Proxy by the method {@link net.sf.sahi.ant.Report} and do an trusty
     * exception handling on top of that.
     *
     * @param joinPoint injected joinPoint of the execution
     */
    @Before("execution(* net.sf.sahi.report.Report.addResult(..))")
    public void doHandleRhinoException(JoinPoint joinPoint) {
        // Read out all args
        Object[] args = joinPoint.getArgs();
        ResultType resultType;
        if (args[1] instanceof ResultType) {
            resultType = (ResultType) args[1];
        } else {
            resultType = ResultType.getType((String) args[1]);
        }

        LogResult logResult = new LogResult(
                (String) args[0],
                resultType,
                (String) args[2],
                (String) args[3]
        );

        if (logResult.getFailureMsg() == null || !logResult.getFailureMsg().contains(ALREADY_PROCESSED)) {
            //log and handle exception from sahi actions
            if (ResultType.ERROR.equals(resultType)
                    || ResultType.FAILURE.equals(resultType)) {

                BaseActionLoader environmentLoader = BeanLoader.loadBaseActionLoader();
                environmentLoader.getExceptionHandler().handleException(logResult);
            }

            /**
             * all Actions in Package {@link org.sakuli.actions} should be already logged by
             * {@link #doAddActionLog(org.aspectj.lang.JoinPoint, org.sakuli.actions.logging.LogToResult)}.
             */
            else if (logResult.getDebugInfo() == null
                    || !logResult.getDebugInfo().startsWith("org.sakuli.actions.")) {
                logger.info(logResult.getMessage());
                addActionsToCurrentTestCase(TestAction.createSahiTestAction(logResult.getMessage(), BeanLoader.loadBaseActionLoader().getSakuliProperties().getSahiDocBaseUrl()));
            }
        }

    }

}
