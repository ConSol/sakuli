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

package org.sakuli.example;

import net.sf.sahi.util.OSUtils;
import org.sakuli.actions.environment.Application;
import org.sakuli.actions.screenbased.Region;
import org.sakuli.javaDSL.AbstractSakuliTest;
import org.sakuli.javaDSL.TestCaseInitParameter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Tobias Schneck
 */
public class Example extends AbstractSakuliTest {

    @Override
    protected TestCaseInitParameter getTestCaseInitParameter() throws Exception {
        return new TestCaseInitParameter(
                "my-example-test", "test1")
                .withWarningTime(5)
                .withCriticalTime(10);
    }

    @Test
    public void example1GUI() throws Exception {
        new Region().highlight();

    }

    @Test
    public void exampleSahi() throws Exception {
        browser.navigateTo("http://sahi.example.com/_s_/dyn/Driver_initialized");
        Arrays.asList("SSL Manager", "Logs", "Online Documentation", "Test Pages", "Sample Application")
                .stream().forEach(this::highlightLink);
        new Region().find("sahi_logo").highlight();
    }

    @Test
    public void testEditor() throws Exception {
        switch (OSUtils.identifyOS()) {
            case "xp":
                callEditor("notepad.exe");
                break;
            case "linux":
                callEditor("gedit");
                break;
        }
    }

    private void callEditor(String executable) {
        Application editor = new Application(executable).open();

        Assert.assertTrue(editor.getPID() > 0, "PID " + editor.getPID() + " <= 0");
        editor.getRegion().type("TEST it!");
        editor.closeApp();
    }

    private void highlightLink(String identifier) {
        browser.highlight(browser.link(identifier));
    }
}
