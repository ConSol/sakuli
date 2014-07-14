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

package de.consol.sakuli.services.receiver.gearman;

import de.consol.sakuli.datamodel.state.TestCaseState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 10.07.14
 */
@ProfileGearman
@Component
public class GearmanProperties {
    public static final String SERVICE_TYPE_DEFAULT = "passive";
    private String serviceType = SERVICE_TYPE_DEFAULT;
    public static final String SERVER_QUEUE_DEFAULT = "check_results";
    public static final String SERVER_QUEUE = "sakuli.receiver.gearman.server.queue";
    public static final String SERVER_HOST = "sakuli.receiver.gearman.server.host";
    public static final String SERVER_PORT = "sakuli.receiver.gearman.server.port";
    public static final String NAGIOS_HOSTNAME = "sakuli.receiver.gearman.nagios.hostname";
    public static final String NAGIOS_OUTPUT_SUITE_SUMMARY = "sakuli.receiver.gearman.nagios.output.suite.summary";
    public static final String NAGIOS_OUTPUT_SUITE_TABLE = "sakuli.receiver.gearman.nagios.output.suite.table";
    public static final String NAGIOS_OUTPUT_CASE_OK = "sakuli.receiver.gearman.nagios.output.case.ok";
    public static final String NAGIOS_OUTPUT_CASE_WARNING = "sakuli.receiver.gearman.nagios.output.case.warning";
    public static final String NAGIOS_OUTPUT_CASE_CRITICAL = "sakuli.receiver.gearman.nagios.output.case.critical";
    public static final String NAGIOS_OUTPUT_CASE_ERROR = "sakuli.receiver.gearman.nagios.output.case.error";
    public static final String NAGIOS_OUTPUT_SUITE_SUMMARY_DEFAULT = "{{state}} - {{state_short}} Sakuli suite \"{{id}}\" ran in {{duration}} seconds - {{state_summary}}. (Last suite run: {{last_run}})";
    public static final String NAGIOS_OUTPUT_SUITE_TABLE_DEFAULT = "{{state_short}} Sakuli suite \"{{id}}\" ran in {{duration}} seconds - {{state_summary}}. (Last suite run: {{last_run}})";
    public static final String NAGIOS_OUTPUT_CASE_OK_DEFAULT = "{{state_short}} case \"{{id}}\" ran in {{duration}}s - {{state_description}}";
    public static final String NAGIOS_OUTPUT_CASE_WARNING_DEFAULT = "{{state_short}} case \"{{id}}\" over runtime ({{duration}}s /{{state_description}} at {{warning_threshold}}s) {{step_information}}";
    public static final String NAGIOS_OUTPUT_CASE_CRITICAL_DEFAULT = "{{state_short}} case \"{{id}}\" over runtime ({{duration}}s /{{state_description}} at {{critical_threshold}}s) {{step_information}}";
    public static final String NAGIOS_OUTPUT_CASE_ERROR_DEFAULT = "{{state_short}} case \"{{id}}\" {{state_description}}: {{error_message}}";

    @Value("${" + SERVER_QUEUE + ":" + SERVER_QUEUE_DEFAULT + "}")
    private String serverQueue;
    @Value("${" + SERVER_HOST + "}")
    private String serverHost;
    @Value("${" + SERVER_PORT + "}")
    private int serverPort;
    @Value("${" + NAGIOS_HOSTNAME + ":null}")
    private String nagiosHost;
    @Value("${" + NAGIOS_OUTPUT_SUITE_SUMMARY + ":" + NAGIOS_OUTPUT_SUITE_SUMMARY_DEFAULT + "}")
    private String outputSuiteSummary;
    @Value("${" + NAGIOS_OUTPUT_SUITE_TABLE + ":" + NAGIOS_OUTPUT_SUITE_TABLE_DEFAULT + "}")
    private String outputSuiteTable;
    @Value("${" + NAGIOS_OUTPUT_CASE_OK + ":" + NAGIOS_OUTPUT_CASE_OK_DEFAULT + "}")
    private String outputCaseOk;
    @Value("${" + NAGIOS_OUTPUT_CASE_WARNING + ":" + NAGIOS_OUTPUT_CASE_WARNING_DEFAULT + "}")
    private String outputCaseWarning;
    @Value("${" + NAGIOS_OUTPUT_CASE_CRITICAL + ":" + NAGIOS_OUTPUT_CASE_CRITICAL_DEFAULT + "}")
    private String outputCaseCritical;
    @Value("${" + NAGIOS_OUTPUT_CASE_ERROR + ":" + NAGIOS_OUTPUT_CASE_ERROR_DEFAULT + "}")
    private String outputCaseError;

    //TODO add checkresult
    //TODO write test with context
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServerQueue() {
        return serverQueue;
    }

    public void setServerQueue(String serverQueue) {
        this.serverQueue = serverQueue;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getNagiosHost() {
        return nagiosHost;
    }

    public void setNagiosHost(String nagiosHost) {
        this.nagiosHost = nagiosHost;
    }

    public String getOutputSuiteSummary() {
        return outputSuiteSummary;
    }

    public void setOutputSuiteSummary(String outputSuiteSummary) {
        this.outputSuiteSummary = outputSuiteSummary;
    }

    public String getOutputSuiteTable() {
        return outputSuiteTable;
    }

    public void setOutputSuiteTable(String outputSuiteTable) {
        this.outputSuiteTable = outputSuiteTable;
    }

    public String getOutputCaseOk() {
        return outputCaseOk;
    }

    public void setOutputCaseOk(String outputCaseOk) {
        this.outputCaseOk = outputCaseOk;
    }

    public String getOutputCaseError() {
        return outputCaseError;
    }

    public void setOutputCaseError(String outputCaseError) {
        this.outputCaseError = outputCaseError;
    }

    public String getOutputCaseCritical() {
        return outputCaseCritical;
    }

    public void setOutputCaseCritical(String outputCaseCritical) {
        this.outputCaseCritical = outputCaseCritical;
    }

    public String getOutputCaseWarning() {
        return outputCaseWarning;
    }

    public void setOutputCaseWarning(String outputCaseWarning) {
        this.outputCaseWarning = outputCaseWarning;
    }

    public String lookUpOutputString(TestCaseState state) {
        if (state.isOk()) {
            return outputCaseOk;
        } else if (state.isWarning()) {
            return outputCaseWarning;
        } else if (state.isCritical()) {
            return outputCaseCritical;
        } else if (state.isError()) {
            return outputCaseError;
        }
        return null;
    }
}