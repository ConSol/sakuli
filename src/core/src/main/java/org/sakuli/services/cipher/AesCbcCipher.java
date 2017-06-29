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

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

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
//
//    public static void main(String[] args) throws Exception {
//        final SecureRandom rng = new SecureRandom();
//        // you somehow need to distribute this key
//        //new BASE64Encoder().encode(aesKey.getEncoded())
//        final SecretKey aesKey = createKey("AES", 128, Optional.empty(), Optional.of(rng));
//        final String secret = "owlstead";
//
//        final byte[] plaintext = secret.getBytes(UTF_8);
//        final byte[] ciphertext = encryptBytes(rng, aesKey, plaintext);
//
//        final byte[] decrypted = decryptBytes(aesKey, ciphertext);
//
//        final String result = new String(decrypted, UTF_8);
//        System.out.println(result);
//        assert result.equals(secret);
//    }

    protected static byte[] decryptBytes(SecretKey aesKey, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        //TODO wrapp exception
        final byte[] decrypted;
        {
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
        }
        return decrypted;
    }

    protected static byte[] encryptBytes(SecureRandom rng, SecretKey aesKey, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        //TODO wrapp exception
        final byte[] ciphertext;
        {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final Cipher aesCBC = Cipher.getInstance(CBC_ALGORITHM);
            final IvParameterSpec ivForCBC = createIV(aesCBC.getBlockSize(), Optional.of(rng));
            aesCBC.init(Cipher.ENCRYPT_MODE, aesKey, ivForCBC);

            baos.write(ivForCBC.getIV());

            try (final CipherOutputStream cos = new CipherOutputStream(baos, aesCBC)) {
                cos.write(plaintext);
            }

            ciphertext = baos.toByteArray();
        }
        return ciphertext;
    }

    public String encryptString(String secret, SecretKey aesKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        final SecureRandom rng = new SecureRandom();
        final byte[] plaintext = secret.getBytes(UTF_8);
        final byte[] ciphertext = encryptBytes(rng, aesKey, plaintext);
        return new BASE64Encoder().encode(ciphertext);

    }

    public String decryptString(String encryptedStringBase64, SecretKey aesKey) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        final byte[] ciphertext = new BASE64Decoder().decodeBuffer(encryptedStringBase64);

        final byte[] decrypted = decryptBytes(aesKey, ciphertext);
        final String result = new String(decrypted, UTF_8);
        return result;
    }

}