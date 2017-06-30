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

import org.sakuli.exceptions.SakuliCipherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

/**
 * Abstract class for the supported ciphers modules
 *
 * @author tschneck
 *         Date: 6/28/17
 */
public abstract class AbstractCipher implements CipherService {

    private static Logger LOGGER = LoggerFactory.getLogger(CipherService.class);


    /**
     * Encrypts the secret into a encrypted {@link String}, based on the MAC address of the first network interface of a machine.
     * Therewith it should be secured, that an encrypted secret is only valid on one physical machine.
     *
     * @param strToEncrypt the secret
     * @return a encrypted String, which is coupled to one physical machine
     * @throws SakuliCipherException if the encryption fails.
     */
    public String encrypt(String strToEncrypt) throws SakuliCipherException {
        try {
            LOGGER.debug("encrypt secret: {}", strToEncrypt);
            final String encrypted = AesCbcCipher.encryptString(strToEncrypt, getKey());
            LOGGER.debug("encrypted secret: {}", encrypted);
            return encrypted;
        } catch (Exception e) {
            throw new SakuliCipherException(e, "Error during encryption of secret, by cipher: " + getCipherInfoOutput());
        }
    }

    /**
     * Decrypts a String to the secret. The decryption must be take place on the same physical machine like the encryption, see {@link #encrypt(String)}.
     *
     * @param strToDecrypt String to encrypt
     * @return the decrypted secret
     * @throws SakuliCipherException if the decryption fails.
     */
    public String decrypt(String strToDecrypt) throws SakuliCipherException {
        try {
            LOGGER.debug("decrypt secret: {}", strToDecrypt);
            final String decrypted = AesCbcCipher.decryptString(strToDecrypt, getKey());
            LOGGER.debug("decrypted secret: {}", decrypted);
            return decrypted;
        } catch (Exception e) {
            throw new SakuliCipherException(e, "Error during decryption of secret '" + strToDecrypt + "', by cipher: " + getCipherInfoOutput());
        }
    }

    /**
     * @return a cipher specific output message, how the secret key has been created
     */
    abstract String getCipherInfoOutput();

    /**
     * @return the expected master key for the encryption
     */
    protected abstract SecretKey getKey() throws SakuliCipherException;
}
