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

package de.consol.sakuli;

import de.consol.sakuli.loader.BeanLoader;
import de.consol.sakuli.utils.SakuliProperties;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


/**
 * run tests with mvn
 *
 * @author tschneck
 *         Date: 10.06.13
 */

public class SakuliPropertyPlacehodlerConfigurerTest extends BaseTest {

    @Test
    public void testTestSuiteFolder() throws IOException {
        PropertyHolder properties = BeanLoader.loadBean(PropertyHolder.class);
        Path tsFolder = Paths.get(properties.getTestSuiteFolder());

        assertTrue(Files.exists(tsFolder), "test suite folder doesn't exists or have not been set correctly");
        System.out.println(tsFolder.toFile().getAbsolutePath());
        assertTrue(tsFolder.toFile().getAbsolutePath().contains(TEST_FOLDER_PATH));
    }

    @Test
    public void testIncludeFolder() throws IOException {
        SakuliProperties properties = BeanLoader.loadBean(SakuliProperties.class);

        assertTrue(Files.exists(properties.getIncludeFolder()), "include folder doesn't exists");
        assertTrue(properties.getIncludeFolder().toString().contains(INCLUDE_FOLDER_PATH));

        assertNotNull(properties.getTestSuiteId());
    }


}