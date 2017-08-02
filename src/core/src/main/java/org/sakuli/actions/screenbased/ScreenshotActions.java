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
import org.sakuli.datamodel.TestSuite;
import org.sakuli.datamodel.actions.Screen;
import org.sakuli.datamodel.properties.ActionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Component
public class ScreenshotActions {
    private static final int MAX_FILENAME_LENGTH = 50;
    protected static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotActions.class);
    private final static List<String> allowedScreenshotFormats = Collections.unmodifiableList(Arrays.asList("jpg", "png"));
    private String defaultScreenShotFormat;
    private Screen screen;

    @Autowired
    public ScreenshotActions(Screen screen, ActionProperties props) {
        this.screen = screen;
        defaultScreenShotFormat = props.getScreenShotFormat();
    }

    /**
     * Transfers a {@link BufferedImage} to an picture and saves it
     *
     * @param message          Prefixed filename of the picture (timestamp will be added)
     * @param folderPath       Folder of the place, where the picture should be stored
     * @param screenShotFormat
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws java.io.IOException
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
     * @param pictureFile      Path to the final image
     * @param screenShotFormat
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws java.io.IOException
     */
    static Path createPictureFromBufferedImage(Path pictureFile, String screenShotFormat, BufferedImage bufferedImage) throws IOException {
        Path absPath = pictureFile.normalize().toAbsolutePath();
        //check Folder
        if (!Files.exists(absPath.getParent())) {
            Files.createDirectory(absPath.getParent());
        }

        if (Files.exists(absPath)) {
            LOGGER.info("overwrite screenshot '{}'", absPath);
            Files.delete(absPath);
        }

        Files.createFile(pictureFile);
        OutputStream outputStream = Files.newOutputStream(pictureFile);

        if (!allowedScreenshotFormats.contains(screenShotFormat)) {
            throw new IOException("Format '" + screenShotFormat + "' is not supported! Use " + allowedScreenshotFormats.toString());
        }
        //write image
        ImageIO.write(bufferedImage, screenShotFormat, outputStream);
        LOGGER.info("screen shot saved to file \"" + pictureFile.toFile().getAbsolutePath() + "\"");
        return pictureFile;
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
     * Takes a screenshot of the current display and save in the given format;
     *
     * @param message    Filename of the picture
     * @param folderPath Folder of the place, where the picture should be stored
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws IOException
     */
    public Path takeScreenshotWithTimestamp(String message, Path folderPath) throws IOException {
        return takeScreenshotWithTimestamp(message, folderPath, null);
    }

    /**
     * takes a Screenshot a specific format
     *
     * @param message
     * @param folderPath
     * @param format
     * @return
     * @throws IOException
     */
    public Path takeScreenshotWithTimestamp(String message, Path folderPath, String format) throws IOException {
        return createPictureFromBufferedImage(
                message,
                folderPath,
                StringUtils.isEmpty(format) ? this.defaultScreenShotFormat : format,
                createBufferedImage(null));
    }

    /**
     * Takes a screenshot of the hole screen area and save in the given format;
     *
     * @param picturePath Path to the final image
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws IOException
     */
    public Path takeScreenshot(Path picturePath) throws IOException {
        return takeScreenshot(picturePath, null);
    }

    /**
     * Takes a screenshot of the assigned area and save in the given format;
     *
     * @param picturePath Path to the final image
     * @param rectangle   if not null, the screenshot only shows the defined rectangle
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws IOException
     */
    public Path takeScreenshot(Path picturePath, Rectangle rectangle) throws IOException {
        if (!Files.isDirectory(picturePath)) {
            String filename = picturePath.getFileName().toString();
            String format = defaultScreenShotFormat;
            if (filename.contains(".")) {
                format = filename.substring(filename.lastIndexOf(".") + 1);
            } else {
                //add default screenshot format
                picturePath = picturePath.getParent().resolve(filename + "." + format);
            }
            return createPictureFromBufferedImage(picturePath, format, createBufferedImage(rectangle));
        }
        throw new IOException("Path to a file in format [folder-path]" + File.separator + "screenshotname[.png|jpg] expected!");

    }

}
