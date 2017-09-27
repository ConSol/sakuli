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

package org.sakuli.actions.screenbased;

import org.apache.commons.lang.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.BaseTest;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.loader.BaseActionLoader;
import org.sikuli.basics.Settings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.sakuli.AbstractLogAwareTest.getResource;
import static org.sakuli.BaseTest.assertRegExMatch;
import static org.testng.Assert.*;

@SuppressWarnings("ConstantConditions")
public class ScreenshotActionsTest {
    @Mock
    private BaseActionLoader loader;

    private ActionProperties props;
    @Spy
    @InjectMocks
    private ScreenshotActions testling;
    private Path rootpath = Paths.get(
            getResource(".", ScreenshotActionsTest.class))
            .toAbsolutePath().normalize();

    private static String getDayPrefix() {
        return new SimpleDateFormat("yyyy_MM_dd").format(new Date());
    }


    @Test
    public void testResolveTakeScreenshotFolder() throws Exception {
        TestCase tc = mock(TestCase.class);
        Path examplePath = Paths.get(BaseTest.TEST_FOLDER_PATH + File.separator + "case" + File.separator + "tc.js");
        when(tc.getTcFile()).thenReturn(examplePath);
        when(loader.getCurrentTestCase()).thenReturn(tc);
        Path result = testling.resolveTakeScreenshotPath("screenshot.png");
        assertEquals(result.toString(), BaseTest.TEST_FOLDER_PATH + File.separator + "case" + File.separator + "screenshot.png");
        verify(loader, never()).getTestSuite();
        verify(loader).getCurrentTestCase();
    }

    @Test
    public void testResolveTakeScreenshotFolderFallback() throws Exception {
        TestCase tc = mock(TestCase.class);
        Path examplePath = Paths.get(BaseTest.TEST_FOLDER_PATH + File.separator + "NO_FOLDER" + File.separator + "tc.js");
        when(tc.getTcFile()).thenReturn(examplePath);
        when(loader.getCurrentTestCase()).thenReturn(tc);
        TestSuite ts = mock(TestSuite.class);
        when(ts.getTestSuiteFolder()).thenReturn(Paths.get(BaseTest.TEST_FOLDER_PATH));
        when(loader.getTestSuite()).thenReturn(ts);

        Path result = testling.resolveTakeScreenshotPath("test");
        assertEquals(result.toString(), Paths.get(BaseTest.TEST_FOLDER_PATH) + File.separator + "test");
        verify(loader).getTestSuite();
        verify(loader).getCurrentTestCase();
    }

    @Test
    public void testResolveTakeScreenshotFolderAbsolutPath() throws Exception {
        String filename = Settings.isWindows() ? "C:\\test" : "/home/test";
        Path result = testling.resolveTakeScreenshotPath(filename);
        assertEquals(result.toString(), filename);
        verify(loader, never()).getTestSuite();
        verify(loader, never()).getCurrentTestCase();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        props = mock(ActionProperties.class);
        when(props.getScreenshotformat()).thenReturn("png");
        MockitoAnnotations.initMocks(this);
        BufferedImage bufferedImage = ImageIO.read(Paths.get(getResource("buffered-image.png", this.getClass())).toFile());
        doReturn(bufferedImage).when(testling).createBufferedImage(any());
    }

    @Test
    public void testCreatePictureFromBufferedImage() throws Exception {
        assertRegExMatch(testling.takeScreenshot(rootpath.resolve("test-without-format")).toString(),
                ".*org.sakuli.actions.screenbased.test-without-format.png");
        assertTrue(Files.exists(Paths.get(getResource("test-without-format.png", this.getClass()))));
        //create two times to test overwrite won't create a exception
        assertRegExMatch(testling.takeScreenshot(rootpath.resolve("test-without-format")).toString(),
                ".*org.sakuli.actions.screenbased.test-without-format.png");
    }

    @Test
    public void testCreatePictureFromBufferedImageWithFormat() throws Exception {
        for (String format : Arrays.asList("png", "jpg")) {
            assertRegExMatch(testling.takeScreenshot(rootpath.resolve("test-with-format." + format)).toString(),
                    ".*org.sakuli.actions.screenbased.test-with-format." + format);
            assertTrue(Files.exists(Paths.get(getResource("test-with-format." + format, this.getClass()))));
        }
    }

    @Test
    public void testCreatePictureFromBufferedImageTimestamp() throws Exception {
        Path result = testling.takeScreenshotWithTimestamp("screenshot-timestamp", rootpath);
        assertRegExMatch(result.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_timestamp.png");
        assertTrue(Files.exists(result));

        //try to create a second
        Path result2 = testling.takeScreenshotWithTimestamp("screenshot-timestamp", rootpath, "png", null);
        assertRegExMatch(result2.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_timestamp.png");
        assertTrue(Files.exists(result2));
        assertNotEquals(result.toString(), result2.toString());
    }

    @Test
    public void testCreatePictureFromBufferedImageTimestampJPG() throws Exception {
        Path jpgResult = testling.takeScreenshotWithTimestamp("screenshot-jpg", rootpath, "jpg", null);
        assertRegExMatch(jpgResult.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_jpg.jpg");
        assertTrue(Files.exists(jpgResult));
    }

    @Test
    public void testCreatePictureFromBufferedImageTimestampNoSpecialChars() throws Exception {
        assertRegExMatch(testling.takeScreenshotWithTimestamp("screen>shot$$$<html", rootpath).toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screen_shot_html.png");
    }

    @Test
    public void testCreatePictureFromBufferedImageTimestampTooLong() throws Exception {
        String message = "screenshoot" + StringUtils.repeat("x", 400);
        assertRegExMatch(testling.takeScreenshotWithTimestamp(message, rootpath).toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*x{10,20}.png");
    }


    @Test
    public void testTakeScreenshotWithTimestamp() throws Exception {
        Path result = testling.takeScreenshotWithTimestamp("screenshot-timestamp", rootpath.toString(), null, null);
        assertRegExMatch(result.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_timestamp.png");
        assertTrue(Files.exists(result));

        //try to create a second
        Path result2 = testling.takeScreenshotWithTimestamp("screenshot-timestamp", rootpath.toString(), "png", null);
        assertRegExMatch(result2.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_timestamp.png");
        assertTrue(Files.exists(result2));
        assertNotEquals(result.toString(), result2.toString());
        verify(loader, never()).getExceptionHandler();
    }

    @Test
    public void testTakeScreenshotWithTimestampEmptyDestinationSuite() throws Exception {
        TestSuite ts = mock(TestSuite.class);
        when(ts.getTestSuiteFolder()).thenReturn(rootpath);
        when(loader.getTestSuite()).thenReturn(ts);

        Path result = testling.takeScreenshotWithTimestamp("screenshot-suite", "", null, null);
        assertRegExMatch(result.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_suite.png");
        assertTrue(Files.exists(result));

        //try for testcase
        Path tcRoot = Files.createDirectories(rootpath.resolve("testcase_tmp"));
        TestCase tc = mock(TestCase.class);
        when(tc.getTcFile()).thenReturn(tcRoot.resolve("tc.js"));
        when(loader.getCurrentTestCase()).thenReturn(tc);

        Path result2 = testling.takeScreenshotWithTimestamp("screenshot-tc", "", null, null);
        assertRegExMatch(result2.toString(),
                ".*org.sakuli.actions.screenbased.testcase_tmp." + getDayPrefix() + ".*screenshot_tc.png");
        assertTrue(Files.exists(result2));
        assertNotEquals(result.toString(), result2.toString());
        verify(loader, never()).getExceptionHandler();
    }
}