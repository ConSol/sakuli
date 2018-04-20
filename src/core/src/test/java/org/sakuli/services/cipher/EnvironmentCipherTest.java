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

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.exceptions.SakuliCipherException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.sakuli.BaseTest.assertContains;

/**
 * @author tschneck
 * Date: 6/28/17
 */
public class EnvironmentCipherTest {
    private EnvironmentCipher testling;
    @Mock
    private CipherProperties props;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        testling = new EnvironmentCipher(props);
        when(props.getEncryptionKey()).thenReturn(AesKeyHelper.createRandomBase64Key());
    }

    /**
     * More already tested in {@link AesCbcCipherTest}
     */
    @Test
    public void testFixedKeyAndSecret() throws Exception {
        final String masterkey = "Bsqs/IR1jW+eibNrdYvlAQ==";
        final String encryptedText = "QCO15+8FL2xq5TEoOClVa3ZbfhC+kCyLI1Xc9ofE5So=";
        when(props.getEncryptionKey()).thenReturn(masterkey);
        Assert.assertEquals(testling.decrypt(encryptedText), "akdfjkald");
    }

    @Test
    public void testEncryptFixedKey() throws Exception {
        final String masterkey = "Bsqs/IR1jW+eibNrdYvlAQ==";
        when(props.getEncryptionKey()).thenReturn(masterkey);
        final String encryptedText = testling.encrypt("akdfjkald");
        Assert.assertEquals(testling.decrypt(encryptedText), "akdfjkald");
    }

    @Test
    public void testEncryptRandomKey() throws Exception {
        final String masterkey = AesKeyHelper.createRandomBase64Key();
        when(props.getEncryptionKey()).thenReturn(masterkey);
        final String encryptedText = testling.encrypt("@my-$ecret-!");
        Assert.assertEquals(testling.decrypt(encryptedText), "@my-$ecret-!");
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testNotEncryptedException() throws Exception {
        try {
            testling.decrypt("nonEncrypted");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertEquals(e.getCause().getCause().getClass(), IOException.class);
            assertContains(e.getMessage(), "Error during decryption of secret 'nonEncrypted'");
            throw e;
        }
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testEmptyTestException() throws Exception {
        try {
            testling.decrypt("");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertEquals(e.getCause().getClass(), SakuliCipherException.class);
            assertContains(e.getMessage(), "Empty secret can not en-/decrypted!");
            throw e;
        }
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testNullKey() throws Exception {
        when(props.getEncryptionKey()).thenReturn(null);
        try {
            testling.decrypt("irrelevant");
        } catch (SakuliCipherException e) {
            Assert.assertNotNull(e.cipherLog);
            Assert.assertEquals(e.getCause().getClass(), SakuliCipherException.class);
            assertContains(e.getMessage(), "Please specify an masterkey");
            throw e;
        }
    }

    @Test(expectedExceptions = SakuliCipherException.class)
    public void testEmptyKey() throws Exception {
        when(props.getEncryptionKey()).thenReturn("");
        try {
            testling.decrypt("irrelevant");
        } catch (SakuliCipherException e) {
            assertContains(e.getMessage(), "Please specify an masterkey");
            throw e;
        }
    }

}