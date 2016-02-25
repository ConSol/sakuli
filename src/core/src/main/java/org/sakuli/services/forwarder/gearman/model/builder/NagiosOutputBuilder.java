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

package org.sakuli.services.forwarder.gearman.model.builder;

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.Builder;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.services.forwarder.AbstractOutputBuilder;
import org.sakuli.services.forwarder.OutputState;
import org.sakuli.services.forwarder.gearman.GearmanProperties;
import org.sakuli.services.forwarder.gearman.ProfileGearman;
import org.sakuli.services.forwarder.gearman.model.NagiosOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tschneck Date: 11.07.14
 */
@ProfileGearman
@Component
public class NagiosOutputBuilder extends AbstractOutputBuilder implements Builder<NagiosOutput> {
    private static final String TABLE_HEADER = "<table style=\"border-collapse: collapse;\">";
    private static final String TABLE_FOOTER = "</table>";

    @Autowired
    private GearmanProperties gearmanProperties;
    @Autowired
    private NagiosPerformanceDataBuilder nagiosPerformanceDataBuilder;

    @Override
    protected String getOutputScreenshotDivWidth() {
        return gearmanProperties.getOutputScreenshotDivWidth();
    }

    @Override
    public NagiosOutput build() {
        NagiosOutput output = new NagiosOutput();
        output.setStatusSummary(getStatusSummary(testSuite, gearmanProperties));
        output.setPerformanceData(nagiosPerformanceDataBuilder.build());
        return output;
    }

    /**
     * Generates the displayed overview data for the nagios server as an {@link String}.
     *
     * @param testSuite finshed {@link TestSuite}
     * @return formatted payload string
     */
    protected String getStatusSummary(TestSuite testSuite, GearmanProperties properties) {
        OutputState outputState = OutputState.lookupSakuliState(testSuite.getState());
        StringBuilder sb = new StringBuilder();
        sb.append(outputState.name())
                .append(" - ")
                .append(StringUtils.remove(formatTestSuiteSummaryStateMessage(testSuite, properties.getOutputSuiteSummary()), "\n"))
                .append(NagiosOutput.DETAILS_SEPARATOR)
                .append(TABLE_HEADER)
                .append(StringUtils.remove(formatTestSuiteTableStateMessage(testSuite, properties.getOutputSuiteTable()), "\n"));

        for (TestCase tc : testSuite.getTestCasesAsSortedSet()) {
            sb.append(formatTestCaseTableStateMessage(tc, properties.lookUpOutputString(tc.getState())));
        }
        sb.append(TABLE_FOOTER);
        return sb.toString();
    }

}
