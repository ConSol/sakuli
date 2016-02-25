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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Aspect for the External Sahi Libary {@link net.sf.sahi}
 *
 * @author tschneck Date: 17.10.13
 */
@Aspect
@Component
public class SahiCommandExecutionAspect extends BaseSakuliAspect {

    //TODO TS write TEST and comment
    //"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --incognito --user-data-dir="C:/DATEN/git-files/sakuli/sahi/userdata/browser/chrome/profiles/sahi0" --no-default-browser-check --no-first-run --disable-infobars --proxy-server=localhost:9999 --disable-popup-blocking "http://sahi.example.com/_s_/dyn/Player_auto?startUrl=http%3A%2F%2Fsahi.example.com%2F_s_%2Fdyn%2FDriver_initialized__SahiAmpersandSahi__sahisid=sahi_44eb94b307a820415609523034635e99d939sahixb9a85c8c0dad2040b908ae00bf62f3b9774cx__SahiAmpersandSahi__isSingleSession=false"
    @Around("execution(* net.sf.sahi.util.Utils.getCommandTokens(..)) && args(commandString)")
    public String[] getCommandTokens(ProceedingJoinPoint joinPoint, String commandString) {
        Logger LOGGER = getLogger(joinPoint);
        CommandLine parsed = CommandLine.parse(commandString);
        String[] tokens = new String[]{parsed.getExecutable()};
        tokens = ArrayUtils.addAll(tokens, parsed.getArguments());
        try {
            Object result = joinPoint.proceed();
            if (result instanceof String[] && !Arrays.equals(tokens, (String[]) result)) {
                LOGGER.info("MODIFYED SAHI COMMAND TOKENS: {} => {}", printArray((String[]) result), printArray(tokens));
            }
        } catch (Throwable e) {
            LOGGER.error("Exception during execution of JoinPoint net.sf.sahi.util.Utils.getCommandTokens", e);
        }
        return tokens;
    }

    @AfterThrowing(pointcut = "execution(* net.sf.sahi.util.Utils.execute*(..))", throwing = "error")
    public void catchSahiCommandExcecutionErrors(JoinPoint joinPoint, Throwable error) {
        BeanLoader.loadBaseActionLoader().getExceptionHandler().handleException(new SakuliInitException(error,
                "Error executing command " + printArgs(joinPoint, true)));
    }

    @Before("execution(* net.sf.sahi.util.Utils.execute*(..))")
    public void logSahiCommandExection(JoinPoint joinPoint) {
        getLogger(joinPoint).debug("SAHI command execution: {}", printArgs(joinPoint, true));
    }

}
