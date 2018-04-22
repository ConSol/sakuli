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

package org.sakuli.starter.sahi.action.screenbased;

import net.sf.sahi.rhino.RhinoScriptRunner;
import net.sf.sahi.session.Session;
import org.aspectj.lang.JoinPoint;
import org.sakuli.actions.screenbased.UserInterfaceInputActionCallback;
import org.sakuli.aop.BaseSakuliAspect;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Callback handler for the {@link UserInterfaceInputActionCallback} interface.
 *
 * @author tschneck
 *         Date: 17.10.13
 */
@Component
public class ModifySahiTimerCallback implements UserInterfaceInputActionCallback {


    private final static Logger LOGGER = LoggerFactory.getLogger(ModifySahiTimerCallback.class);

    @Override
    public void beforeUserInterfaceInput(JoinPoint joinPoint) {
        modifySahiTimer(joinPoint, true);
    }

    @Override
    public void afterUserInterfaceInput(JoinPoint joinPoint) {
        modifySahiTimer(joinPoint, false);
    }

    void modifySahiTimer(JoinPoint joinPoint, boolean beforeMethod) {
        SahiActionLoader loader = BeanLoader.loadBean(SahiActionLoader.class);
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
                        LOGGER.info("wait {}ms for sahi refresh", sahiProxyProperties.getRequestDelayRefreshMs());
                        Thread.sleep(sahiProxyProperties.getRequestDelayRefreshMs());
                    } catch (InterruptedException e) {
                        loader.getBaseActionLoader().getExceptionHandler().handleException(e, true);
                    }
                    LOGGER.info("sahi-proxy-timer modified to {} ms", delay.toString());

                } else {
                    LOGGER.info("reset sahi-proxy-timer");
                    session.setVariable(SahiProxyProperties.SAHI_REQUEST_DELAY_TIME_VAR, null);
                }
            }
        }
    }

    protected Integer determineDelay(JoinPoint joinPoint, SahiActionLoader loader) {
        Integer delay = loader.getSahiProxyProperties().getRequestDelayMs();
        if (joinPoint.getSignature().getName().startsWith("type")) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Object text = args[0];
                if (text instanceof String) {
                    int length = ((String) text).length();
                    int typeDelayMs = loader.getBaseActionLoader().getActionProperties().getTypeDelayMs();

                    return length * (typeDelayMs + delay);
                }
            }
        }
        return delay;
    }

    //wrapping needed for testing
    Logger getLogger(JoinPoint joinPoint) {
        return BaseSakuliAspect.getLogger(joinPoint);
    }

    String getClassAndMethodAsString(JoinPoint joinPoint) {
        return BaseSakuliAspect.getClassAndMethodAsString(joinPoint);
    }
}
