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

package org.sakuli.utils;

import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.exceptions.SakuliCipherException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.IllegalBlockSizeException;

/**
 * @author tschneck
 *         Date: 06.08.13
 */
public class CipherUtilsTest {

    private CipherUtil testling;

    @BeforeMethod
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        ActionProperties props = new ActionProperties();
        props.setEncryptionInterfaceAutodetect(true);
        testling = new CipherUtil(props);
        testling.scanNetworkInterfaces();
    }

    @Test
    public void testEncrypt() throws Throwable {
        //check if MAC-Adrress is reachable

        String testSecrect = "testSecrect";
        Assert.assertNotNull(testling.encrypt(testSecrect));

    }

    @Test
    public void testEncryptAndDecrypt() throws Throwable {
        String testSecrect = "testSecrect";
        String encrypted = testling.encrypt(testSecrect);
        Assert.assertEquals(testSecrect, testling.decrypt(encrypted));
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testNotEncryptedException() throws Throwable {
        try {
            testling.decrypt("nonEncrypted");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.interfaceLog);
            Assert.assertTrue(e.getSuppressed()[0] instanceof IllegalBlockSizeException);
            Assert.assertTrue(e.getMessage().contains("Maybe this secret hasn't been encrypted"));
            throw e;
        }
    }

    @Test
    public void testChipherException() throws Throwable {
        try {
            ActionProperties props = new ActionProperties();
            props.setEncryptionInterfaceAutodetect(false);
            props.setEncryptionInterface("etNOVALID");
            testling = new CipherUtil(props);
            testling.scanNetworkInterfaces();
            Assert.assertTrue(false, "Error, no exception is thrown");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.interfaceLog);
            Assert.assertTrue(e.getMessage().contains(e.interfaceLog));
        }

    }

    @Test
    public void testConvertToByteArray() throws Exception {
        byte[] target =
                {
                        0x63, 0x6f, 0x6e, 0x31, 0x33, 0x53, 0x61, 0x6b, 0x53, 0x6f
                };
        Assert.assertEquals(CipherUtil.convertStringToBytes("con13SakSo"), target);
    }
}
