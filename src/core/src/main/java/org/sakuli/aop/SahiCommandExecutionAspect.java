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

import net.sf.sahi.config.Configuration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for the External Sahi Libary {@link net.sf.sahi}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class SahiCommandExecutionAspect extends BaseSakuliAspect {

    /**
     * Due to the fact, the parsing of the sahi method {@link net.sf.sahi.util.Utils#getCommandTokens(String)} won't
     * work correctly, this {@link Around} advice use the Apache libary {@link CommandLine#parse(String)} to modify it.
     * See http://community.sahipro.com/forums/discussion/8552/sahi-os-5-0-and-chrome-user-data-dir-containing-spaces-not-working.
     *
     * @param joinPoint     the {@link ProceedingJoinPoint} of the invoked method
     * @param commandString the original argument as{@link String}
     * @return the result of {@link CommandLine#parse(String)}
     */
    @Around("execution(* net.sf.sahi.util.Utils.getCommandTokens(..)) && args(commandString)")
    public String[] getCommandTokens(ProceedingJoinPoint joinPoint, String commandString) {
        Logger LOGGER = getLogger(joinPoint);
        CommandLine parsed = CommandLine.parse(commandString);
        String[] tokens = new String[]{parsed.getExecutable()};
        tokens = ArrayUtils.addAll(tokens, parsed.getArguments());
        try {
            Object result = joinPoint.proceed();
            if (result instanceof String[] && !Arrays.equals(tokens, (String[]) result)) {
                if (commandString.startsWith("sh -c \'")) { //exclude this kind of arguments, because the won't parsed correctly
                    //LOGGER.info("SAHI-RESULT {}", printArray((Object[]) result));
                    //LOGGER.info("SAKULI-RESULT {}", printArray(tokens));
                    return (String[]) result;
                }
                LOGGER.info("MODIFIED SAHI COMMAND TOKENS: {} => {}", printArray((String[]) result), printArray(tokens));
            }
        } catch (Throwable e) {
            LOGGER.error("Exception during execution of JoinPoint net.sf.sahi.util.Utils.getCommandTokens", e);
        }
        return tokens;
    }

    /**
     * Catch exceptions that would have been droped from {@link net.sf.sahi.util.Utils} of all 'execute*' methods and
     * forward it to the exception Handler.
     * <p>
     * Exceptions which thrown by missing "keytool" command, will be ignored , due to the internal exception handling
     * strategie at the point {@link Configuration#getKeytoolPath()}.
     *
     * @param joinPoint the {@link JoinPoint} of the invoked method
     * @param error     any {@link Exception} thrown of the Method
     */
    @AfterThrowing(pointcut = "execution(* net.sf.sahi.util.Utils.execute*(..))", throwing = "error")
    public void catchSahiCommandExcecutionErrors(JoinPoint joinPoint, Exception error) {
        String argString = printArgs(joinPoint, true);
        if (!argString.contains("keytool")) {
            BeanLoader.loadBaseActionLoader().getExceptionHandler().handleException(new SakuliInitException(error,
                    "Error executing command " + argString));
        }
    }

    /**
     * Because Sahi doesn't log the unmodified command call, do the logging over this advice.
     *
     * @param joinPoint the {@link JoinPoint} of the invoked method
     */
    @Before("execution(* net.sf.sahi.util.Utils.execute*(..))")
    public void logSahiCommandExection(JoinPoint joinPoint) {
        getLogger(joinPoint).debug("SAHI command execution: {}", printArgs(joinPoint, true));
    }

}
