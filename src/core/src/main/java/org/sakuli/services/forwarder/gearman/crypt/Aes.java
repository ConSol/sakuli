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

package org.sakuli.services.forwarder.gearman.crypt;

import org.apache.commons.codec.binary.Base64;
import org.gearman.common.GearmanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * @author Christoph Deppisch
 */
public class Aes {

    public static final int DEFAULT_KEY_LENGTH = 32;
    public static final String CRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final Logger LOGGER = LoggerFactory.getLogger(Aes.class);
    /** Key length in byte that should be used, default is 32 = 256 bit */
    public static int keyLength = DEFAULT_KEY_LENGTH;

    /**
     * Encrypt text with AES-256/ECP mode.
     * @param text
     * @param password
     * @return
     */
    public static byte[] encrypt(String text, String password) {
        try {
            LOGGER.debug("encrypt data using " + CRYPT_ALGORITHM + " algorithm");
            Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            return Base64.encodeBase64(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            LOGGER.error("Error while encrypting: ", e);
            throw new GearmanException("Error while encrypting: " + e.getMessage());
        }
    }

    /**
     * Decrypt with AES-256/ECP mode.
     * @param enrcypted
     * @param password
     * @return
     */
    public static String decrypt(byte[] enrcypted, String password) {
        try {
            LOGGER.debug("decrypt data using " + CRYPT_ALGORITHM + " algorithm");
            Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            return new String(cipher.doFinal(Base64.decodeBase64(enrcypted)));
        } catch (Exception e) {
            LOGGER.error("Error while decrypting: ", e);
            throw new GearmanException("Error while decrypting: " + e.getMessage());
        }
    }

    /**
     * Create proper secret key spec from password.
     * @param password
     * @return
     */
    private static SecretKeySpec getSecretKey(String password){
        try {
            byte[] key = Arrays.copyOf(password.getBytes(), keyLength); // use only first 256 bit
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            LOGGER.error("Error while creating secret key: ", e);
            throw new GearmanException("Error while creating secret key: " + e.getMessage());
        }
    }
}
