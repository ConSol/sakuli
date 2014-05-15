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

package de.consol.sakuli.actions.screenbased;

import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.datamodel.actions.Screen;
import de.consol.sakuli.datamodel.properties.ActionProperties;
import org.sikuli.script.ScreenHighlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


@Component
public class ScreenshotActions {
    private static final int MAX_FILENAME_LENGTH = 50;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String screenShotFormat;
    private Screen screen;

    @Autowired
    public ScreenshotActions(Screen screen, ActionProperties props) {
        this.screen = screen;
        screenShotFormat = props.getScreenShotFormat();
    }

    /**
     * Takes a screenshot of the current arrea and save in the given format;
     *
     * @param message    Filename of the picture
     * @param folderPath Folder of the place, where the picture should be stored
     * @param region     if not null, the region will be highlighted at the screenshot
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws java.io.IOException
     */
    public Path takeScreenshotAndHighlight(String message, Path folderPath, RegionImpl region) throws IOException {
        ScreenHighlighter screenHighlighter = null;

        //highlight if an valid region parameter is set
        if (region != null) {
            screenHighlighter = new ScreenHighlighter(screen);
            screenHighlighter.highlight(region);
        }

        Path pictureFromBufferedImage = createPictureFromBufferedImage(message, folderPath, screen.capture().getImage());

        // close Highlighter
        if (screenHighlighter != null) {
            screenHighlighter.close();
        }
        return pictureFromBufferedImage;
    }

    public Path takeScreenshot(String message, Path folderPath) throws IOException {
        return takeScreenshotAndHighlight(message, folderPath, null);
    }

    /**
     * takes a Screenshot a specific format
     */
    public Path takeScreenshot(String message, Path folderPath, String format) throws IOException {
        String standardFormat = screenShotFormat;
        screenShotFormat = format;
        Path pic = takeScreenshot(message, folderPath);
        screenShotFormat = standardFormat;
        return pic;
    }

    /**
     * Transfers a {@link BufferedImage} to an picture and saves it
     *
     * @param message    Filename of the picture
     * @param folderPath Folder of the place, where the picture should be stored
     * @return {@link java.nio.file.Path} ot the screenshot.
     * @throws java.io.IOException
     */
    public Path createPictureFromBufferedImage(String message, Path folderPath, BufferedImage bufferedImage) throws IOException {
        logger.info("create screen shot for error \"" + message + "\"");
        //check Folder
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        //generate a valid file name
        String fileName = TestSuite.GUID_DATE_FORMATE.format(new Date())
                + "_"
                + replaceSpecialCharacters(message)
                + "." + screenShotFormat;
        if (fileName.length() > MAX_FILENAME_LENGTH) {
            fileName = fileName.substring(0, MAX_FILENAME_LENGTH) + "." + screenShotFormat;
        }

        //create file
        Path pictureFile = Paths.get(URI.create(folderPath.toUri() + fileName));
        Files.createFile(pictureFile);
        OutputStream outputStream = Files.newOutputStream(pictureFile);

        //write image
        ImageIO.write(bufferedImage, screenShotFormat, outputStream);
        logger.info("screen shot saved to file \""
                + pictureFile.toFile().getAbsolutePath() + "\"");
        return pictureFile;
    }

    private String replaceSpecialCharacters(String string) {
        return string.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_+", "_");
    }

}
