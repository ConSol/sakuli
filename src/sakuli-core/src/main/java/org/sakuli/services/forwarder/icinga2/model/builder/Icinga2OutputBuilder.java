/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.services.forwarder.icinga2.model.builder;

import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.AbstractOutputBuilder;
import org.sakuli.services.forwarder.icinga2.Icinga2Properties;
import org.sakuli.services.forwarder.icinga2.ProfileIcinga2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 2/24/16
 */
@ProfileIcinga2
@Component
public class Icinga2OutputBuilder extends AbstractOutputBuilder implements Builder<String> {

    public static final String ICINGA_SEPARATOR = "\n";
    @Autowired
    private Icinga2Properties properties;
    @Autowired
    protected TestSuite testSuite;

    @Override
    protected int getSummaryMaxLength() {
        return properties.getTemplateSuiteSummaryMaxLength();
    }

    @Override
    protected String getOutputScreenshotDivWidth() {
        //move to properties, when screenshots are supported in Incinga
        return "640px";
    }


    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(formatTestSuiteSummaryStateMessage(testSuite, properties.getTemplateSuiteSummary()))
                .append(ICINGA_SEPARATOR);

        for (TestCase tc : testSuite.getTestCasesAsSortedSet()) {
            String template = properties.lookUpTemplate(tc.getState());
            String result = replacePlaceHolder(template, getTextPlaceholder(tc));
            LOGGER.debug("{{xxx}} patterns in template '{}' replaced with message '{}'", template, result);
            sb.append(result);
        }
        return sb.toString();
    }

}
