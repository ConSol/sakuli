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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.properties.CipherProperties;
import org.sakuli.datamodel.properties.ForwarderProperties;
import org.sakuli.loader.BeanLoader;
import org.sakuli.services.ResultService;
import org.sakuli.services.cipher.CipherService;
import org.sakuli.services.cipher.EnvironmentCipher;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class SpringProfilesInitializerTest {

    @Mock
    private ForwarderProperties forwarderProperties;
    @Mock
    private CipherProperties cipherProperties;
    @InjectMocks
    private SpringProfilesInitializer testling;

    @DataProvider(name = "profileNames")
    public static Object[][] profileNames() {
        return new Object[][]{
                {false, false, new String[0]},
                {true, false, new String[]{"JDBC_DB"}},
                {false, true, new String[]{"GEARMAN"}},
                {true, true, new String[]{"JDBC_DB", "GEARMAN"}},
        };
    }

    @DataProvider(name = "profileProperties")
    public static Object[][] profileProperties() {
        return new Object[][]{
                {"no-persistcence-services", 0},
                {"all-persistcence-services", 2}
        };
    }

    @Test(dataProvider = "profileNames")
    public void testGetConfiguredProfiles(boolean database, boolean gearman, String[] expectedStrings) throws Exception {
        MockitoAnnotations.initMocks(this);
        when(forwarderProperties.isDatabaseEnabled()).thenReturn(database);
        when(forwarderProperties.isGearmanEnabled()).thenReturn(gearman);
        assertEquals(expectedStrings, testling.getConfiguredProfiles());
    }

    @Test(dataProvider = "profileProperties")
    public void testAfterPropertiesSet(String propertyFile, int countOfResultServices) throws Exception {
        SakuliPropertyPlaceholderConfigurer.SAKULI_HOME_FOLDER_VALUE = BaseTest.SAKULI_HOME_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_HOME_VALUE = BaseTest.SAHI_FOLDER_PATH;
        BeanLoader.CONTEXT_PATH = "loaderTest-beanRefFactory.xml";
        Path testProps = Paths.get(getClass().getResource(propertyFile).toURI());
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = testProps.toString();

        BeanLoader.refreshContext();
        Map<String, ResultService> resultServiceMap = BeanLoader.loadMultipleBeans(ResultService.class);
        assertEquals(resultServiceMap.size(), countOfResultServices);

        final Map<String, CipherService> resultCipher = BeanLoader.loadMultipleBeans(CipherService.class);
        assertEquals(resultCipher.size(), 1);
        //default cipher
        assertEquals(resultCipher.values().iterator().next().getClass(), EnvironmentCipher.class);

    }

    @Test
    public void testCipherEnv() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(cipherProperties.isEncryptionModeEnv()).thenReturn(true);
        when(cipherProperties.isEncryptionModeInterface()).thenReturn(true);
        assertEquals(new String[]{SpringProfilesInitializer.CIPHER_ENV}, testling.getConfiguredProfiles());
    }

    @Test
    public void testCipherInterface() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(cipherProperties.isEncryptionModeEnv()).thenReturn(false);
        when(cipherProperties.isEncryptionModeInterface()).thenReturn(true);
        assertEquals(new String[]{SpringProfilesInitializer.CIPHER_INTERFACE}, testling.getConfiguredProfiles());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        BeanLoader.CONTEXT_PATH = BaseTest.TEST_CONTEXT_PATH;
    }
}