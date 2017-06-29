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

import org.apache.commons.codec.binary.Hex;
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
                , {""}
                , {"ADKAKADFKADFLAFJ$ß0??!"}
        };
    }

    @Test(dataProvider = "secrets")
    public void testEnAndDecryptWithAESKey(String testSecret) throws Exception {
        final SecretKey aesKey = AesKeyHelper.createRandomKey();

        final AesCbcCipher cipher1 = new AesCbcCipher();
        final String encrypted = cipher1.encryptString(testSecret, aesKey);

        System.out.println("ENCRYPTED:   " + encrypted);
        System.out.println("ENCRYPTED:   " + Hex.encodeHexString(encrypted.getBytes()));

        final AesCbcCipher cipher2 = new AesCbcCipher();
        final String result = cipher2.decryptString(encrypted, aesKey);
        System.out.println("RESULT:   " + result);
        System.out.println("RESULT:   " + Hex.encodeHexString(result.getBytes()));
        System.out.println("EXPECTED: " + testSecret);
        System.out.println("EXPECTED: " + Hex.encodeHexString(testSecret.getBytes()));
        Assert.assertEquals(testSecret.getBytes(), result.getBytes());
        Assert.assertEquals(testSecret, result);
    }

    @Test(dataProvider = "secrets")
    public void testEnAndDecryptWithBase64Key(String testSecret) throws Exception {
        final String aesKey = AesKeyHelper.createRandomBase64Key();
        System.out.println("aes = [" + aesKey + "]");
        final AesCbcCipher cipher1 = new AesCbcCipher();
        final String encrypted = cipher1.encryptString(testSecret, AesKeyHelper.readBase64Keay(aesKey));

        System.out.println("ENCRYPTED:   " + encrypted);
        System.out.println("ENCRYPTED:   " + Hex.encodeHexString(encrypted.getBytes()));

        final AesCbcCipher cipher2 = new AesCbcCipher();
        final String result = cipher2.decryptString(encrypted, AesKeyHelper.readBase64Keay(aesKey));
        System.out.println("RESULT:   " + result);
        System.out.println("RESULT:   " + Hex.encodeHexString(result.getBytes()));
        System.out.println("EXPECTED: " + testSecret);
        System.out.println("EXPECTED: " + Hex.encodeHexString(testSecret.getBytes()));
        Assert.assertEquals(testSecret.getBytes(), result.getBytes());
        Assert.assertEquals(testSecret, result);
    }

    @Test
    public void testFixedKeyAndSecret() throws Exception {
        final String masterkey = "Bsqs/IR1jW+eibNrdYvlAQ==";
        final String encryptedText = "SZUs7Oy0U9QyC5P3QhphzIPJV9txOkppIsiwproRd8g=";
        final AesCbcCipher cipher = new AesCbcCipher();
        Assert.assertEquals(cipher.decryptString(encryptedText, AesKeyHelper.readBase64Keay(masterkey)), "akdfjkald");
    }

    //TODO add error tests
}