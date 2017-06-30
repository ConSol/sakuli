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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author tschneck Date: 14.05.14
 */
@Component
public class CipherProperties extends AbstractProperties {

    public static final String ENCRYPTION_MODE = "sakuli.encryption.mode";
    public static final String ENCRYPTION_KEY = "sakuli.encryption.key";
    public static final String ENCRYPTION_KEY_ENV = "SAKULI_ENCRYPTION_KEY";
    public static final String ENCRYPTION_INTERFACE = "sakuli.encryption.interface";
    public static final String ENCRYPTION_INTERFACE_AUTODETECT = "sakuli.encryption.interface.autodetect";
    protected static final String ENCRYPTION_MODE_ENVIRONMENT = "environment";
    protected static final String ENCRYPTION_MODE_INTERFACE = "interface";

    //defaults
    protected static final String ENCRYPTION_MODE_DEFAULT = ENCRYPTION_MODE_INTERFACE;
    protected static final String ENCRYPTION_KEY_DEFAULT = "null";
    protected static final String ENCRYPTION_INTERFACE_DEFAULT = "null";
    protected static final String ENCRYPTION_INTERFACE_AUTODETECT_DEFAULT = "true";

    @Value("${" + ENCRYPTION_MODE + ":" + ENCRYPTION_MODE_DEFAULT + "}")
    private String encryptionMode;
    @Value("${" + ENCRYPTION_KEY + ":" + ENCRYPTION_KEY_DEFAULT + "}")
    private String encryptionKey;
    @Value("${" + ENCRYPTION_INTERFACE + ":" + ENCRYPTION_INTERFACE_DEFAULT + "}")
    private String encryptionInterface;
    @Value("${" + ENCRYPTION_INTERFACE_AUTODETECT + ":" + ENCRYPTION_INTERFACE_AUTODETECT_DEFAULT + "}")
    private boolean encryptionInterfaceAutodetect;

    public String getEncryptionInterface() {
        return encryptionInterface;
    }

    public void setEncryptionInterface(String encryptionInterface) {
        this.encryptionInterface = encryptionInterface;
    }

    public boolean isEncryptionInterfaceAutodetect() {
        return encryptionInterfaceAutodetect;
    }

    public void setEncryptionInterfaceAutodetect(boolean encryptionInterfaceAutodetect) {
        this.encryptionInterfaceAutodetect = encryptionInterfaceAutodetect;
    }

    public boolean isEncryptionModeInterface() {
        return ENCRYPTION_MODE_INTERFACE.equals(encryptionMode);
    }

    public boolean isEncryptionModeEnv() {
        return ENCRYPTION_MODE_ENVIRONMENT.equals(encryptionMode);
    }

    public String getEncryptionMode() {
        return encryptionMode;
    }

    public void setEncryptionMode(String encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
