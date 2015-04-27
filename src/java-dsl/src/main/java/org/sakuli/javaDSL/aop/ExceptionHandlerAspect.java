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

package org.sakuli.javaDSL.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.sakuli.aop.BaseSakuliAspect;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.loader.BeanLoader;
import org.springframework.stereotype.Component;

/**
 * Aspect for the Java based exception handling.
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class ExceptionHandlerAspect extends BaseSakuliAspect {

    /**
     * Throw the handled Exception after {@link SakuliExceptionHandler#processException(Throwable)} to stop the current
     * test case execution of an JAVA test.
     */
    @After("execution(* org.sakuli.exceptions.SakuliExceptionHandler.processException(..)) ")
    public void processJavaException(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length == 1) {
            Object e = args[0];
            SakuliExceptionHandler exceptionHandler = BeanLoader.loadBaseActionLoader().getExceptionHandler();
            if (e instanceof SakuliException && exceptionHandler.resumeToTestExcecution((SakuliException) e)) {
                return;
            }
            throw (Throwable) e;
        }
    }

}
