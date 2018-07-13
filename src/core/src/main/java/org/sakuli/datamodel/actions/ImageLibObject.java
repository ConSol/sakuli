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

import org.sakuli.exceptions.SakuliCheckedException;
import org.sikuli.script.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mapping object for images in the sakuli application.
 *
 * @author tschneck
 *         Date: 13.10.13
 */
public class ImageLibObject {

    public static final List<String> SUPPORT_INPUT_FILE_ENDINGS = Collections.unmodifiableList(Arrays.asList(".png", ".PNG", ".jpg", ".JPG"));
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path imageFile;
    private String id;
    private Pattern pattern;


    public ImageLibObject(Path imageFile) throws SakuliCheckedException {
        this.imageFile = imageFile;

        if (Files.exists(imageFile)) {
            String name = imageFile.toFile().getName();

            if (isValidInputImageFileEnding(name)) {
                pattern = new Pattern(imageFile.toFile().getAbsolutePath());

                id = name.substring(0, name.lastIndexOf('.'));

                logger.info("loaded image " + this.toString());
            }
            // for .js files do nothing
            else if (name.endsWith(".js")) {
                logger.debug("internal image library: Ignore javascript file " + name);
            }
            // for all other files log a warning
            else {
                logger.info("internal image library: '" + imageFile.toFile().getAbsolutePath() + "' is no .png picture");
            }
        } else {
            throw new SakuliCheckedException("Image-File '" + imageFile.toFile().getAbsolutePath() + "' does not exists!");
        }
    }

    public static boolean isValidInputImageFileEnding(String name) {
        return SUPPORT_INPUT_FILE_ENDINGS.stream().anyMatch(name::endsWith);
    }

    public void setMinSimilarity(double similarityScore) {
        pattern.similar((float) similarityScore);
    }

    public Path getImageFile() {
        return imageFile;
    }

    public String getId() {
        return id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", path=" + imageFile.toFile().getAbsolutePath() + " ]";
    }
}
