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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.actions.logging.LogToResultCallback;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Aspect for all Actions under {@link org.sakuli.actions}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class LogActionExecutedAspect extends BaseSakuliAspect {

    protected final static Logger logger = LoggerFactory.getLogger(LogActionExecutedAspect.class);

    /**
     * Pointcut for the {@link org.sakuli.actions.TestCaseAction} class to do an {@link
     * #addActionLog(org.aspectj.lang.JoinPoint, org.sakuli.actions.logging.LogToResult)}
     */
    @Before("execution(* org.sakuli.actions.TestCaseAction.*(..)) &&" +
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
     * entry will created at the sakuli log files and at the sahi HTML over the callback {@link LogToResultCallback}.
     *
     * @param joinPoint   {@link JoinPoint} object of the calling aspect
     * @param logToResult {@link LogToResult} Annotation
     */
    protected void addActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        Logger logger = getLogger(joinPoint);
        if (logToResult != null) {
            StringBuilder message = createLoggingString(joinPoint, logToResult);

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
                    break;
            }
        }
        callLoggingCallbacks(joinPoint, logToResult);
    }

    /**
     * Calls all defined implementations of {@link } as Callback.
     * NOTE: Because a class can only compiled/weaved by aspectj once, this behaviour is needed to be modular.
     */
    protected void callLoggingCallbacks(JoinPoint joinPoint, LogToResult logToResult) {
        BeanLoader.loadMultipleBeans(LogToResultCallback.class).values().stream()
                .filter(Objects::nonNull)
                .forEach(cb -> cb.doActionLog(joinPoint, logToResult));
    }

}
