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

package de.consol.sakuli.actions.screenbased;

import de.consol.sakuli.actions.logging.LogToResult;
import de.consol.sakuli.aop.RhinoAspectTest;
import de.consol.sakuli.datamodel.actions.LogLevel;
import de.consol.sakuli.loader.ScreenActionLoader;

/**
 * Test class for {@link RhinoAspectTest#testdoScreenBasedActionLog()}
 *
 * @author tschneck
 *         Date: 23.05.14
 */
public class RegionTestImpl extends Region {
    public RegionTestImpl(int x, int y, int w, int h, boolean resumeOnException, ScreenActionLoader loader) {
        super(x, y, w, h, resumeOnException, loader);
    }

    @LogToResult(level = LogLevel.WARNING)
    public static void testLogMethod() {
        //trigger for aspect
    }
}
