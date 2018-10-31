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

package org.sakuli.services.forwarder.gearman;

import org.sakuli.services.forwarder.AbstractMonitoringTemplateProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck Date: 10.07.14
 */
@ProfileGearman
@Component
public class GearmanProperties extends AbstractMonitoringTemplateProperties {
    protected static final String TEMPLATE_SUITE_SUMMARY = "sakuli.forwarder.gearman.nagios.template.suite.summary";
    protected static final String TEMPLATE_SUITE_SUMMARY_MAX_LENGTH = "sakuli.forwarder.gearman.nagios.template.suite.summary.maxLength";
    protected static final String TEMPLATE_CASE_OK = "sakuli.forwarder.gearman.nagios.template.case.ok";
    protected static final String TEMPLATE_CASE_WARNING = "sakuli.forwarder.gearman.nagios.template.case.warning";
    protected static final String TEMPLATE_CASE_WARNING_IN_STEP = "sakuli.forwarder.gearman.nagios.template.case.warningInStep";
    protected static final String TEMPLATE_CASE_CRITICAL = "sakuli.forwarder.gearman.nagios.template.case.critical";
    protected static final String TEMPLATE_CASE_ERROR = "sakuli.forwarder.gearman.nagios.template.case.error";
    private static final String SERVICE_TYPE_DEFAULT = "passive";
    private static final String SERVER_QUEUE = "sakuli.forwarder.gearman.server.queue";
    private static final String SERVER_HOST = "sakuli.forwarder.gearman.server.host";
    private static final String SERVER_PORT = "sakuli.forwarder.gearman.server.port";
    private static final String CACHE_ENABLED = "sakuli.forwarder.gearman.cache.enabled";
    private static final String JOB_INTERVAL = "sakuli.forwarder.gearman.job.interval";
    private static final String ENCRYPTION = "sakuli.forwarder.gearman.encryption";
    private static final String SECRET_KEY = "sakuli.forwarder.gearman.secret.key";
    private static final String HASH_IDS = "sakuli.forwarder.gearman.hash.ids";
    private static final String NAGIOS_HOSTNAME = "sakuli.forwarder.gearman.nagios.hostname";
    private static final String NAGIOS_CHECK_COMMAND = "sakuli.forwarder.gearman.nagios.check_command";
    private static final String NAGIOS_SERVICE_DESCRIPTION = "sakuli.forwarder.gearman.nagios.service_description";
    private static final String TEMPLATE_SUITE_TABLE = "sakuli.forwarder.gearman.nagios.template.suite.table";
    private static final String TEMPLATE_SCREENSHOT_DIV_WIDTH = "sakuli.forwarder.gearman.nagios.template.screenshotDivWidth";
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
    private String serviceType = SERVICE_TYPE_DEFAULT;
    @Value("${" + SERVER_QUEUE + "}")
    private String serverQueue;
    @Value("${" + SERVER_HOST + "}")
    private String serverHost;
    @Value("${" + SERVER_PORT + "}")
    private int serverPort;
    @Value("${" + CACHE_ENABLED + "}")
    private boolean cacheEnabled;
    @Value("${" + JOB_INTERVAL + "}")
    private long jobInterval;
    @Value("${" + ENCRYPTION + "}")
    private boolean encryption;
    @Value("${" + SECRET_KEY + "}")
    private String secretKey;
    @Value("${" + HASH_IDS + "}")
    private boolean idHashing;
    @Value("${" + NAGIOS_HOSTNAME + "}")
    private String nagiosHost;
    @Value("${" + NAGIOS_CHECK_COMMAND + "}")
    private String nagiosCheckCommand;
    @Value("${" + NAGIOS_SERVICE_DESCRIPTION + "}")
    private String nagiosServiceDescription;
    @Value("${" + TEMPLATE_SUITE_TABLE + "}")
    private String templateSuiteTable;
    @Value("${" + TEMPLATE_SCREENSHOT_DIV_WIDTH + "}")
    private String templateScreenshotDivWidth;

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

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public long getJobInterval() {
        return this.jobInterval;
    }

    public void setJobInterval(long jobInterval) {
        this.jobInterval = jobInterval;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public boolean isEncryption() {
        return encryption;
    }

    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getNagiosHost() {
        return nagiosHost;
    }

    public void setNagiosHost(String nagiosHost) {
        this.nagiosHost = nagiosHost;
    }

    public String getNagiosCheckCommand() {
        return nagiosCheckCommand;
    }

    public String getNagiosServiceDescription() {
        return nagiosServiceDescription;
    }

    public void setNagiosCheckCommand(String nagiosCheckCommand) {
        this.nagiosCheckCommand = nagiosCheckCommand;
    }

    public void setNagiosServiceDescription(String nagiosServiceDescription) {
        this.nagiosServiceDescription = nagiosServiceDescription;
    }

    public String getTemplateSuiteTable() {
        return templateSuiteTable;
    }

    public void setTemplateSuiteTable(String templateSuiteTable) {
        this.templateSuiteTable = templateSuiteTable;
    }

    public String getTemplateScreenshotDivWidth() {
        return templateScreenshotDivWidth;
    }

    public void setTemplateScreenshotDivWidth(String templateScreenshotDivWidth) {
        this.templateScreenshotDivWidth = templateScreenshotDivWidth;
    }

    public boolean isIdHashing() {
        return idHashing;
    }

    public void setIdHashing(boolean idHashing) {
        this.idHashing = idHashing;
    }

}
