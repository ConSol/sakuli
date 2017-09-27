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

/**
 * @author tschneck
 * Date: 6/29/17
 */

import org.apache.commons.lang.StringUtils;
import org.sakuli.exceptions.SakuliCipherException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;

public class AesCbcCipher {

    public static final String CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static IvParameterSpec createIV(final int ivSizeBytes, final Optional<SecureRandom> rng) {
        final byte[] iv = new byte[ivSizeBytes];
        final SecureRandom theRNG = rng.orElse(new SecureRandom());
        theRNG.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static IvParameterSpec readIV(final int ivSizeBytes, final InputStream is) throws IOException {
        final byte[] iv = new byte[ivSizeBytes];
        int offset = 0;
        while (offset < ivSizeBytes) {
            final int read = is.read(iv, offset, ivSizeBytes - offset);
            if (read == -1) {
                throw new IOException("Too few bytes for IV in input stream");
            }
            offset += read;
        }
        return new IvParameterSpec(iv);
    }

    public static byte[] decryptBytes(SecretKey aesKey, byte[] ciphertext) throws SakuliCipherException {
        checkCipherParameters(aesKey, ciphertext);
        final byte[] decrypted;
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(ciphertext);

            final Cipher aesCBC = Cipher.getInstance(CBC_ALGORITHM);
            final IvParameterSpec ivForCBC = readIV(aesCBC.getBlockSize(), bais);
            aesCBC.init(Cipher.DECRYPT_MODE, aesKey, ivForCBC);

            final byte[] buf = new byte[1_024];
            try (final CipherInputStream cis = new CipherInputStream(bais, aesCBC);
                 final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int read;
                while ((read = cis.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                decrypted = baos.toByteArray();
            }
            return decrypted;
        } catch (Exception e) {
            throw new SakuliCipherException(e, "Error during decrypting secret!");
        }
    }

    public static byte[] encryptBytes(SecureRandom rng, SecretKey aesKey, byte[] plaintext) throws SakuliCipherException {
        checkCipherParameters(aesKey, plaintext);
        final byte[] ciphertext;
        try {

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final Cipher aesCBC = Cipher.getInstance(CBC_ALGORITHM);
            final IvParameterSpec ivForCBC = createIV(aesCBC.getBlockSize(), Optional.of(rng));
            aesCBC.init(Cipher.ENCRYPT_MODE, aesKey, ivForCBC);

            baos.write(ivForCBC.getIV());

            try (final CipherOutputStream cos = new CipherOutputStream(baos, aesCBC)) {
                cos.write(plaintext);
            }

            ciphertext = baos.toByteArray();
            return ciphertext;
        } catch (Exception e) {
            throw new SakuliCipherException(e, "Error during encrypting secret!");
        }
    }

    public static String encryptString(String secret, SecretKey aesKey) throws SakuliCipherException {
        final SecureRandom rng = new SecureRandom();
        final byte[] ciphertext = encryptBytes(rng, aesKey, convertStringToBytes(secret));
        return new BASE64Encoder().encode(ciphertext);

    }

    public static String decryptString(String encryptedStringBase64, SecretKey aesKey) throws SakuliCipherException {
        if (StringUtils.isEmpty(encryptedStringBase64)) {
            throw new SakuliCipherException("Empty secret can not en-/decrypted!");
        }
        final byte[] ciphertext;
        try {
            ciphertext = new BASE64Decoder().decodeBuffer(encryptedStringBase64);
        } catch (IOException e) {
            throw new SakuliCipherException("Can not decrypt invalid Base64 secret: " + encryptedStringBase64);
        }

        final byte[] decrypted = decryptBytes(aesKey, ciphertext);
        return convertBytesToString(decrypted);
    }

    private static void checkCipherParameters(SecretKey aesKey, byte[] plaintext) throws SakuliCipherException {
        if (aesKey == null || aesKey.getEncoded() == null || aesKey.getEncoded().length == 0) {
            throw new SakuliCipherException("Provided AES key is null or empty");
        }
        if (plaintext == null || plaintext.length == 0) {
            throw new SakuliCipherException("Empty secret can not en-/decrypted!");
        }
    }

    /**
     * Converts a String input to a byte array
     */
    static byte[] convertStringToBytes(String s) {
        if (s == null) {
            return null;
        }
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts a byte array input to a string
     */
    static String convertBytesToString(byte[] b) {
        if (b == null) {
            return null;
        }
        return new String(b, StandardCharsets.UTF_8);
    }

}