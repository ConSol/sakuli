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

package de.consol.sakuli.aop;

import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.datamodel.actions.LogResult;
import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BaseActionLoaderImpl;
import de.consol.sakuli.loader.BeanLoader;
import net.sf.sahi.report.ResultType;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Aspect for the External Sahi Libary {@link net.sf.sahi}
 *
 * @author tschneck
 *         Date: 17.10.13
 */
@Aspect
@Component
public class RhinoAspect {

    public static final String ALREADY_HANDELED = "{{SAKULI_EX}}";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Aspect to fetch the {@link RhinoScriptRunner} at the start of a test case script.
     * The {@link RhinoScriptRunner} will then saved in the {@link BaseActionLoaderImpl}
     * for forther usage.
     *
     * @param joinPoint
     */
    @After("execution(* net.sf.sahi.rhino.RhinoScriptRunner.setReporter*(*))")
    public void getRhinoScriptRunner(JoinPoint joinPoint) {
        logger.info("Add RhinoScriptRunner to the JavaBackEnd");
        BaseActionLoader environmentLoader = BeanLoader.loadBaseActionLoader();
        if (joinPoint.getTarget() instanceof RhinoScriptRunner) {
            environmentLoader.setRhinoScriptRunner((RhinoScriptRunner) joinPoint.getTarget());
        } else {
            logger.warn(joinPoint.getTarget().getClass().getName() + " could not added to the JavaBackEnd!");
        }
    }

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.TestCaseActions} class
     * to do an {@link #addActionLog(org.aspectj.lang.JoinPoint, de.consol.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* de.consol.sakuli.actions.TestCaseActions.*(..)) &&" +
            "@annotation(logToResult)")
    public void doTestCaseActionsLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.environment} classes
     * to do an {@link #addActionLog(org.aspectj.lang.JoinPoint, de.consol.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* de.consol.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doScreenBasedActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.environment} classes
     * to do an {@link #addActionLog(org.aspectj.lang.JoinPoint, de.consol.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* de.consol.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doEnvironmentLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.logging} classes
     * to do an {@link #addActionLog(org.aspectj.lang.JoinPoint, de.consol.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* de.consol.sakuli.actions.logging.*.*(..)) &&" +
            "@annotation(logToResult)")
    public void doLoggingLog(JoinPoint joinPoint, LogToResult logToResult) {
        addActionLog(joinPoint, logToResult);
    }

    /**
     * Method to do all Logs for the action classes annotated with {@link de.consol.sakuli.actions.logging.LogToResult}.
     * A log entry will created at the sakuli log files and at the sahi HTML {@link net.sf.sahi.report.Report}.
     *
     * @param joinPoint   {@link JoinPoint} object of the calling aspect
     * @param logToResult {@link LogToResult} Annotation
     */
    private void addActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        Logger logger = getLogger(joinPoint);
        if (logToResult != null) {
            StringBuilder message = new StringBuilder();
            if (logToResult.logClassInstance()) {
                message.append("\"").append(joinPoint.getTarget().toString()).append("\" ");
            }
            message.append(joinPoint.getSignature().getDeclaringType().getSimpleName()).append(".")
                    .append(joinPoint.getSignature().getName()).append("()");

            if (!isEmpty(logToResult.message())) {
                message.append(" - ").append(logToResult.message());
            }
            if (joinPoint.getArgs().length > 0) {
                message.append(" with arg(s) ").append(printArgs(joinPoint, logToResult.logArgs()));
            }

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
                BeanLoader.loadBaseActionLoader().getSahiReport().addResult(
                        message.toString(),
                        logToResult.level().getResultType(),
                        joinPoint.getSignature().getDeclaringTypeName(),
                        "");
            }
        }
    }

    /**
     * Aspect to fetch all Logs from the Sahi-Proxy by the methode {@link net.sf.sahi.ant.Report}
     * and do an trusty exception handling on top of that.
     *
     * @param joinPoint
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
        if (logResult.getFailureMsg() == null
                || !logResult.getFailureMsg().contains(ALREADY_HANDELED)
//                && !logResult.getMessage().startsWith("Logging exception:")
                ) {

            //log and handle exception from sahi actions
            if (ResultType.ERROR.equals(resultType)
                    || ResultType.FAILURE.equals(resultType)) {

                BaseActionLoader environmentLoader = BeanLoader.loadBaseActionLoader();
                environmentLoader.getExceptionHandler().handleException(logResult);
            }
            /**
             * all Actions in Package {@link de.consol.sakuli.actions} should be already logged by
             * {@link #doAddActionLog(org.aspectj.lang.JoinPoint, de.consol.sakuli.actions.logging.LogToResult)}.
             */
            else if (logResult.getDebugInfo() == null
                    || !logResult.getDebugInfo().startsWith("de.consol.sakuli.actions.")) {
                logger.info(logResult.getMessage());
            }
        }

    }


    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
    }

    private String printArgs(JoinPoint joinPoint, boolean logArgs) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        Iterator iterator = Arrays.asList(joinPoint.getArgs()).iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (logArgs) {
                if (o != null) {
                    builder.append(o.toString());
                } else {
                    builder.append("NULL");
                }
            } else {
                builder.append("****");
            }
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.append("]").toString();
    }


    //    private <A extends Annotation> A retrieveAnnotationFrom(JoinPoint joinPoint, Class<A> classDef) throws NoSuchMethodException {
//        return retrieveTargetMethodFrom(joinPoint).getAnnotation(classDef);
//    }
//
//    private Method retrieveTargetMethodFrom(JoinPoint joinPoint) throws NoSuchMethodException {
//        Class targetClass = joinPoint.getTarget().getClass();
//        return targetClass.getMethod(joinPoint.getSignature().getName(), retrieveArgTypesFrom(joinPoint));
//    }
//
//    private Class[] retrieveArgTypesFrom(JoinPoint joinPoint) {
//        Class[] argTypes = new Class[joinPoint.getArgs().length];
//        for (int i = 0; i < argTypes.length; i++) {
//            argTypes[i] = joinPoint.getArgs()[i].getClass();
//        }
//        return argTypes;
//    }

    //SAMPLE FOR TESTS
    //    @Before("execution(* de.consol.sakuli.starter.proxy.SahiProxy.startProxy(..))")
    //    public void doHandleException(JoinPoint joinPoint) {
    //        Logger logger = getLogger(joinPoint);
    //        logger.error("NEW ASPECT ACTION fetched");
    //    }
}
