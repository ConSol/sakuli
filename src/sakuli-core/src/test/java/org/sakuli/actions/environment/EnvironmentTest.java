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

package org.sakuli.actions.environment;

import org.apache.commons.lang.StringUtils;
import org.sakuli.BaseTest;
import org.sakuli.exceptions.SakuliException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author tschneck
 *         Date: 06.07.15
 */
public class EnvironmentTest extends BaseTest {


    private Environment env;

    @BeforeMethod
    public void setUp() throws Exception {
        env = new Environment();

    }

    @Test
    public void testIsWindows() throws Exception {
        boolean windows = Arrays.asList("xp", "nt").contains(env.getOsIdentifier());
        assertEquals(env.isWindows(), windows);
    }

    @Test
    public void testIsLinux() throws Exception {
        boolean linux = env.getOsIdentifier().equals("linux");
        assertEquals(env.isLinux(), linux);
    }

    @Test
    public void testRunCmd() throws Exception {
        CommandLineUtil.CommandLineResult result = Environment.runCommand("hostname");
        assertTrue(StringUtils.isNotBlank(result.getOutput()));
        assertEquals(result.getExitCode(), 0);
    }

    @Test
    public void testRunCmdNoError() throws Exception {
        CommandLineUtil.CommandLineResult result = Environment.runCommand("host?name", false);
        assertTrue(StringUtils.isNotBlank(result.getOutput()));
        assertEquals(result.getExitCode(), 2);
    }

    @Test(expectedExceptions = SakuliException.class)
    public void testRunCmdException() throws Exception {
        Environment.runCommand("host?name");
    }

}