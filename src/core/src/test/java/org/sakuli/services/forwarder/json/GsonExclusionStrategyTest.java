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

package org.sakuli.services.forwarder.json;

import org.sakuli.actions.screenbased.RegionImpl;
import org.sakuli.datamodel.actions.Screen;
import org.sikuli.script.Region;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by georgi on 29/09/17.
 */
public class GsonExclusionStrategyTest {

    GsonExclusionStrategy testling = new GsonExclusionStrategy();

    @DataProvider
    public Object[][] shouldSkipClassDP() {
        return new Object[][]{
                {Region.class, true},
                {Screen.class, true},
                {RegionImpl.class, true},
                {org.sakuli.actions.screenbased.Region.class, false},
        };
    }

    @Test(dataProvider = "shouldSkipClassDP")
    public void shouldSkipClass(Class c, boolean shouldBeSkiped) {
        assertEquals(testling.shouldSkipClass(c), shouldBeSkiped);
    }

}
