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

import org.apache.commons.codec.binary.Base64;
import org.sakuli.exceptions.SakuliCipherException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;

/**
 * Abstract class for the supported ciphers modules
 *
 * @author tschneck
 *         Date: 6/28/17
 */
public abstract class AbstractCipher implements CipherService {
    private static String IV_KEY = "IVcon17SakSoENVS";
    private static String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Converts a String input to a byte array
     */
    static byte[] convertStringToBytes(String s) {
        if (s == null) {
            throw new InvalidParameterException("can't convert null String to byte array");
        }
        return s.getBytes(Charset.defaultCharset());
    }

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
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), getIV());
            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            throw new SakuliCipherException(e, getPreLogOutput());
        }
    }

    abstract String getPreLogOutput();

    /**
     * Decrypts a String to the secret. The decryption must be take place on the same physical machine like the encryption, see {@link #encrypt(String)}.
     *
     * @param strToDecrypt String to encrypt
     * @return the decrypted secret
     * @throws SakuliCipherException if the decryption fails.
     */
    public String decrypt(String strToDecrypt) throws SakuliCipherException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), getIV());
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
        } catch (IllegalBlockSizeException e) {
            throw new SakuliCipherException("Maybe this secret hasn't been encrypted correctly! Maybe encrypt it again!", getPreLogOutput(), e);
        } catch (Exception e) {
            throw new SakuliCipherException(e, getPreLogOutput());
        }
    }

    /**
     * build the initialization vector
     *
     * @return byte array wrapped {@link IvParameterSpec}
     */
    protected IvParameterSpec getIV() {
        return new IvParameterSpec(AbstractCipher.convertStringToBytes(IV_KEY));
    }

    /**
     * @return the expected master key for the encryption
     */
    protected abstract SecretKeySpec getKey();
}
