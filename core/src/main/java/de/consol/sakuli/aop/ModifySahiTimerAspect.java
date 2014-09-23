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

import de.consol.sakuli.actions.ModifySahiTimer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for the External Sahi Libary {@link net.sf.sahi}
 *
 * @author tschneck
 *         Date: 17.10.13
 */
@Aspect
@Component
public class ModifySahiTimerAspect extends BaseSakuliAspect {

    protected final static Logger logger = LoggerFactory.getLogger(ModifySahiTimerAspect.class);

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.environment} class
     * to do an {@link #modifySahiTimer(JoinPoint)}.
     */
    @Before("execution(* de.consol.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doEnvironmentLog(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint);
    }

    /**
     * Pointcut for the {@link de.consol.sakuli.actions.screenbased} class
     * to do an {@link #modifySahiTimer(JoinPoint)}.
     */
    @Before("execution(* de.consol.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doScreenbasedAction(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint);
    }

    void modifySahiTimer(JoinPoint joinPoint) {
        getLogger(joinPoint).debug("MODIFY SAHI-TIMER for {}", getClassAndMethodAsString(joinPoint));
    }

}
