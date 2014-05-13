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

package de.consol.sakuli.actions.settings;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import de.consol.sakuli.datamodel.properties.TestSuiteProperties;
import org.sikuli.basics.Settings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.InvalidParameterException;

/**
 * @author Tobias Schneck
 */
@Component
public class ScreenBasedSettings extends Settings {

    @Value("${" + SakuliProperties.INCLUDE_FOLDER + "}")
    private String includeFolderPath;
    @Value("${" + TestSuiteProperties.AUTO_HIGHLIGHT_ENABLED + "}")
    private boolean autoHighlightEnabled;
    @Value("${" + TestSuiteProperties.AUTO_HIGHLIGHT_SEC + "}")
    private float autoHighlightSeconds;
    @Value("${" + TestSuite.CLICK_DELAY_PROPERTY + "}")
    private double defClickDelay;
    @Value("${" + TestSuite.TYPE_DELAY_PROPERTY + "}")
    private double defTypeDelay;

    private double curMinSimilarity = 0.8f;

    @PostConstruct
    public void setDefaults() {
        MinSimilarity = curMinSimilarity;

        WaitScanRate = 10f;
        ObserveScanRate = 10f;

        ClickDelay = defClickDelay;
//        AutoWaitTimeout = 0;
        TypeDelay = defTypeDelay;

        OcrDataPath = includeFolderPath;
        OcrTextSearch = true;
        OcrTextRead = true;

        Highlight = autoHighlightEnabled;
        if (autoHighlightSeconds < 1) {
            /**
             * because of the mehtode {@link org.sikuli.script.ScreenHighlighter#closeAfter(float)}
             * */
            throw new InvalidParameterException("the property '" + TestSuiteProperties.AUTO_HIGHLIGHT_SEC + "' has to be greater as 1, but was " + autoHighlightSeconds);
        }
        DefaultHighlightTime = autoHighlightSeconds;
        WaitAfterHighlight = 0.1f;

        //Logging
        ActionLogs = false;
        DebugLogs = false;
        InfoLogs = false;
        ProfileLogs = false;

    }

    public void setMinSimilarity(double minSimilarity) {
        curMinSimilarity = minSimilarity;
        MinSimilarity = minSimilarity;
    }
}
