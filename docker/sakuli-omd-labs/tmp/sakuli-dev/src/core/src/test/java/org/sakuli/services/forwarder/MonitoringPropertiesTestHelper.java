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

package org.sakuli.services.forwarder;

import org.sakuli.datamodel.state.TestCaseState;
import org.sakuli.services.forwarder.gearman.GearmanProperties;

import static org.mockito.Mockito.when;

/**
 * @author tschneck
 *         Date: 14.07.14
 */
public class MonitoringPropertiesTestHelper {

    public static final String NAGIOS_CHECK_COMMAND_DEFAULT = "check_sakuli";
    public static final String TEMPLATE_SUITE_SUMMARY = "{{state_short}} Sakuli suite \"{{id}}\" {{suite_summary}}. (Last suite run: {{stop_date}})";

    public static AbstractMonitoringTemplateProperties initMonitoringMock(AbstractMonitoringTemplateProperties mock) {
        when(mock.getTemplateSuiteSummary()).thenReturn(
                TEMPLATE_SUITE_SUMMARY
        );
        when(mock.lookUpTemplate(TestCaseState.OK)).thenReturn(
                "{{state_short}} case \"{{id}}\" ran in {{duration}}s - {{state_description}}"
        );
        when(mock.lookUpTemplate(TestCaseState.WARNING)).thenReturn(
                "{{state_short}} case \"{{id}}\" over runtime ({{duration}}s/warn at {{warning_threshold}}s){{step_information}}"
        );
        when(mock.lookUpTemplate(TestCaseState.WARNING_IN_STEP)).thenReturn(
                "{{state_short}} case \"{{id}}\" ({{duration}}s) ok{{step_information}}"
        );
        when(mock.lookUpTemplate(TestCaseState.CRITICAL)).thenReturn(
                "{{state_short}} case \"{{id}}\" over runtime ({{duration}}s/crit at {{critical_threshold}}s){{step_information}}"
        );
        when(mock.lookUpTemplate(TestCaseState.ERRORS)).thenReturn(
                "{{state_short}} case \"{{id}}\" {{state_description}}: {{error_message}}"
        );
        when(mock.getTemplateSuiteSummaryMaxLength()).thenReturn(200);
        return mock;
    }

    public static GearmanProperties initMock(GearmanProperties mock) {
        initMonitoringMock(mock);
        when(mock.getNagiosCheckCommand()).thenReturn(NAGIOS_CHECK_COMMAND_DEFAULT);
        when(mock.getTemplateSuiteTable()).thenReturn(
                "{{state_short}} Sakuli suite \"{{id}}\" {{suite_summary}}. (Last suite run: {{stop_date}}){{error_screenshot}}"
        );
        when(mock.lookUpTemplate(TestCaseState.ERRORS)).thenReturn(
                "{{state_short}} case \"{{id}}\" {{state_description}}: {{error_message}}{{error_screenshot}}"
        );
        when(mock.getTemplateScreenshotDivWidth()).thenReturn("640px");
        when(mock.getServiceType()).thenReturn("passive");
        return mock;
    }
}
