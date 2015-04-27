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

import org.sakuli.actions.settings.ScreenBasedSettings;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.security.InvalidParameterException;


public class ScreenBasedSettingsTest {

    @Test(expectedExceptions = InvalidParameterException.class, expectedExceptionsMessageRegExp = "the property '" + ActionProperties.DEFAULT_HIGHLIGHT_SEC + "' has to be greater as 1, but was 0.6")
    public void testSetDefaults() throws Exception {
        ActionProperties props = new ActionProperties();
        props.setDefaultHighlightSeconds(0.6f);
        SakuliProperties sakuliProps = new SakuliProperties();
        sakuliProps.setTessDataLibFolder(Paths.get("."));

        ScreenBasedSettings testling = new ScreenBasedSettings(props, sakuliProps);
        testling.setDefaults();
    }
}
