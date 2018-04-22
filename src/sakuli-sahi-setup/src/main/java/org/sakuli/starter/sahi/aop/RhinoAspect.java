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

package org.sakuli.starter.sahi.aop;

import net.sf.sahi.report.ResultType;
import net.sf.sahi.rhino.RhinoScriptRunner;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.aop.BaseSakuliAspect;
import org.sakuli.aop.LogActionExecutedAspect;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BaseActionLoaderImpl;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.datamodel.actions.LogResult;
import org.sakuli.starter.sahi.exceptions.SahiActionException;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for the External Sahi Library {@link net.sf.sahi}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class RhinoAspect extends BaseSakuliAspect {

    protected final static Logger logger = LoggerFactory.getLogger(LogActionExecutedAspect.class);

    /**
     * Aspect to fetch the {@link RhinoScriptRunner} at the start of a test case script. The {@link RhinoScriptRunner}
     * will then saved in the {@link BaseActionLoaderImpl} for forther usage.
     *
     * @param joinPoint injected joinPoint of the execution
     */
    @After("execution(* net.sf.sahi.rhino.RhinoScriptRunner.setReporter*(*))")
    public void getRhinoScriptRunner(JoinPoint joinPoint) {
        if (joinPoint.getTarget() instanceof RhinoScriptRunner) {
            logger.info("Add RhinoScriptRunner to the JavaBackEnd");
            BeanLoader.loadBean(SahiActionLoader.class).setRhinoScriptRunner((RhinoScriptRunner) joinPoint.getTarget());
        } else {
            logger.warn(joinPoint.getTarget().getClass().getName() + " could not added to the JavaBackEnd!");
        }
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
                environmentLoader.getExceptionHandler().handleException(new SahiActionException(logResult));
            }

            /**
             * all Actions in Package {@link org.sakuli.actions} should be already logged by
             * {@link #doaddSahiActionLog(JoinPoint, LogToResult)}.
             */
            else if (logResult.getDebugInfo() == null
                    || !logResult.getDebugInfo().startsWith("org.sakuli.actions.")) {
                logger.info(logResult.getMessage());
            }
        }

    }

}
