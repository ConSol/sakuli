/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.starter.sahi.action.logging;

import net.sf.sahi.report.Report;
import net.sf.sahi.report.ResultType;
import org.aspectj.lang.JoinPoint;
import org.sakuli.actions.logging.LogToResult;
import org.sakuli.actions.logging.LogToResultCallback;
import org.sakuli.loader.BeanLoader;
import org.sakuli.starter.sahi.datamodel.actions.LogResult;
import org.sakuli.starter.sahi.loader.SahiActionLoader;
import org.springframework.stereotype.Component;

import static org.sakuli.aop.BaseSakuliAspect.createLoggingString;
import static org.sakuli.datamodel.actions.LogLevel.WARNING;

/**
 * Implementation to log results to the SahiReport.
 * Based on the Callback interface {@link LogToResultCallback}
 *
 * @author tschneck
 *         Date: 4/24/17
 */
@Component
public class SahiLogToResultCallback implements LogToResultCallback {


    /**
     * Method to do all Log Actions for the Sahi Report for the action classes annotated with {@link LogToResult}. A log
     * entry will created at the sakuli log files and at the sahi HTML {@link Report}.
     *
     * @param joinPoint   {@link JoinPoint} object of the calling aspect
     * @param logToResult {@link LogToResult} Annotation
     */
    @Override
    public void doActionLog(JoinPoint joinPoint, LogToResult logToResult) {
        if (logToResult != null) {
            StringBuilder message = createLoggingString(joinPoint, logToResult);

            //log the action to log file and print
            if (WARNING.equals(logToResult.level())) {
                message.insert(0, "WARNING: ");
            }
            ResultType resultType = LogResult.lookupResultType(logToResult.level());
            if (resultType != null) {
                Report sahiReport = BeanLoader.loadBean(SahiActionLoader.class).getSahiReport();
                if (sahiReport != null) {
                    sahiReport.addResult(
                            message.toString(),
                            resultType,
                            joinPoint.getSignature().getDeclaringTypeName(),
                            "");
                }
            }
        }
    }

}
