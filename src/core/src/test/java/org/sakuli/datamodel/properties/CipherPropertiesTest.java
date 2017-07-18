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

package org.sakuli.datamodel.properties;

import org.sakuli.exceptions.SakuliInitException;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author tschneck
 *         Date: 7/3/17
 */
public class CipherPropertiesTest {
    @Test(expectedExceptions = SakuliInitException.class,
    expectedExceptionsMessageRegExp = "Value of property 'sakuli.encryption.mode=not-valid-mode' is not valid! Accepted values are: \\[environment, interface\\]")
    public void testValidatePropertyValues() throws Exception {
        Properties props = new Properties();
        props.setProperty(CipherProperties.ENCRYPTION_MODE,"not-valid-mode");
        CipherProperties.load(props).validatePropertyValues();
    }

}