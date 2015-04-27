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

package org.sakuli.datamodel.properties;

import org.sakuli.starter.helper.SahiProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author tschneck Date: 14.05.14
 */
@Component
@SuppressWarnings("unused")
public class SahiProxyProperties extends AbstractProperties {

    public static final String PROXY_HOME_FOLDER = "sahi.proxy.homePath";
    public static final String PROXY_CONFIG_FOLDER = "sahi.proxy.configurationPath";
    public static final String PROXY_PORT = "sahi.proxy.port";
    /**
     * Mapping between sakuli.properties {@link #PROXY_PORT} and sahi orignal property key in 'userdata.properties'.
     */
    public static final String SAHI_PROPERTY_PROXY_PORT_MAPPING = "proxy.port";
    public static final String MAX_CONNECT_TRIES = "sahi.proxy.maxConnectTries";
    public static final String RECONNECT_SECONDS = "sahi.proxy.reconnectSeconds";
    public static final String REQUEST_DELAY_MS = "sahi.proxy.requestDelayOnSikuliInput.delayTime";
    public static final String REQUEST_DELAY_REFRESH_MS = "sahi.proxy.requestDelayOnSikuliInput.refreshTime";
    public static final String DEFAULT_PROXY_PORT = "9999";
    public static final String DEFAULT_RECONNECT_SECONDS = "5";
    public static final String DEFAULT_MAX_CONNECT_TRIES = "5";

    public static final String SAHI_PROPERTY_FILE_APPENDER = File.separator + "config" + File.separator + "userdata.properties";
    public static final String SAHI_LOG_PROPERTY_FILE_APPENDER = File.separator + "config" + File.separator + "log.properties";

    public static final String SAHI_REQUEST_DELAY_ACTIVE_VAR = "sakuli-delay-active";
    public static final String SAHI_REQUEST_DELAY_TIME_VAR = "sakuli-delay-time";
    public static final String SAHI_JS_INJECT_CODE_FILENAME = "inject.js";
    public static final String SAHI_JS_INJECT_CODE_FILENAME_APPENDER = File.separator + "internal" + File.separator + SAHI_JS_INJECT_CODE_FILENAME;
    public static final String SAHI_JS_INJECT_CONFIG_FILE_APPENDER = File.separator + "config" + File.separator + "inject_top.txt";
    public static final String SAHI_JS_INJECT_TARGET_FOLDER_APPENDER = File.separator + "htdocs" + File.separator + "spr" + File.separator + "sakuli";
    public static final String SAHI_JS_INJECT_TARGET_FILE_APPENDER = SAHI_JS_INJECT_TARGET_FOLDER_APPENDER + File.separator + SAHI_JS_INJECT_CODE_FILENAME;

