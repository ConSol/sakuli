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

package org.sakuli.starter.helper;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sakuli.datamodel.properties.SahiProxyProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static org.mockito.Mockito.when;
import static org.sakuli.BaseTest.assertContains;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SahiProxyTest {
    @Mock
    private SahiProxyProperties props;
    @InjectMocks
    private SahiProxy testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInjectCustomJavaScriptFiles() throws Exception {
        Path configOrig = Paths.get(this.getClass().getResource("mock-files/inject_top.txt").toURI());
        Path config = Paths.get(configOrig.getParent().toString() + File.separator + "inject_top_temp.txt");
        Path target = Paths.get(configOrig.getParent().toString() + File.separator + "inject.js");
        Path source = Paths.get(SahiProxy.class.getResource("inject_source.js").toURI());
        FileUtils.copyFile(configOrig.toFile(), config.toFile());

        when(props.getSahiJSInjectConfigFile()).thenReturn(config);
        when(props.getSahiJSInjectTargetFile()).thenReturn(target);
        when(props.getSahiJSInjectSourceFile()).thenReturn(source);
        assertFalse(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));

        testling.injectCustomJavaScriptFiles();
        assertTrue(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertContains(FileUtils.readFileToString(target.toFile()),
                "Sahi.prototype.originalEx = Sahi.prototype.ex;");
        assertContains(FileUtils.readFileToString(target.toFile()),
                "Sahi.prototype.ex = function (isStep) {");
    }

    @Test
    public void testInjectCustomJavaScriptFilesOverridingNewer() throws Exception {
        Path configOrig = Paths.get(this.getClass().getResource("mock-files/inject_top.txt").toURI());
        Path config = Paths.get(configOrig.getParent().toString() + File.separator + "inject_top_temp.txt");
        Path targetOrig = Paths.get(this.getClass().getResource("mock-old-js/inject.js").toURI());
        Path target = Paths.get(targetOrig.getParent().toString() + File.separator + "inject_temp.js");
        FileUtils.copyFile(configOrig.toFile(), config.toFile());
        FileUtils.copyFile(targetOrig.toFile(), target.toFile());

        Files.setLastModifiedTime(target, FileTime.fromMillis(DateTime.now().minusYears(10).getMillis()));
        Path source = Paths.get(SahiProxy.class.getResource("inject_source.js").toURI());

        when(props.getSahiJSInjectConfigFile()).thenReturn(config);
        when(props.getSahiJSInjectTargetFile()).thenReturn(target);
        when(props.getSahiJSInjectSourceFile()).thenReturn(source);
        assertFalse(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertContains(FileUtils.readFileToString(target.toFile()), "//old inject.js");

        testling.injectCustomJavaScriptFiles();
        assertTrue(FileUtils.readFileToString(config.toFile()).contains(SahiProxy.SAKULI_INJECT_SCRIPT_TAG));
        assertContains(FileUtils.readFileToString(target.toFile()),
                "Sahi.prototype.originalEx = Sahi.prototype.ex;");
        assertContains(FileUtils.readFileToString(target.toFile()),
                "Sahi.prototype.ex = function (isStep) {");

    }
}