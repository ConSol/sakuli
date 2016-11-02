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

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.actions.ModifySahiTimer;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.loader.BaseActionLoader;
import org.sakuli.loader.BeanLoader;
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
     * Before Pointcut for the {@link org.sakuli.actions.environment} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @Before("execution(* org.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doEnvironmentBefore(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, true);
    }

    /**
     * Before Pointcut for the {@link org.sakuli.actions.screenbased} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @Before("execution(* org.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doScreenbasedBefore(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, true);
    }

    /**
     * After Pointcut for the {@link org.sakuli.actions.environment} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @After("execution(* org.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doEnvironmentAfter(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, false);
    }

    /**
     * After for the {@link org.sakuli.actions.screenbased} class
     * to do an {@link #modifySahiTimer(JoinPoint, boolean)}.
     */
    @After("execution(* org.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(modifySahiTimer)")
    public void doScreenbasedAfter(JoinPoint joinPoint, ModifySahiTimer modifySahiTimer) {
        modifySahiTimer(joinPoint, false);
    }

    void modifySahiTimer(JoinPoint joinPoint, boolean beforeMethod) {
        BaseActionLoader loader = BeanLoader.loadBaseActionLoader();
        SahiProxyProperties sahiProxyProperties = loader.getSahiProxyProperties();
        if (sahiProxyProperties != null
                && sahiProxyProperties.isRequestDelayActive()) {
            RhinoScriptRunner rhinoScriptRunner = loader.getRhinoScriptRunner();

            if (rhinoScriptRunner != null && rhinoScriptRunner.getSession() != null) {
                getLogger(joinPoint).debug("MODIFY SAHI-TIMER for {}", getClassAndMethodAsString(joinPoint));
                Session session = rhinoScriptRunner.getSession();
                if (beforeMethod) {
                    Integer delay = determineDelay(joinPoint, loader);
                    session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, delay.toString());

                    //short sleep to ensure that browser context can read out the value
                    try {
                        logger.info("wait {}ms for sahi refresh", sahiProxyProperties.getRequestDelayRefreshMs());
                        Thread.sleep(sahiProxyProperties.getRequestDelayRefreshMs());
                    } catch (InterruptedException e) {
                        BeanLoader.loadBaseActionLoader().getExceptionHandler().handleException(e, true);
                    }
                    logger.info("sahi-proxy-timer modified to {} ms", delay.toString());

                } else {
                    logger.info("reset sahi-proxy-timer");
                    session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
                }
            }
        }
    }

    protected Integer determineDelay(JoinPoint joinPoint, BaseActionLoader loader) {
        Integer deleay = loader.getSahiProxyProperties().getRequestDelayMs();
        if (joinPoint.getSignature().getName().startsWith("type")) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Object text = args[0];
                if (text instanceof String) {
                    int length = ((String) text).length();
                    int typeDelayMs = loader.getActionProperties().getTypeDelayMs();

                    return length * (typeDelayMs + deleay);
                }
            }
        }
        return deleay;
    }

}