    public static final String SAHI_LOG_DIR = "logs.dir";
    public static final String HTTP_PROXY_ENABLED = "ext.http.proxy.enable";
    public static final String HTTP_PROXY_HOST = "ext.http.proxy.host";
    public static final String HTTP_PROXY_PORT = "ext.http.proxy.port";
    public static final String HTTP_PROXY_AUTH_ENABLED = "ext.http.proxy.auth.enable";
    public static final String HTTP_PROXY_AUTH_NAME = "ext.http.proxy.auth.name";
    public static final String HTTP_PROXY_AUTH_PASSWORD = "ext.http.proxy.auth.password";
    public static final String HTTPS_PROXY_ENABLED = "ext.https.proxy.enable";
    public static final String HTTPS_PROXY_HOST = "ext.https.proxy.host";
    public static final String HTTPS_PROXY_PORT = "ext.https.proxy.port";
    public static final String HTTPS_PROXY_AUTH_ENABLED = "ext.https.proxy.auth.enable";
    public static final String HTTPS_PROXY_AUTH_NAME = "ext.https.proxy.auth.name";
    public static final String HTTPS_PROXY_AUTH_PASSWORD = "ext.https.proxy.auth.password";
    public static final String HTTP_HTTPS_BYBASS_HOSTS = "ext.http.both.proxy.bypass_hosts";
    public static final List<String> userdataPropertyNames = Arrays.asList(SAHI_LOG_DIR,
            HTTP_PROXY_ENABLED, HTTP_PROXY_HOST, HTTP_PROXY_PORT, HTTP_PROXY_AUTH_ENABLED,
            HTTP_PROXY_AUTH_NAME, HTTP_PROXY_AUTH_PASSWORD,
            HTTPS_PROXY_ENABLED, HTTPS_PROXY_HOST, HTTPS_PROXY_PORT, HTTPS_PROXY_AUTH_ENABLED,
            HTTPS_PROXY_AUTH_NAME, HTTPS_PROXY_AUTH_PASSWORD,
            HTTP_HTTPS_BYBASS_HOSTS
    );
    public static final String SAHI_HANLDER = "handlers";
    public static final String SAHI_LOG_CONSOLE_HANLDER_LEVEL = "java.util.logging.ConsoleHandler.level";
    public static final String SAHI_LOG_FILE_HANDLER = "java.util.logging.FileHandler.level";
    public static final String SAHI_LOG_CONSOLE_HANDLER_FORMATTER = "java.util.logging.ConsoleHandler.formatter";
    public static final String SAHI_LOG_FILE_HANDLER_FORMATTER = "java.util.logging.FileHandler.formatter";
    public static final String SAHI_LOG_FILE_HANDLER_LIMIT = "java.util.logging.FileHandler.limit";
    public static final String SAHI_LOG_FILE_HANDLER_COUNT = "java.util.logging.FileHandler.count";
    public static final String SAHI_LOG_FILE_HANDLER_PATERN = "java.util.logging.FileHandler.pattern";
    public static final List<String> logPropertyNames = Arrays.asList(SAHI_HANLDER, SAHI_LOG_CONSOLE_HANLDER_LEVEL,
            SAHI_LOG_FILE_HANDLER, SAHI_LOG_CONSOLE_HANDLER_FORMATTER, SAHI_LOG_FILE_HANDLER_FORMATTER,
            SAHI_LOG_FILE_HANDLER_LIMIT, SAHI_LOG_FILE_HANDLER_COUNT, SAHI_LOG_FILE_HANDLER_PATERN);
    public static final Logger LOGGER = LoggerFactory.getLogger(SahiProxyProperties.class);

    @Value("${" + PROXY_HOME_FOLDER + "}")
    private String sahiHomeFolderPropertyValue;
    private Path sahiHomeFolder;
    @Value("${" + PROXY_CONFIG_FOLDER + "}")
    private String sahiConfigFolderPropertyValue;
    private Path sahiConfigFolder;
    @Value("${" + PROXY_PORT + ":" + DEFAULT_PROXY_PORT + "}")
    private Integer proxyPort;
    @Value("${" + RECONNECT_SECONDS + ":" + DEFAULT_RECONNECT_SECONDS + "}")
    private Integer reconnectSeconds;
    @Value("${" + MAX_CONNECT_TRIES + ":" + DEFAULT_MAX_CONNECT_TRIES + "}")
    private Integer maxConnectTries;
    /**
     * Specifies the default delay for one event, which should be used to prevent blocking Request during sikuli based
     * actions in a sahi controlled proxy
     */
    @Value("${" + REQUEST_DELAY_MS + ":}")
    private Integer requestDelayMs;
    @Value("${" + REQUEST_DELAY_REFRESH_MS + ":500}")
    private Integer requestDelayRefreshMs;
    @Autowired
    private SakuliProperties sakuliProperties;
    @Autowired
    private TestSuiteProperties testSuiteProperties;
    private Path sahiJSInjectConfigFile;
    private Path sahiJSInjectSourceFile;
    private Path sahiJSInjectTargetFile;

    @PostConstruct
    public void initFolders() throws FileNotFoundException {
        sahiHomeFolder = Paths.get(sahiHomeFolderPropertyValue).normalize().toAbsolutePath();
        sahiConfigFolder = Paths.get(sahiConfigFolderPropertyValue).normalize().toAbsolutePath();
        if (!testSuiteProperties.isUiTest()) {
            checkFolders(sahiHomeFolder, sahiConfigFolder);
            loadSahiInjectFiles();
        }
    }

