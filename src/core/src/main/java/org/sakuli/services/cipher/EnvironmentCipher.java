/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2017 the original author or authors.
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

package org.sakuli.services.cipher;

import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.exceptions.SakuliCipherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * @author tschneck
 *         Date: 6/28/17
 */
@ProfileCipherEnv
@Component
public class EnvironmentCipher extends AbstractCipher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentCipher.class);
    private final CipherProperties cipherProperties;

    @Autowired
    public EnvironmentCipher(CipherProperties cipherProperties) {
        this.cipherProperties = cipherProperties;
    }

    @Override
    String getCipherInfoOutput() {
        return "use encryption key from environment variable '" + CipherProperties.ENCRYPTION_KEY + "'";
    }

    @Override
    protected SecretKey getKey() throws SakuliCipherException {
        LOGGER.debug("read master key from environment: {}", cipherProperties.getEncryptionKey());
        return AesKeyHelper.readBase64Keay(cipherProperties.getEncryptionKey());
    }
}
