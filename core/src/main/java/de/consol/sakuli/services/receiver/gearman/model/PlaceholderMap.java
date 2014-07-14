/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.services.receiver.gearman.model;

import de.consol.sakuli.services.receiver.gearman.TextPlaceholder;

import java.util.HashMap;

/**
 * @author tschneck
 *         Date: 14.07.14
 */
public class PlaceholderMap extends HashMap<TextPlaceholder, String> {

    /**
     * Overrides the normal get, so that a null String will be returned as empty String.
     */
    @Override
    public String get(Object key) {
        String value = super.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }
}
