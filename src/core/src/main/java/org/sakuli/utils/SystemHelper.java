/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tschneck
 *         Date: 4/6/16
 */
public class SystemHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHelper.class);

    /**
     * Do a {@link Thread#sleep(long)} with Logging around.
     */
    public static void sleep(Long milliseconds) {
        try {
            LOGGER.debug("Application sleeping for {} milliseconds.", milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            LOGGER.error("error during sleeping", e);
        }
    }
}
