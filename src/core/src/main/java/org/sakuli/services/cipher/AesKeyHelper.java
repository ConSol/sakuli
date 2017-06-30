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
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Optional;

/**
 * @author tschneck
 *         Date: 6/29/17
 */
public class AesKeyHelper {

    public static final int KEYSIZE = 128;
    public static final String ALGORITHM = "AES";
    //    TODO TS validate later!
    private static final Object CLI_COMMAND = "sakuli encrypt generate-masterkey";

    /**
     * extracted from https://stackoverflow.com/questions/35907877/aes-encryption-ivs
     */
    private static SecretKey createKey(final String algorithm, final int keysize, final Optional<Provider> provider, final Optional<SecureRandom> rng) throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator;
        if (provider.isPresent()) {
            keyGenerator = KeyGenerator.getInstance(algorithm, provider.get());
        } else {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        }

        if (rng.isPresent()) {
            keyGenerator.init(keysize, rng.get());
        } else {
            // not really needed for the Sun provider which handles null OK
            keyGenerator.init(keysize);
        }

        return keyGenerator.generateKey();
    }

    public static SecretKey createRandomKey() throws SakuliCipherException {
        try {
            return AesKeyHelper.createKey(ALGORITHM, KEYSIZE, Optional.empty(), Optional.empty());
        } catch (NoSuchAlgorithmException e) {
            throw new SakuliCipherException(e, "Unexpected error during generation of new random AES key");
        }
    }

    public static String createRandomBase64Key() throws SakuliCipherException {
        try {
            return new BASE64Encoder().encode(createRandomKey().getEncoded());
        } catch (Exception e) {
            throw new SakuliCipherException(e, "Unexpected error during converting of AES key to Base64");
        }
    }

    public static SecretKey readBase64Keay(String base64Key) throws SakuliCipherException {
        final String error_msg = String.format(" - master key have to be bas64 encoded %s key (%s bit)! Please use the Sakuli CLI command '%s' " +
                        "to generate a valid masterkey!",
                ALGORITHM, KEYSIZE, CLI_COMMAND);
        if (base64Key == null) {
            throw new SakuliCipherException("master key is NULL " + error_msg);
        }
        try {
            byte[] origKeyByte = new BASE64Decoder().decodeBuffer(base64Key);
            return new SecretKeySpec(origKeyByte, 0, origKeyByte.length, ALGORITHM);
        } catch (IOException e) {
            throw new SakuliCipherException(e, "Unable to decode base64 key '' to bytes" + error_msg);
        }
    }
}
