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

package org.sakuli.datamodel.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author tschneck Date: 14.05.14
 */
@Component
public class ActionProperties extends AbstractProperties {

    public static final String TAKE_SCREENSHOTS = "sakuli.screenshot.onError";
    public static final String AUTO_HIGHLIGHT_ENABLED = "sakuli.autoHighlight.enabled";
    public static final String AUTO_HIGHLIGHT_SEC = "sakuli.autoHighlight.seconds";
    public static final String AUTO_HIGHLIGHT_SEC_DEFAULT = "1.1f";
    public static final String TYPE_DELAY_PROPERTY = "sakuli.screenbased.typeDelay";
    public static final String CLICK_DELAY_PROPERTY = "sakuli.screenbased.clickDelay";
    public static final String ENCRYPTION_INTERFACE = "sakuli.encryption.interface";
    public static final String ENCRYPTION_INTERFACE_AUTODETECT = "sakuli.encryption.interface.autodetect";
    public static final String SCREENSHOT_FOLDER_PROPERTY = "sakuli.screenshot.dir";
    public static final String SCREENSHOT_FORMAT_PROPERTY = "sakuli.screenshot.format";
    protected static final String ENCRYPTION_INTERFACE_DEFAULT = "null";
    protected static final String ENCRYPTION_INTERFACE_AUTODETECT_DEFAULT = "true";
    @Value("${" + TAKE_SCREENSHOTS + "}")
    private boolean takeScreenshots;
    @Value("${" + AUTO_HIGHLIGHT_ENABLED + "}")
    private boolean autoHighlightEnabled;
    @Value("${" + AUTO_HIGHLIGHT_SEC + ":" + AUTO_HIGHLIGHT_SEC_DEFAULT + "}")
    private float autoHighlightSeconds;
    @Value("${" + CLICK_DELAY_PROPERTY + "}")
    private double clickDelay;
    @Value("${" + TYPE_DELAY_PROPERTY + "}")
    private double typeDelay;
    @Value("${" + ENCRYPTION_INTERFACE + ":" + ENCRYPTION_INTERFACE_DEFAULT + "}")
    private String encryptionInterface;
    @Value("${" + ENCRYPTION_INTERFACE_AUTODETECT + ":" + ENCRYPTION_INTERFACE_AUTODETECT_DEFAULT + "}")
    private boolean encryptionInterfaceAutodetect;
    @Value("${" + SCREENSHOT_FOLDER_PROPERTY + "}")
    private String screenShotFolderPropertyValue;
    private Path screenShotFolder;
    @Value("${" + SCREENSHOT_FORMAT_PROPERTY + "}")
    private String screenShotFormat;

    @PostConstruct
    public void initFolders() throws IOException {
        if (takeScreenshots) {
            screenShotFolder = Paths.get(screenShotFolderPropertyValue).normalize();
            try {
                checkFolders(screenShotFolder);
            } catch (FileNotFoundException e) {
                System.out.println(String.format("create sakuli screenshot folder for logging '%s'",
                        screenShotFolder.toString()));
                Files.createDirectories(screenShotFolder);
            }
        }
    }

    public boolean isTakeScreenshots() {
        return takeScreenshots;
    }

    public void setTakeScreenshots(boolean takeScreenshots) {
        this.takeScreenshots = takeScreenshots;
    }

    public boolean isAutoHighlightEnabled() {
        return autoHighlightEnabled;
    }

    public void setAutoHighlightEnabled(boolean autoHighlightEnabled) {
        this.autoHighlightEnabled = autoHighlightEnabled;
    }

    public float getAutoHighlightSeconds() {
        return autoHighlightSeconds;
    }

    public void setAutoHighlightSeconds(float autoHighlightSeconds) {
        this.autoHighlightSeconds = autoHighlightSeconds;
    }

    /**
     * Specify a delay between the mouse down and up in seconds as 0.nnn. This
     * only applies to the next click action and is then reset to 0 again. A value
     * &gt; 1 is cut to 1.0 (max delay of 1 second)
     */
    public double getClickDelay() {
        return clickDelay;
    }

    public void setClickDelay(double clickDelay) {
        this.clickDelay = clickDelay;
    }

    /**
     * Specify a delay between the key presses in seconds as 0.nnn. This only
     * applies to the next type and is then reset to 0 again. A value &gt; 1 is cut
     * to 1.0 (max delay of 1 second)
     */
    public double getTypeDelay() {
        return typeDelay;
    }

    public void setTypeDelay(double typeDelay) {
        this.typeDelay = typeDelay;
    }

    public String getEncryptionInterface() {
        return encryptionInterface;
    }

    public void setEncryptionInterface(String encryptionInterface) {
        this.encryptionInterface = encryptionInterface;
    }

    public boolean isEncryptionInterfaceAutodetect() {
        return encryptionInterfaceAutodetect;
    }

    public void setEncryptionInterfaceAutodetect(boolean encryptionInterfaceAutodetect) {
        this.encryptionInterfaceAutodetect = encryptionInterfaceAutodetect;
    }

    public String getScreenShotFolderPropertyValue() {
        return screenShotFolderPropertyValue;
    }

    public void setScreenShotFolderPropertyValue(String screenShotFolderPropertyValue) {
        this.screenShotFolderPropertyValue = screenShotFolderPropertyValue;
    }

    public Path getScreenShotFolder() {
        return screenShotFolder;
    }

    public void setScreenShotFolder(Path screenShotFolder) {
        this.screenShotFolder = screenShotFolder;
    }

    public String getScreenShotFormat() {
        return screenShotFormat;
    }

    public void setScreenShotFormat(String screenShotFormat) {
        this.screenShotFormat = screenShotFormat;
    }
}
