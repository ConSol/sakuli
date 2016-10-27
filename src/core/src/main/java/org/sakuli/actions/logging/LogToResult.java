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

package org.sakuli.actions.logging;

import org.sakuli.datamodel.actions.LogLevel;

import java.lang.annotation.*;

/**
 * This Annotation should be used do log actions in the package {@link org.sakuli.actions}.
 * All annoted methods or constructor will be called by the aspect
 * {@link org.sakuli.aop.RhinoAspect}.
 *
 * @author Tobias Schneck
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogToResult {
    String message() default "";

    boolean logClassInstance() default true;

    boolean logArgs() default true;

    LogLevel level() default LogLevel.INFO;

    boolean logArgsOnly() default false;
}
