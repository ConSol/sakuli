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

package org.sakuli.actions.settings;

import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.RobotDesktop;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenBasedSettings.class);
    private ActionProperties props;
    private SakuliProperties sakuliProps;
    private double currentSimilarity;

    @Autowired
    public ScreenBasedSettings(ActionProperties props, SakuliProperties sakuliProps) {
        this.props = props;
        this.sakuliProps = sakuliProps;
        restetMinSimilarity();
    }

    @PostConstruct
    public void setDefaults() {
        setMinSimilarity(currentSimilarity);

        WaitScanRate = 10f;
        ObserveScanRate = 10f;

        ClickDelay = props.getClickDelay();
        RobotDesktop.stdAutoDelay = props.getTypeDelayMs();
        //if stdAutoDelay is set TypeDelay is no longer needed!
        TypeDelay = 0;

        OcrDataPath = sakuliProps.getTessDataLibFolder().toAbsolutePath().toString();
        OcrTextSearch = true;
        OcrTextRead = true;

        Highlight = props.isAutoHighlightEnabled();
        if (props.getDefaultHighlightSeconds() < 1) {
            /**
             * because of the mehtode {@link org.sikuli.script.ScreenHighlighter#closeAfter(float)}
             * */
            throw new InvalidParameterException("the property '" + ActionProperties.DEFAULT_HIGHLIGHT_SEC + "' has to be greater as 1, but was " + props.getDefaultHighlightSeconds());
        }
        DefaultHighlightTime = props.getDefaultHighlightSeconds();
        WaitAfterHighlight = 0.1f;

        /***
         * Logging for sikuliX => {@link SysOutOverSLF4J} will send the logs to SLF4J
         */
        Logger sikuliLogger = LoggerFactory.getLogger(Debug.class);
        if (sikuliLogger.isInfoEnabled()) {
            LOGGER.debug("sikuli log level INFO enabled");
            ActionLogs = true;
            InfoLogs = true;
            ProfileLogs = true;
        }
        if (sikuliLogger.isDebugEnabled()) {
            LOGGER.debug("sikuli log level DEBUG enabled");
            DebugLogs = true;
        }
    }

    public void setMinSimilarity(double minSimilarity) {
        currentSimilarity = minSimilarity;
        MinSimilarity = minSimilarity;
        CheckLastSeenSimilar = (float) minSimilarity;

    }

    public void restetMinSimilarity() {
        setMinSimilarity(props.getDefaultRegionSimilarity());
    }
}
