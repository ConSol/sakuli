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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.sakuli.datamodel.properties.ActionProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class ScreenshotActionsTest {

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

    @BeforeMethod
    public void setUp() throws Exception {
        props = mock(ActionProperties.class);
        when(props.getScreenShotFormat()).thenReturn("png");
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
        Path result2 = testling.takeScreenshotWithTimestamp("screenshot-timestamp", rootpath, "png");
        assertRegExMatch(result2.toString(),
                ".*org.sakuli.actions.screenbased." + getDayPrefix() + ".*screenshot_timestamp.png");
        assertTrue(Files.exists(result2));
        assertNotEquals(result.toString(), result2.toString());
    }

    @Test
    public void testCreatePictureFromBufferedImageTimestampJPG() throws Exception {
        Path jpgResult = testling.takeScreenshotWithTimestamp("screenshot-jpg", rootpath, "jpg");
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

}