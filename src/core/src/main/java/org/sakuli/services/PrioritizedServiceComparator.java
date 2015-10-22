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

package org.sakuli.services;

import java.util.Comparator;

/**
 * @author Tobias Schneck
 */
public class PrioritizedServiceComparator<S extends PrioritizedService> implements Comparator<S> {

    @Override
    public int compare(S o1, S o2) {
        int result = -1 * Integer.compare(o1.getServicePriority(), o2.getServicePriority());
        if (result == 0) {
            result = Integer.compare(o1.hashCode(), o2.hashCode());
        }
        return result;
    }
}
