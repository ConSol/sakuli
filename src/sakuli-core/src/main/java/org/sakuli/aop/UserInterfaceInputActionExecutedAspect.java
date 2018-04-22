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
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.actions.screenbased.UserInterfaceInputAction;
import org.sakuli.actions.screenbased.UserInterfaceInputActionCallback;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Aspect for the External Sahi Libary {@link net.sf.sahi}
 *
 * @author tschneck
 *         Date: 17.10.13
 */
@Aspect
@Component
public class UserInterfaceInputActionExecutedAspect extends BaseSakuliAspect {

    protected final static Logger logger = LoggerFactory.getLogger(UserInterfaceInputActionExecutedAspect.class);

    /**
     * Before Pointcut for the {@link org.sakuli.actions.environment} class
     * to do an {@link #triggerBeforeCallbacks(JoinPoint)}.
     */
    @Before("execution(* org.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(userInterfaceInputAction)")
    public void doEnvironmentBefore(JoinPoint joinPoint, UserInterfaceInputAction userInterfaceInputAction) {
        triggerBeforeCallbacks(joinPoint);
    }

    /**
     * Before Pointcut for the {@link org.sakuli.actions.screenbased} class
     * to do an {@link #triggerBeforeCallbacks(JoinPoint)}.
     */
    @Before("execution(* org.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(userInterfaceInputAction)")
    public void doScreenbasedBefore(JoinPoint joinPoint, UserInterfaceInputAction userInterfaceInputAction) {
        triggerBeforeCallbacks(joinPoint);
    }

    /**
     * After Pointcut for the {@link org.sakuli.actions.environment} class
     * to do an {@link #triggerAfterCallbacks(JoinPoint)}.
     */
    @After("execution(* org.sakuli.actions.environment.*.*(..)) &&" +
            "@annotation(userInterfaceInputAction)")
    public void doEnvironmentAfter(JoinPoint joinPoint, UserInterfaceInputAction userInterfaceInputAction) {
        triggerAfterCallbacks(joinPoint);
    }

    /**
     * After for the {@link org.sakuli.actions.screenbased} class
     * to do an {@link #triggerAfterCallbacks(JoinPoint)}.
     */
    @After("execution(* org.sakuli.actions.screenbased.*.*(..)) &&" +
            "@annotation(userInterfaceInputAction)")
    public void doScreenbasedAfter(JoinPoint joinPoint, UserInterfaceInputAction userInterfaceInputAction) {
        triggerAfterCallbacks(joinPoint);
    }

    void triggerBeforeCallbacks(JoinPoint joinPoint) {
        BeanLoader.loadMultipleBeans(UserInterfaceInputActionCallback.class).values().stream()
                .filter(Objects::nonNull)
                .forEach(cb -> cb.beforeUserInterfaceInput(joinPoint));
    }

    void triggerAfterCallbacks(JoinPoint joinPoint) {
        BeanLoader.loadMultipleBeans(UserInterfaceInputActionCallback.class).values().stream()
                .filter(Objects::nonNull)
                .forEach(cb -> cb.afterUserInterfaceInput(joinPoint));
    }

}
