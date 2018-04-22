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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.datamodel.properties.SahiProxyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.lang.Boolean.FALSE;

/**
 * Aspect for the External Sahi Library {@link net.sf.sahi}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class SahiHeaderAspect extends BaseSakuliAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(SahiHeaderAspect.class);
    private static Boolean removeAuthorizationHeader;

    public static boolean getRemoveAuthorizationHeader() {
        if (removeAuthorizationHeader == null) {
            removeAuthorizationHeader = BeanLoader.loadBaseActionLoader().getSahiProxyProperties().getRemoveAuthorizationHeader();
            if (FALSE.equals(removeAuthorizationHeader))
                LOGGER.info("{}={}: SAHI remove Header 'Authorization' is DISABLED! ", SahiProxyProperties.REMOVE_AUTHORIZATION_HEADER, removeAuthorizationHeader);
        }
        return removeAuthorizationHeader;
    }

    /**
     * Aspect to skip the execution of the action {@link net.sf.sahi.request.HttpRequest#removeHeader(String) } functionality for the headerString "Authorization"
     *
     * @param joinPoint    injected joinPoint of the execution
     * @param headerString will called with different header values from Sahi
     */
    @Around("execution(* net.sf.sahi.StreamHandler.removeHeader(..)) && args(headerString)")
    public void aroundSahiRemoveHeaders(ProceedingJoinPoint joinPoint, String headerString) throws Throwable {
        if (FALSE.equals(getRemoveAuthorizationHeader()) && "Authorization".equals(headerString)) {
            LOGGER.debug("SAHI skip remove Header '{}'", headerString);
            //skip execution of method
            return;
        }
        joinPoint.proceed();
    }


}
