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

package org.sakuli.actions.screenbased;

import org.apache.commons.lang.StringUtils;
import org.sakuli.datamodel.TestCase;
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.Screen;
import org.sakuli.datamodel.properties.ActionProperties;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.loader.BaseActionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;


@Component
public class ScreenshotActions {
    private static final int MAX_FILENAME_LENGTH = 50;
    protected static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotActions.class);
    private final static List<String> allowedScreenshotFormats = Collections.unmodifiableList(Arrays.asList("jpg", "png"));
    private BaseActionLoader baseActionLoader;
    private String defaultScreenshotFormat;
    private Screen screen;

    @Autowired
    public ScreenshotActions(Screen screen, ActionProperties props, @Qualifier("baseLoader") BaseActionLoader baseActionLoader) {
        this.screen = screen;
        this.defaultScreenshotFormat = props.getScreenshotformat();
        this.baseActionLoader = baseActionLoader;
    }

    /**
     * Transfers a {@link BufferedImage} to an picture and saves it
     *
     * @param message    Prefixed filename of the picture (timestamp will be added)
     * @param folderPath Folder of the place, where the picture should be stored
     * @return {@link Path} ot the screenshot.
     */
    static Path createPictureFromBufferedImage(String message, Path folderPath, String screenShotFormat, BufferedImage bufferedImage) throws IOException {
        LOGGER.info("create screen shot for error \"" + message + "\"");
        //generate a valid file name
        String fileName = TestSuite.GUID_DATE_FORMATE.format(new Date())
                + "_"
                + replaceSpecialCharacters(message)
                + "." + screenShotFormat;
        if (fileName.length() > MAX_FILENAME_LENGTH) {
            fileName = fileName.substring(0, MAX_FILENAME_LENGTH) + "." + screenShotFormat;
        }

        //create file
        Path pictureFile = folderPath.resolve(fileName);
        return createPictureFromBufferedImage(pictureFile, screenShotFormat, bufferedImage);
    }

    /**
     * Transfers a {@link BufferedImage} to an picture and saves it
     *
     * @param pictureFile Path to the final image
     * @return {@link Path} ot the screenshot.
     */
    static Path createPictureFromBufferedImage(Path pictureFile, String screenshotFormat, BufferedImage bufferedImage) throws IOException {
        Path absPath = pictureFile.normalize().toAbsolutePath();

        //checked allowed format
        if (!allowedScreenshotFormats.contains(screenshotFormat)) {
            throw new IOException("Format '" + screenshotFormat + "' is not supported! Use " + allowedScreenshotFormats.toString());
        }

        //check Folder
        if (!Files.exists(absPath.getParent())) {
            Files.createDirectory(absPath.getParent());
        }

        if (Files.exists(absPath)) {
            LOGGER.info("overwrite screenshot '{}'", absPath);
        } else {
            absPath = Files.createFile(absPath);
        }
        OutputStream outputStream = Files.newOutputStream(absPath);
        //write image
        ImageIO.write(bufferedImage, screenshotFormat, outputStream);
        LOGGER.info("screen shot saved to file \"" + absPath + "\"");
        return absPath;
    }

    private static String replaceSpecialCharacters(String string) {
        return string.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_+", "_");
    }

    /**
     * serrated method to generate the buffered image, to mock the screen usage in tests
     */
    BufferedImage createBufferedImage(Rectangle rectangle) {
        return rectangle == null ? screen.capture().getImage() : screen.capture(rectangle).getImage();
    }


    /**
     * Takes a screenshot of the hole screen area and save in the given format;
     *
     * @param picturePath Path to the final image
     * @return {@link Path} ot the screenshot.
     */
    public Path takeScreenshot(Path picturePath) {
        return takeScreenshot(picturePath, null);
    }

    /**
     * Takes a screenshot of the assigned area and save in the given format;
     *
     * @param filename  filename to resolve
     * @param rectangle if not null, the screenshot only shows the defined rectangle
     * @return {@link Path} ot the screenshot.
     */
    public Path takeScreenshot(String filename, Rectangle rectangle) {
        return takeScreenshot(resolveTakeScreenshotPath(filename), rectangle);
    }

    /**
     * Takes a screenshot of the assigned area and save in the given format;
     *
     * @param picturePath Path to the final image
     * @param rectangle   if not null, the screenshot only shows the defined rectangle
     * @return {@link Path} ot the screenshot.
     */
    public Path takeScreenshot(Path picturePath, Rectangle rectangle) {
        try {
            if (Files.isDirectory(picturePath)) {
                throw new IOException("Path to a file in format [folder-path]" + File.separator + "screenshotname[.png|jpg] expected!");
            }
            String filename = picturePath.getFileName().toString();
            String format = defaultScreenshotFormat;
            if (filename.contains(".")) {
                format = filename.substring(filename.lastIndexOf(".") + 1);
            } else {
                //add default screenshot format
                picturePath = picturePath.getParent().resolve(filename + "." + format);
            }
            return createPictureFromBufferedImage(picturePath, format, createBufferedImage(rectangle));
        } catch (Exception e) {
            baseActionLoader.getExceptionHandler().handleException(new SakuliException(e,
                    "Can't create Screenshot for path '" + picturePath + "'"));
            return null;
        }

    }

    /**
     * Create a screenshot with timestamp in the filename.
     * Resolves the optFolderPath to a valid path!
     *
     * @return the path of the screenshot file
     */
    public Path takeScreenshotWithTimestamp(final String filenamePostfix, final String optFolderPath, final String optFormat, Rectangle optRectangle) {
        Path folderPath = isEmpty(optFolderPath)
                ? resolveTakeScreenshotPath(filenamePostfix).getParent()
                : Paths.get(optFolderPath);
        return takeScreenshotWithTimestamp(
                filenamePostfix,
                folderPath,
                optFormat, optRectangle);
    }

    /**
     * takes a Screenshot with timestamp for a specific format from the whole screen
     */
    public Path takeScreenshotWithTimestamp(String message, Path folderPath) {
        return takeScreenshotWithTimestamp(message, folderPath, null, null);
    }

    /**
     * takes a Screenshot with timestamp for a specific format
     */
    public Path takeScreenshotWithTimestamp(String message, Path folderPath, String format, Rectangle rectangle) {
        try {
            return takeScreenshotWithTimestampThrowIOException(message, folderPath, format, rectangle);
        } catch (IOException e) {
            baseActionLoader.getExceptionHandler().handleException(new SakuliException(e,
                    "Can't execute 'takeScreenshotWithTimestamp()' for '" + message + ", " + folderPath + ", " + format + "'"));
            return null;
        }
    }

    /**
     * takes a Screenshot with timestamp for a specific format
     */
    public Path takeScreenshotWithTimestampThrowIOException(String message, Path folderPath, String format, Rectangle rectangle) throws IOException {
        return createPictureFromBufferedImage(
                message,
                folderPath,
                StringUtils.isEmpty(format) ? this.defaultScreenshotFormat : format,
                createBufferedImage(rectangle));
    }


    /**
     * Resolves a given String to absolute Path or if not absolute:
     * a) the current testcase folder
     * b) the current testsuite folder
     */
    public Path resolveTakeScreenshotPath(String filename) {
        Path path = Paths.get(filename);
        if (path.isAbsolute()) {
            return path;
        }
        TestCase currentTestCase = baseActionLoader.getCurrentTestCase();
        Path folderPath = currentTestCase != null ? currentTestCase.getTcFile().getParent() : null;
        if (folderPath == null || !Files.exists(folderPath)) {
            LOGGER.warn("The test case folder could not be found => Fallback: Use test suite folder!");
            folderPath = baseActionLoader.getTestSuite().getTestSuiteFolder();
        }
        return folderPath.resolve(filename);
    }

}
