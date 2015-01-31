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

import de.consol.sakuli.datamodel.properties.ActionProperties;
import de.consol.sakuli.datamodel.properties.SakuliProperties;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import javax.annotation.PostConstruct;
import java.security.InvalidParameterException;

/**
 * @author Tobias Schneck
 */
@Component
public class ScreenBasedSettings extends Settings {

    private double curMinSimilarity = 0.8f;
    private ActionProperties props;
    private SakuliProperties sakuliProps;

    @Autowired
    public ScreenBasedSettings(ActionProperties props, SakuliProperties sakuliProps) {
        this.props = props;
        this.sakuliProps = sakuliProps;
    }

    @PostConstruct
    public void setDefaults() {
        MinSimilarity = curMinSimilarity;

        WaitScanRate = 10f;
        ObserveScanRate = 10f;

        ClickDelay = props.getClickDelay();
        TypeDelay = props.getTypeDelay();

        OcrDataPath = sakuliProps.getIncludeFolder().toAbsolutePath().toString();
        OcrTextSearch = true;
        OcrTextRead = true;

        Highlight = props.isAutoHighlightEnabled();
        if (props.getAutoHighlightSeconds() < 1) {
            /**
             * because of the mehtode {@link org.sikuli.script.ScreenHighlighter#closeAfter(float)}
             * */
            throw new InvalidParameterException("the property '" + ActionProperties.AUTO_HIGHLIGHT_SEC + "' has to be greater as 1, but was " + props.getAutoHighlightSeconds());
        }
        DefaultHighlightTime = props.getAutoHighlightSeconds();
        WaitAfterHighlight = 0.1f;

        /***
         * Logging for sikuliX => {@link SysOutOverSLF4J} will send the logs to SLF4J
         */
        Logger sikuliLogger = LoggerFactory.getLogger(Debug.class);
        if (sikuliLogger.isInfoEnabled()) {
            ActionLogs = true;
            InfoLogs = true;
            ProfileLogs = true;
        }
        if (sikuliLogger.isDebugEnabled()) {
            DebugLogs = true;
        }
    }

    public void setMinSimilarity(double minSimilarity) {
        curMinSimilarity = minSimilarity;
        MinSimilarity = minSimilarity;
    }
}
