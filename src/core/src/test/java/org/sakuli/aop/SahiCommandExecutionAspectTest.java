/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

package org.sakuli.aop;

import net.sf.sahi.util.Utils;
import org.apache.commons.exec.CommandLine;
import org.sakuli.datamodel.actions.LogLevel;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link SahiCommandExecutionAspect}
 * @author tschneck
 *         Date: 2/25/16
 */
public class SahiCommandExecutionAspectTest extends AopBaseTest {

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        initMocks();
    }

    @Test
    public void testGetCommandTokens() throws Exception {
        String cmd = "\"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\" --incognito " +
                "--user-data-dir=\"C:/Program Files/sakuli/sahi/userdata/browser/chrome/profiles/sahi0\" " +
                "--no-default-browser-check --no-first-run --disable-infobars --proxy-server=localhost:9999";
        String[] parsed = Utils.getCommandTokens(cmd);
        assertLastLine(logFile, "MODIFIED SAHI", LogLevel.INFO, "MODIFIED SAHI COMMAND TOKENS: " +
                "[C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe, --incognito, " +
                "C:/Program Files/sakuli/sahi/userdata/browser/chrome/profiles/sahi0, --no-default-browser-check, " +
                "--no-first-run, --disable-infobars, --proxy-server=localhost:9999]" +
                " => " +
                "[C:/Program Files (x86)/Google/Chrome/Application/chrome.exe, --incognito, " +
                "\"--user-data-dir=C:/Program Files/sakuli/sahi/userdata/browser/chrome/profiles/sahi0\", " +
                "--no-default-browser-check, --no-first-run, --disable-infobars, --proxy-server=localhost:9999]");
        Iterator<String> it = Arrays.asList(parsed).iterator();
        Assert.assertEquals(it.next(), "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
        Assert.assertEquals(it.next(), "--incognito");
        Assert.assertEquals(it.next(), "\"--user-data-dir=C:/Program Files/sakuli/sahi/userdata/browser/chrome/profiles/sahi0\"");
        Assert.assertEquals(it.next(), "--no-default-browser-check");
        Assert.assertEquals(it.next(), "--no-first-run");
        Assert.assertEquals(it.next(), "--disable-infobars");
        Assert.assertEquals(it.next(), "--proxy-server=localhost:9999");
    }

    @Test
    public void testGetCommandTokenNotModifySH() throws Exception {
        String cmd = "sh -c 'ps -ef | grep firefox | grep -v grep'";
        String[] parsed = Utils.getCommandTokens(cmd);
        Iterator<String> it = Arrays.asList(parsed).iterator();
        Assert.assertEquals(it.next(), "sh");
        Assert.assertEquals(it.next(), "-c");
        Assert.assertEquals(it.next(), "ps -ef | grep firefox | grep -v grep");
        //DOUBLE check so the CommanLine.parse really didn't called
        Assert.assertEquals(CommandLine.parse(cmd).getArguments()[1], "\"ps -ef | grep firefox | grep -v grep\"");
    }

    @Test
    public void testCatchSahiCommandExcecutionErrors() throws Exception {
        SakuliExceptionHandler exceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
        try {
            Utils.executeAndGetProcess(new String[]{"testcommand", "arg1", "arg2"});
        } catch (Exception e) {
            //exception have nothing to with the test
        }
        verify(exceptionHandler).handleException(any(SakuliInitException.class));
    }

    @Test
    public void testCatchSahiCommandExcecutionErrorsKeytool() throws Exception {
        SakuliExceptionHandler exceptionHandler = BeanLoader.loadBean(SakuliExceptionHandler.class);
        try {
            Utils.executeAndGetProcess(new String[]{"keytool", "arg1", "arg2"});
        } catch (Exception e) {
            //exception have nothing to with the test
        }
        verify(exceptionHandler, never()).handleException(any(SakuliInitException.class));
    }

    @Test
    public void testLogSahiCommandExection() throws Exception {
        try {
            Utils.executeAndGetProcess(new String[]{"testcommand", "arg1", "arg2"});
        } catch (Exception e) {
            //exception have nothing to with the test
        }
        assertLastLine(logFile, "SAHI", LogLevel.DEBUG, "SAHI command execution: [[testcommand, arg1, arg2]]");
    }

}