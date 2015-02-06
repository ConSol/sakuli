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

package org.sakuli.services.receiver.gearman.model;

import org.sakuli.services.receiver.gearman.TextPlaceholder;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PlaceholderMapTest {

    @Test
    public void testGet() throws Exception {
        PlaceholderMap testling = new PlaceholderMap();
        testling.put(TextPlaceholder.STEP_INFORMATION, "bla");
        testling.put(TextPlaceholder.DURATION, null);

        assertEquals(testling.get(TextPlaceholder.STEP_INFORMATION), "bla");
        assertEquals(testling.get(TextPlaceholder.DURATION), "");
    }
}