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

package org.sakuli.services.cipher;

import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.exceptions.SakuliCipherException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author tschneck
 *         Date: 06.08.13
 */
public class NetworkInterfaceCipherUtilsTest {

    private NetworkInterfaceCipher testling;
    private CipherProperties props;

    @DataProvider(name = "secrets")
    public static Object[][] secrects() {
        return new Object[][]{
                {"testSecrect"}
                , {"t"}
                , {"akdföadjfaödfjkadfjaödfjaö"}
                , {"ADKAKADFKADFLAFJ$ß0??!"}
        };
    }

    @BeforeMethod
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        props = new CipherProperties();
        props.setEncryptionInterfaceAutodetect(true);
        testling = new NetworkInterfaceCipher(props);
        testling.scanNetworkInterfaces();
    }

    @Test(dataProvider = "secrects")
    public void testEncrypt(String testSecrect) throws Throwable {
        //check if MAC-Adrress is reachable
        Assert.assertNotNull(testling.encrypt(testSecrect));
    }

    @Test(dataProvider = "secrects")
    public void testEncryptAndDecrypt(String testSecrect) throws Throwable {
        String encrypted = testling.encrypt(testSecrect);
        final NetworkInterfaceCipher cipher2 = new NetworkInterfaceCipher(props);
        cipher2.scanNetworkInterfaces();
        final String result = cipher2.decrypt(encrypted);
        System.out.println("RESULT:   " + result);
        System.out.println("EXPECTED: " + testSecrect);
        Assert.assertEquals(testSecrect.getBytes(), result.getBytes());
        Assert.assertEquals(testSecrect, result);
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testNotEncryptedException() throws Throwable {
        try {
            testling.decrypt("nonEncrypted");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertTrue(e.getCause().getCause() instanceof IOException);
            Assert.assertTrue(e.getMessage().contains("Error during decryption of secret 'nonEncrypted'"));
            throw e;
        }
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testEmptyTestException() throws Throwable {
        try {
            testling.decrypt("");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertTrue(e.getCause() instanceof SakuliCipherException);
            Assert.assertTrue(e.getMessage().contains("Empty secret can not en-/decrypted!"));
            throw e;
        }
    }

    @Test
    public void testChipherException() throws Throwable {
        try {
            CipherProperties props = new CipherProperties();
            props.setEncryptionInterfaceAutodetect(false);
            props.setEncryptionInterface("etNOVALID");
            testling = new NetworkInterfaceCipher(props);
            testling.scanNetworkInterfaces();
            Assert.assertTrue(false, "Error, no exception is thrown");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertTrue(e.getMessage().contains(e.cipherLog));
        }

    }

}
