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

package org.sakuli.datamodel.actions;

import org.sakuli.actions.settings.ScreenBasedSettings;
import org.sakuli.exceptions.SakuliException;
import org.sikuli.script.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * custom implementation of an {@link HashMap}, which stores all pictures for the sikuli actions as a {@link Pattern} object.
 *
 * @author tschneck
 *         Date: 08.10.13
 */
public class ImageLib extends HashMap<String, ImageLibObject> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Helper methode to load all png images from folders as {@link ImageLibObject}
     *
     * @param paths multiple path to image folders
     */
    public void addImagesFromFolder(Path... paths) throws IOException {
        for (Path path : paths) {
            logger.info("load all pics from \"" + path.toString() + "\" in picLib");
            DirectoryStream<Path> directory = Files.newDirectoryStream(path);
            for (Path file : directory) {
                if (!Files.isDirectory(file)) {
                    try {
                        ImageLibObject imageLibObject = new ImageLibObject(file);
                        if (imageLibObject.getId() != null) {
                            put(imageLibObject.getId(), imageLibObject);
                        }
                    } catch (SakuliException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * getter, which checks the format of the pic names.
     * The function should prevent typos and throws a {@link SakuliException}
     * if the pic is not loaded in the picLib
     *
     * @param imageName imageName with or without the ending ".png".
     * @return the corresponding {@link Pattern} object to the imageName.
     */
    public Pattern getPattern(String imageName) throws SakuliException {
        return getImage(imageName).getPattern();
    }

    public ImageLibObject getImage(String imageName) throws SakuliException {
        String key = imageName;
        if (ImageLibObject.isValidInputImageFileEnding(imageName)) {
            key = imageName.substring(0, imageName.lastIndexOf("."));
        }

        //if the set imageName is not loaded in the piclib throw a exception and log it
        if (!this.containsKey(key)) {
            throw new SakuliException("SIKULI-PIC \"" + imageName + "\" not found in the loaded picLib folders! ... available pictures are: " + this.values());
        }
        ImageLibObject imageLibObject = get(key);
        imageLibObject.setMinSimilarity(ScreenBasedSettings.MinSimilarity);
        return imageLibObject;
    }
}
