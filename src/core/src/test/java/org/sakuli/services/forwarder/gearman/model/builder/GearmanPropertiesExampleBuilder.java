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

import org.sakuli.builder.ExampleBuilder;
import org.sakuli.services.forwarder.gearman.GearmanProperties;

/**
 * @author tschneck
 *         Date: 11.07.14
 */
public class GearmanPropertiesExampleBuilder implements ExampleBuilder<GearmanProperties> {

    @Override
    public GearmanProperties buildExample() {
        GearmanProperties props = new GearmanProperties();
        props.setNagiosHost("testhost");
        props.setServerHost("localhost");
        props.setServerPort(4730);
        props.setServerQueue("check_results");
        props.setOutputSuiteSummary(GearmanProperties.NAGIOS_OUTPUT_SUITE_SUMMARY_DEFAULT);
        props.setOutputSuiteTable(GearmanProperties.NAGIOS_OUTPUT_SUITE_TABLE_DEFAULT);
        props.setOutputCaseOk(GearmanProperties.NAGIOS_OUTPUT_CASE_OK_DEFAULT);
        props.setOutputCaseWarning(GearmanProperties.NAGIOS_OUTPUT_CASE_WARNING_DEFAULT);
        props.setOutputCaseCritical(GearmanProperties.NAGIOS_OUTPUT_CASE_CRITICAL_DEFAULT);
        props.setOutputCaseError(GearmanProperties.NAGIOS_OUTPUT_CASE_ERROR_DEFAULT);

        return props;
    }
}
