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

package org.sakuli.services.forwarder.icinga2;

import org.sakuli.services.forwarder.AbstractMonitoringTemplateProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck
 *         Date: 2/22/16
 */
@ProfileIcinga2
@Component
public class Icinga2Properties extends AbstractMonitoringTemplateProperties {

    private static final String HOSTNAME = "sakuli.forwarder.icinga2.hostname";
    private static final String API_HOST = "sakuli.forwarder.icinga2.api.host";
    private static final String API_PORT = "sakuli.forwarder.icinga2.api.port";
    private static final String API_USERNAME = "sakuli.forwarder.icinga2.api.username";
    private static final String API_PASSWORD = "sakuli.forwarder.icinga2.api.password";
    private static final String API_URL = "sakuli.forwarder.icinga2.api.url";
    private static final String TEMPLATE_SUITE_SUMMARY = "sakuli.forwarder.icinga2.template.suite.summary";
    private static final String TEMPLATE_SUITE_SUMMARY_MAX_LENGTH = "sakuli.forwarder.icinga2.template.suite.summary.maxLength";
    private static final String TEMPLATE_CASE_OK = "sakuli.forwarder.icinga2.template.case.ok";
    private static final String TEMPLATE_CASE_WARNING = "sakuli.forwarder.icinga2.template.case.warning";
    private static final String TEMPLATE_CASE_WARNING_IN_STEP = "sakuli.forwarder.icinga2.template.case.warningInStep";
    private static final String TEMPLATE_CASE_CRITICAL = "sakuli.forwarder.icinga2.template.case.critical";
    private static final String TEMPLATE_CASE_ERROR = "sakuli.forwarder.icinga2.template.case.error";

    @Value("${" + TEMPLATE_SUITE_SUMMARY + "}")
    private String templateSuiteSummary;
    @Value("${" + TEMPLATE_SUITE_SUMMARY_MAX_LENGTH + "}")
    private int templateSuiteSummaryMaxLength;
    @Value("${" + TEMPLATE_CASE_OK + "}")
    private String templateCaseOk;
    @Value("${" + TEMPLATE_CASE_WARNING + "}")
    private String templateCaseWarning;
    @Value("${" + TEMPLATE_CASE_WARNING_IN_STEP + "}")
    private String templateCaseWarningInStep;
    @Value("${" + TEMPLATE_CASE_CRITICAL + "}")
    private String templateCaseCritical;
    @Value("${" + TEMPLATE_CASE_ERROR + "}")
    private String templateCaseError;
    @Value("${" + HOSTNAME + "}")
    private String hostName;
    @Value("${" + API_HOST + "}")
    private String apiHost;
    @Value("${" + API_PORT + "}")
    private int apiPort;
    @Value("${" + API_USERNAME + "}")
    private String apiUsername;
    @Value("${" + API_PASSWORD + "}")
    private String apiPassword;
    @Value("${" + API_URL + "}")
    private String apiURL;

    @Override
    public String getTemplateSuiteSummary() {
        return templateSuiteSummary;
    }

    public void setTemplateSuiteSummary(String templateSuiteSummary) {
        this.templateSuiteSummary = templateSuiteSummary;
    }

    @Override
    public int getTemplateSuiteSummaryMaxLength() {
        return templateSuiteSummaryMaxLength;
    }

    public void setTemplateSuiteSummaryMaxLength(int templateSuiteSummaryMaxLength) {
        this.templateSuiteSummaryMaxLength = templateSuiteSummaryMaxLength;
    }

    @Override
    public String getTemplateCaseOk() {
        return templateCaseOk;
    }

    public void setTemplateCaseOk(String templateCaseOk) {
        this.templateCaseOk = templateCaseOk;
    }

    @Override
    public String getTemplateCaseWarning() {
        return templateCaseWarning;
    }

    public void setTemplateCaseWarning(String templateCaseWarning) {
        this.templateCaseWarning = templateCaseWarning;
    }

    @Override
    public String getTemplateCaseWarningInStep() {
        return templateCaseWarningInStep;
    }

    public void setTemplateCaseWarningInStep(String templateCaseWarningInStep) {
        this.templateCaseWarningInStep = templateCaseWarningInStep;
    }

    @Override
    public String getTemplateCaseCritical() {
        return templateCaseCritical;
    }

    public void setTemplateCaseCritical(String templateCaseCritical) {
        this.templateCaseCritical = templateCaseCritical;
    }

    @Override
    public String getTemplateCaseError() {
        return templateCaseError;
    }

    public void setTemplateCaseError(String templateCaseError) {
        this.templateCaseError = templateCaseError;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

}
