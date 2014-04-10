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

    @Value("${" + TestSuite.INCLUDE_FOLDER_PROPERTY + "}")
    private String includeFolderPath;
    @Value("${" + TestSuite.AUTO_HIGHLIGHT_PROPERTY + "}")
    private boolean autoHighlightEnabled;
    @Value("${" + TestSuite.AUTO_HIGHLIGHT_SEC_PROPERTY + "}")
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
            throw new InvalidParameterException("the property '" + TestSuite.AUTO_HIGHLIGHT_SEC_PROPERTY + "' has to be greater as 1, but was " + autoHighlightSeconds);
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
