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

package org.sakuli.actions.screenbased;

import java.lang.annotation.*;

/**
 * This Annotation should be used for all actions in the package {@link org.sakuli.actions}, if the Action use a native
 * UI input action like typing or clicking.
 * <p>
 * This is need to fix some issues with Sahi Proxy:
 * To prevent missing UI event like a key press the Sahi request timer will be modified in respect of the property
 * {@code SahiProxyProperties#REQUEST_DELAY_MS}
 * <p>
 * All annoted methods or constructor will be called by the aspect
 * {@code org.sakuli.aop.ModifySahiTimerAspect}.
 * </p>
 *
 * @author Tobias Schneck
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserInterfaceInputAction {

}
