/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.utils;

import de.consol.sakuli.actions.environment.CipherUtil;
import de.consol.sakuli.exceptions.SakuliCipherException;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.IllegalBlockSizeException;

/**
 * @author tschneck
 *         Date: 06.08.13
 */
public class CipherUtilsTest {

    private CipherUtil testling = new CipherUtil();

    @BeforeMethod
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        testling.setInterfaceName(CipherUtil.determineAValidDefaultNetworkInterface());
        testling.getNetworkInterfaceNames();
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
            testling = new CipherUtil();
            testling.setInterfaceName("etNOVALID");
            testling.getNetworkInterfaceNames();
            Assert.assertTrue(false, "Error, no exception is thrown");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.interfaceLog);
            Assert.assertTrue(e.getMessage().contains(e.interfaceLog));
        }

    }
}
