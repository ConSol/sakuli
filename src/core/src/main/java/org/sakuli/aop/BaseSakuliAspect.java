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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author tschneck
 *         Date: 23.09.14
 */
public abstract class BaseSakuliAspect {
    /**
     * @return the {@link Logger} for the assigned joinPoint.
     */
    protected Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
    }

    public String getClassAndMethodAsString(JoinPoint joinPoint) {
        return String.format("%s.%s()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    public String printArgs(JoinPoint joinPoint, boolean logArgs) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        Iterator iterator = Arrays.asList(joinPoint.getArgs()).iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (logArgs) {
                if (o != null) {
                    if (o instanceof Object[]) {
                        builder.append(printArray((Object[]) o));
                    } else {
                        builder.append(o.toString());
                    }
                } else {
                    builder.append("NULL");
                }
            } else {
                builder.append("****");
            }
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.append("]").toString();
    }

    public String printArray(Object[] objects) {
        StringBuilder sb = new StringBuilder("[");
        for (Object o : objects) {
            if (sb.toString().length() > 1) {
                sb.append(", ");
            }
            sb.append(o.toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
