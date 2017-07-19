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
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;

/**
 * @author tschneck
 *         Date: 6/29/17
 */
public class AesCbcCipherTest {

    @DataProvider(name = "secrets")
    public static Object[][] secrets() {
        return new Object[][]{
                {"testSecrect"}
                , {"t"}
                , {"akdfjkald"}
                , {"akdföadjfaödfjkadfjaödfjaö"}
                , {"ADKAKADFKADFLAFJ$ß0??!"}
        };
    }

    @Test(dataProvider = "secrets")
    public void testEnAndDecryptWithAESKey(String testSecret) throws Exception {
        final SecretKey aesKey = AesKeyHelper.createRandomKey();
        final String encrypted = AesCbcCipher.encryptString(testSecret, aesKey);
        System.out.println("ENCRYPTED:   " + encrypted);

        final String result = AesCbcCipher.decryptString(encrypted, aesKey);
        System.out.println("RESULT:   " + result);
        System.out.println("EXPECTED: " + testSecret);
        Assert.assertEquals(testSecret.getBytes(), result.getBytes());
        Assert.assertEquals(testSecret, result);
    }

    @Test(dataProvider = "secrets")
    public void testEnAndDecryptWithBase64Key(String testSecret) throws Exception {
        final String aesKey = AesKeyHelper.createRandomBase64Key();
        final String encrypted = AesCbcCipher.encryptString(testSecret, AesKeyHelper.readBase64Keay(aesKey));
        System.out.println("ENCRYPTED:   " + encrypted);

        final String result = AesCbcCipher.decryptString(encrypted, AesKeyHelper.readBase64Keay(aesKey));
        System.out.println("RESULT:   " + result);
        System.out.println("EXPECTED: " + testSecret);
        Assert.assertEquals(testSecret.getBytes(), result.getBytes());
        Assert.assertEquals(testSecret, result);
    }

    @Test
    public void testFixedKeyAndSecret() throws Exception {
        final String masterkey = "Bsqs/IR1jW+eibNrdYvlAQ==";
        final String encryptedText = "SZUs7Oy0U9QyC5P3QhphzIPJV9txOkppIsiwproRd8g=";
        Assert.assertEquals(AesCbcCipher.decryptString(encryptedText, AesKeyHelper.readBase64Keay(masterkey)), "akdfjkald");
    }


    @Test
    public void testConvertToByteArray() throws Exception {
        byte[] target = {
                0x63, 0x6f, 0x6e, 0x31, 0x33, 0x53, 0x61, 0x6b, 0x53, 0x6f
        };
        Assert.assertEquals(AesCbcCipher.convertStringToBytes("con13SakSo"), target);
        Assert.assertEquals(AesCbcCipher.convertBytesToString(target), "con13SakSo");
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Provided AES key is null or empty")
    public void testKeyNull() throws Exception {
        AesCbcCipher.encryptString("bkkala", null);
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Empty secret can not en-/decrypted!")
    public void testSecretEmpty() throws Exception {
        AesCbcCipher.encryptString("", AesKeyHelper.createRandomKey());
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Empty secret can not en-/decrypted!")
    public void testSecretNull() throws Exception {
        AesCbcCipher.encryptString(null, AesKeyHelper.createRandomKey());
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Provided AES key is null or empty")
    public void testKeyNullDecrypt() throws Exception {
        AesCbcCipher.decryptString("bkkala", null);
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Empty secret can not en-/decrypted!")
    public void testSecretEmptyDecrypt() throws Exception {
        AesCbcCipher.decryptString("", AesKeyHelper.createRandomKey());
    }

    @Test(expectedExceptions = SakuliCipherException.class,
            expectedExceptionsMessageRegExp = "Empty secret can not en-/decrypted!")
    public void testSecretNullDecrypt() throws Exception {
        AesCbcCipher.decryptString(null, AesKeyHelper.createRandomKey());
    }

    //TODO add error tests
}