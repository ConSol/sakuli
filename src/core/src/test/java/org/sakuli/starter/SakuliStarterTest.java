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

package org.sakuli.starter;

import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class SakuliStarterTest {

    @AfterMethod
    @BeforeMethod
    public void setUp() throws Exception {
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE = null;
        SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE = null;
    }

    @Test(expectedExceptions = SakuliInitException.class,
            expectedExceptionsMessageRegExp = ".*Masterkey and network interface specified. please specify only one option.*")
    public void testDoubledEncryptionMode() throws Exception {
        SakuliStarter.checkAndAssignEncryptionOptions("keyvalue", "eth1");
    }

    @Test
    public void testCheckMasterkey() throws Exception {
        SakuliStarter.checkAndAssignEncryptionOptions("keyvalue", "");
        assertNull(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE);
        assertEquals(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE, "keyvalue");
        SakuliStarter.checkAndAssignEncryptionOptions("keyvalue2", null);
        assertNull(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE);
        assertEquals(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE, "keyvalue2");
    }

    @Test
    public void testCheckInterface() throws Exception {
        SakuliStarter.checkAndAssignEncryptionOptions("", "eth1");
        assertNull(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE);
        assertEquals(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE, "eth1");
        SakuliStarter.checkAndAssignEncryptionOptions(null, "eth2");
        assertNull(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_KEY_VALUE);
        assertEquals(SakuliPropertyPlaceholderConfigurer.ENCRYPTION_INTERFACE_VALUE, "eth2");
    }
}