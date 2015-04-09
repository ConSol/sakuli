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

package org.sakuli.services.receiver.gearman;

import org.sakuli.datamodel.state.TestCaseState;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 14.07.14
 */
public class GearmanPropertiesTestHelper {

    public static GearmanProperties initMock(GearmanProperties mock) {
        when(mock.getNagiosCheckCommand()).thenReturn(GearmanProperties.NAGIOS_CHECK_COMMAND_DEFAULT);
        when(mock.getOutputSuiteSummary()).thenReturn(GearmanProperties.NAGIOS_OUTPUT_SUITE_SUMMARY_DEFAULT);
        when(mock.getOutputSuiteTable()).thenReturn(GearmanProperties.NAGIOS_OUTPUT_SUITE_TABLE_DEFAULT);
        when(mock.lookUpOutputString(TestCaseState.OK)).thenReturn(GearmanProperties.NAGIOS_OUTPUT_CASE_OK_DEFAULT);
        when(mock.lookUpOutputString(TestCaseState.WARNING)).thenReturn(GearmanProperties.NAGIOS_OUTPUT_CASE_WARNING_DEFAULT);
        when(mock.lookUpOutputString(TestCaseState.WARNING_IN_STEP)).thenReturn(GearmanProperties.NAGIOS_OUTPUT_CASE_WARNING_DEFAULT);
        when(mock.lookUpOutputString(TestCaseState.CRITICAL)).thenReturn(GearmanProperties.NAGIOS_OUTPUT_CASE_CRITICAL_DEFAULT);
        when(mock.lookUpOutputString(TestCaseState.ERRORS)).thenReturn(GearmanProperties.NAGIOS_OUTPUT_CASE_ERROR_DEFAULT);
        when(mock.getOutputScreenshotDivWidth()).thenReturn(GearmanProperties.NAGIOS_OUTPUT_SCREENSHOT_DIV_WIDTH_DEFAULT);
        when(mock.getServiceType()).thenReturn(GearmanProperties.SERVICE_TYPE_DEFAULT);
        return mock;
    }
}
