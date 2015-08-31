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

package org.sakuli.services.forwarder.gearman.model.builder;

import java.util.Locale;

/**
 * Representing a set of formatting methods in the Nagios context.
 *
 * @author tschneck
 *         Date: 8/26/15
 */
public class NagiosFormatter {


    public static String formatToSec(float duration) {
        return String.format(Locale.ENGLISH, "%.2fs", duration);
    }

    public static String formatToSec(int duration) {
        return duration + "s";
    }
}
