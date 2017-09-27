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

package org.sakuli.starter.helper;

import org.sakuli.BaseTest;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.services.cipher.AesKeyHelper;
import org.sakuli.services.cipher.EnvironmentCipher;
import org.sakuli.services.cipher.NetworkInterfaceCipher;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 7/1/17
 */
public class CipherDelegatorTest {

    @AfterMethod
    @BeforeMethod
    public void setUpEncryptionVals() throws Exception {
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = null;
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = null;
    }

    @Test
    public void testEncryptKey() throws Exception {
        String testSecret = "my-test-val";
        final String masterkey = AesKeyHelper.createRandomBase64Key();
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = masterkey;
        final Map.Entry<String, String> kv = CipherDelegator.encrypt(testSecret);
        assertEquals(kv.getKey(), "environment masterkey");
        assertTrue(kv.getValue().length() > testSecret.length());

        CipherProperties props = new CipherProperties();
        props.setEncryptionKey(masterkey);
        assertEquals(new EnvironmentCipher(props).decrypt(kv.getValue()), testSecret);
    }

    @Test
    public void testEncryptInterface() throws Exception {
        String testSecret = "my-test-val";
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = "auto";
        final Map.Entry<String, String> kv = CipherDelegator.encrypt(testSecret);
        BaseTest.assertContains(kv.getKey(), "interface ");
        assertTrue(kv.getValue().length() > testSecret.length());

        CipherProperties props = new CipherProperties();
        props.setEncryptionInterfaceAutodetect(true);
        final NetworkInterfaceCipher networkInterfaceCipher = new NetworkInterfaceCipher(props);
        networkInterfaceCipher.scanNetworkInterfaces();
        assertEquals(networkInterfaceCipher.decrypt(kv.getValue()), testSecret);
    }

    @Test
    public void testEnvBasedKeyLoading() throws Exception {
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = "test-key";
        CipherDelegator.loadEnvironmentVariables();
        assertEquals(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE, "test-key");
    }

}