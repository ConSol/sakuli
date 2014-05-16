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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for the External Sikuli Libary {@link org.sikuli.script}
 *
 * @author tschneck
 *         Date: 17.10.13
 */
@Aspect
@Component
public class FetchScreenBasedLogsAspect {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Aspect to fetch all Exception from the Sahi-Proxy by the methode {@link net.sf.sahi.ant.Report}
     * and do an trusty exception handling on top of that.
     *
     * @param pjp
     */
//    @Around("execution(*  org.sikuli.basics.Debug.log(..)) &&" +
//            "args(level, message)")
    @Before("execution(* org.sikuli.basics.Debug.log(..))")
    public void doHandleSikuliLog(JoinPoint pjp) {
        logger.info("LEVEL " + pjp.getArgs()[0] + ": " + String.format(pjp.getArgs()[1].toString(), pjp.getArgs()[2]));
    }

}
