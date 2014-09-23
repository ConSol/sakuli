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
import de.consol.sakuli.datamodel.properties.SahiProxyProperties;
import de.consol.sakuli.loader.BaseActionLoader;
import de.consol.sakuli.loader.BeanLoader;
import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
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
     * Before Pointcut for the {@link de.consol.sakuli.actions.environment} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @Before("execution(* de.consol.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doEnvironmentBefore(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, true);
    }

    /**
     * Before Pointcut for the {@link de.consol.sakuli.actions.screenbased} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @Before("execution(* de.consol.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doScreenbasedBefore(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, true);
    }

    /**
     * After Pointcut for the {@link de.consol.sakuli.actions.environment} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @After("execution(* de.consol.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doEnvironmentAfter(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, false);
    }

    /**
     * After for the {@link de.consol.sakuli.actions.screenbased} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @After("execution(* de.consol.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doScreenbasedAfter(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, false);
    }

    void modifySahiTimer(JoinPoint joinPoint, boolean beforeMethod) {
        getLogger(joinPoint).debug("MODIFY SAHI-TIMER for {}", getClassAndMethodAsString(joinPoint));
        //TODO write unit tests

        BaseActionLoader loader = BeanLoader.loadBaseActionLoader();
        SahiProxyProperties sahiProxyProperties = loader.getSahiProxyProperties();
        if (sahiProxyProperties != null
                && sahiProxyProperties.isRequestDelayActive()) {

            RhinoScriptRunner rhinoScriptRunner = loader.getRhinoScriptRunner();
            if (rhinoScriptRunner != null && rhinoScriptRunner.getSession() != null) {
                Session session = rhinoScriptRunner.getSession();
                if (beforeMethod) {
                    String delay = String.valueOf(sahiProxyProperties.getRequestDelayMs());
                    session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, delay);

                    //short sleep to ensure that browser context can read out the value
                    try {
                        Thread.sleep(sahiProxyProperties.getRequestDelayMs() / 4);
                    } catch (InterruptedException e) {
                        BeanLoader.loadBaseActionLoader().getExceptionHandler().handleException(e);
                    }
                    logger.info("sahi-proxy-timer modified to {} ms", delay);

                } else {
                    logger.info("reset sahi-proxy-timer");
                    session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
                }
            }
        }
    }

}