    /**
     * Loads the paths for all custom Sahi inject files, which will be needed for {@link
     * SahiProxy#injectCustomJavaScriptFiles()}
     *
     * @throws FileNotFoundException
     */
    protected void loadSahiInjectFiles() throws FileNotFoundException {
        sahiJSInjectConfigFile = Paths.get(sahiHomeFolder.toString() + SAHI_JS_INJECT_CONFIG_FILE_APPENDER);
        sahiJSInjectSourceFile = Paths.get(sakuliProperties.getJsLibFolder() + SAHI_JS_INJECT_CODE_FILENAME_APPENDER);
        checkFiles(sahiJSInjectConfigFile, sahiJSInjectSourceFile);

        //don't check files and folders => will be created during runtime
        sahiJSInjectTargetFile = Paths.get(sahiHomeFolder.toString() + SAHI_JS_INJECT_TARGET_FILE_APPENDER);
    }

    public String getSahiHomeFolderPropertyValue() {
        return sahiHomeFolderPropertyValue;
    }

    public void setSahiHomeFolderPropertyValue(String sahiHomeFolderPropertyValue) {
        this.sahiHomeFolderPropertyValue = sahiHomeFolderPropertyValue;
    }

    public String getSahiConfigFolderPropertyValue() {
        return sahiConfigFolderPropertyValue;
    }

    public void setSahiConfigFolderPropertyValue(String sahiConfigFolderPropertyValue) {
        this.sahiConfigFolderPropertyValue = sahiConfigFolderPropertyValue;
    }

    public Path getSahiHomeFolder() {
        return sahiHomeFolder;
    }

    public void setSahiHomeFolder(Path sahiHomeFolder) {
        this.sahiHomeFolder = sahiHomeFolder;
    }

    public Path getSahiConfigFolder() {
        return sahiConfigFolder;
    }

    public void setSahiConfigFolder(Path sahiConfigFolder) {
        this.sahiConfigFolder = sahiConfigFolder;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Integer getReconnectSeconds() {
        return reconnectSeconds;
    }

    public void setReconnectSeconds(Integer reconnectSeconds) {
        this.reconnectSeconds = reconnectSeconds;
    }

    public Integer getMaxConnectTries() {
        return maxConnectTries;
    }

    public void setMaxConnectTries(Integer maxConnectTries) {
        this.maxConnectTries = maxConnectTries;
    }

    public Path getSahiJSInjectConfigFile() {
        return sahiJSInjectConfigFile;
    }

    public void setSahiJSInjectConfigFile(Path sahiJSInjectConfigFile) {
        this.sahiJSInjectConfigFile = sahiJSInjectConfigFile;
    }

    public Path getSahiJSInjectSourceFile() {
        return sahiJSInjectSourceFile;
    }

    public void setSahiJSInjectSourceFile(Path sahiJSInjectSourceFile) {
        this.sahiJSInjectSourceFile = sahiJSInjectSourceFile;
    }

    public Path getSahiJSInjectTargetFile() {
        return sahiJSInjectTargetFile;
    }

    public void setSahiJSInjectTargetFile(Path sahiJSInjectTargetFile) {
        this.sahiJSInjectTargetFile = sahiJSInjectTargetFile;
    }

    public Integer getRequestDelayMs() {
        return requestDelayMs;
    }

    public void setRequestDelayMs(Integer requestDelayMs) {
        this.requestDelayMs = requestDelayMs;
    }

    public boolean isRequestDelayActive() {
        return requestDelayMs != null && requestDelayMs > 0;
    }

    public Integer getRequestDelayRefreshMs() {
        return requestDelayRefreshMs;
    }

    public void setRequestDelayRefreshMs(Integer requestDelayRefreshMs) {
        this.requestDelayRefreshMs = requestDelayRefreshMs;
    }
}
