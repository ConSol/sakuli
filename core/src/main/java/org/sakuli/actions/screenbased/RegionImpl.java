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

package org.sakuli.actions.screenbased;

import org.sakuli.actions.Action;
import org.sakuli.datamodel.actions.ImageLibObject;
import org.sakuli.exceptions.SakuliException;
import org.sakuli.loader.ScreenActionLoader;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Schneck
 */
public class RegionImpl extends org.sikuli.script.Region implements Action {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean resumeOnException;
    private ScreenActionLoader loader;

    /**
     * Creates a new Region from the hole Screen
     */
    public RegionImpl(boolean resumeOnException, ScreenActionLoader loader) {
        this(0, 0, loader.getScreen().getW(), loader.getScreen().getH(), resumeOnException, loader);

    }

    /**
     * Creates a new Region from the position paramters
     */
    public RegionImpl(int x, int y, int w, int h, boolean resumeOnException, ScreenActionLoader loader) {
        super(x, y, w, h, loader.getScreen());
        this.loader = loader;
        this.resumeOnException = resumeOnException;
    }

    /**
     * Wrapper for the {@link org.sikuli.script.Region} objects.
     */
    public RegionImpl(org.sikuli.script.Region region, boolean resumeOnException, ScreenActionLoader loader) {
        super(region);
        this.resumeOnException = resumeOnException;
        this.loader = loader;
    }

    public static RegionImpl toRegion(org.sikuli.script.Region region, boolean resumeOnException, ScreenActionLoader loader) {
        if (region != null) {
            return new RegionImpl(region, resumeOnException, loader);
        }
        return null;
    }

    /**
     * {@link Region#find(String)}.
     */
    public RegionImpl find(String imageName) {
        Match match;
        Pattern imagePattern = loadPattern(imageName);
        try {
            match = this.find(imagePattern);
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + imagePattern + "\" in " + this.toString(), this, resumeOnException);
        return null;
    }


    /**
     * {@link Region#findRegion(Region)}
     */
    public RegionImpl findRegion(RegionImpl region) {
        Match match;
        try {
            match = this.find(region);
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + region + "\" in this region!", this, resumeOnException);
        return null;
    }

    /**
     * {@link Region#exists(String)}
     */
    public RegionImpl exists(String imageName) {
        return toRegion(this.exists(loadPattern(imageName)));
    }


    /**
     * Check whether the give imageName is visible in the Region for x Seconds.
     *
     * @return this {@link Region} or null
     */
    public RegionImpl exists(String imageName, int seconds) {
        return toRegion(this.exists(loadPattern(imageName), seconds));
    }

    /**
     * {@link org.sakuli.actions.screenbased.Region#click()}
     */
    public RegionImpl clickMe() {
        int ret;
        try {
            Location center = this.getCenter();
            ret = this.click(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't click on region " + this, this, resumeOnException);
            return null;
        }
        return this;
    }


    /**
     * {@link Region#doubleClick()} ()}
     */
    public RegionImpl doubleClickMe() {
        int ret;
        try {
            Location center = this.getCenter();
            ret = this.doubleClick(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't double click on region " + this, this, resumeOnException);
            return null;
        }
        return this;
    }


    /**
     * {@link Region#rightClick()} ()}
     */
    public RegionImpl rightClickMe() {
        int ret;
        try {
            Location center = this.getCenter();
            ret = this.rightClick(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't right click on region " + this, this, resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * wrapper implementation for {@link #mouseMove(Object)} ()}
     */
    public RegionImpl moveMouse() {
        int ret;
        try {
            Location center = this.getCenter();
            ret = this.mouseMove(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Could not move the mouse on region " + this, this, resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * {@link Region#waitForImage(String, int)} ()}
     */
    public RegionImpl waitForImage(String imageName, int seconds) {
        Match match;
        ImageLibObject imageObj = loadImage(imageName);
        try {
            match = this.wait(imageObj.getPattern(), seconds);
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + imageObj + "\" in" + this + "waitFor function in " + seconds + " sec.", this, resumeOnException);
        return null;
    }

    /**
     * {@link Region#deleteChars(int)}
     */
    public RegionImpl deleteChars(int amountOfChars) {
        String deleteString = "";
        for (int i = 0; i < amountOfChars; i++) {
            deleteString += Key.DELETE;
        }
        int ret;
        try {
            ret = this.type(this, deleteString);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Can't delete  " + amountOfChars + " chars  in the field '\" + imageName + \"'\"", this, resumeOnException);
            return null;
        }
        return this;
    }

    /**
     * {@link Region#move(int, int)}.
     */
    public RegionImpl move(int offsetX, int offsetY) {

        RegionImpl result = toRegion(this.offset(offsetX, offsetY));
        if (result != null) {
            return result;
        }
        loader.getExceptionHandler().handleException("Cant't set offset for region " + this, this, resumeOnException);
        return null;
    }

    /**
     * {@link org.sakuli.actions.screenbased.Region#extractText()}
     */
    public String extractText() {
        assert Settings.OcrTextRead;
        assert Settings.OcrTextSearch;
        String erg = this.text();
        if (erg.equals("--- no text ---")) {
            loader.getExceptionHandler().handleException("text recognition is currently switched off", this, resumeOnException);
            return null;
        }
        logger.info("Extracted text from region " + this + " is: " + erg);
        return erg;
    }

    /**
     * loads the picture from the imageLib, if it is available. Else the exception will be forwarded to the
     * ExceptionHandler.
     *
     * @param imageName name of the image with or without .png
     * @return a {@link org.sikuli.script.Pattern} object or null if pic is not available.
     */
    protected ImageLibObject loadImage(String imageName) {
        try {
            return loader.getImageLib().getImage(imageName);
        } catch (SakuliException e) {
            loader.getExceptionHandler().handleException(e);
        }
        return null;
    }

    protected Pattern loadPattern(String imageName) {
        return loadImage(imageName).getPattern();
    }

    private RegionImpl toRegion(Match match) {
        if (match != null) {
            return new RegionImpl(match, resumeOnException, loader);
        }
        return null;
    }

    private RegionImpl toRegion(org.sikuli.script.Region region) {
        if (region != null) {
            return new RegionImpl(region, resumeOnException, loader);
        }
        return null;
    }

    @Override
    public String toString() {
        if (getLastMatch() != null) {
            return getLastMatch().toString();
        }
        return super.toStringShort();
    }

    @Override
    public boolean getResumeOnException() {
        return resumeOnException;
    }

    @Override
    public ScreenActionLoader getLoader() {
        return loader;
    }

    @Override
    public RegionImpl getActionRegion() {
        return this;
    }
}
