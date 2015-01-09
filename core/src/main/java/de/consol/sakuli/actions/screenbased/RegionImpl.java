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

import de.consol.sakuli.actions.Action;
import de.consol.sakuli.datamodel.actions.ImageLibObject;
import de.consol.sakuli.exceptions.SakuliException;
import de.consol.sakuli.loader.ScreenActionLoader;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Tobias Schneck
 */
public class RegionImpl extends org.sikuli.script.Region implements Action {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean resumeOnException;
    private final ImageLibObject imagePattern;
    private ScreenActionLoader loader;

    /**
     * Creates a new Region from the hole Screen
     */
    public RegionImpl(boolean resumeOnException, ScreenActionLoader loader) {
        super(0, 0, loader.getScreen().getW(), loader.getScreen().getH(), loader.getScreen());
        this.loader = loader;
        this.resumeOnException = resumeOnException;
        imagePattern = null;
    }

    /**
     * Creates a Region object with underlying pattern.
     * This pattern will be used then internal for the find functions an so on.
     */
    public RegionImpl(String imageName, boolean resumeOnException, ScreenActionLoader loader) {
        super(0, 0, loader.getScreen().getW(), loader.getScreen().getH(), loader.getScreen());
        this.loader = loader;
        this.resumeOnException = resumeOnException;
        imagePattern = loadImage(imageName);
    }

    /**
     * Creates a new Region from the position paramters
     */
    public RegionImpl(int x, int y, int w, int h, boolean resumeOnException, ScreenActionLoader loader) {
        super(x, y, w, h, loader.getScreen());
        this.loader = loader;
        this.imagePattern = null;
        this.resumeOnException = resumeOnException;
    }

    /**
     * Wrapper for the {@link org.sikuli.script.Region} objects.
     */
    public RegionImpl(org.sikuli.script.Region region, boolean resumeOnException, ScreenActionLoader loader) {
        super(region);
        imagePattern = null;
        this.resumeOnException = resumeOnException;
        this.loader = loader;
    }

    /**********************
     * FIND FUNCTIONS
     **********************/

    /**
     * {@link Region#find(String)}.
     */
    public RegionImpl find(String imageName) {
        RegionImpl baseRegion = this.findBaseRegion();
        if (isEmpty(imageName)) {
            return baseRegion;
        }
        Match match;
        try {
            match = baseRegion.find(loadPattern(imageName));
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + loadImage(imageName) + "\" in " + this.toString(), baseRegion, resumeOnException);
        return null;
    }

    /**
     * {@link de.consol.sakuli.actions.screenbased.Region#find()}
     */
    public RegionImpl findBaseRegion() {
        if (imagePattern == null) {
            return this;
        }
        Match match;
        try {
            match = loader.getScreen().find(imagePattern.getPattern());
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + imagePattern + "\" in " + this.toString(), resumeOnException);
        return null;
    }

    /**
     * {@link Region#findRegion(Region)}
     */
    public RegionImpl findRegion(RegionImpl region) {
        RegionImpl baseRegion = findBaseRegion();
        Match match;
        try {
            match = baseRegion.find(region);
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + region + "\" in this region!", baseRegion, resumeOnException);
        return null;
    }


    /**************************************
     * EXISTS FUNCTIONS
     *************************************/

    /**
     * Check whether the give pattern is visible on the screen.
     *
     * @return this {@link Region} or null
     */
    public RegionImpl exists() {
        if (imagePattern == null) {
            return this.findBaseRegion();
        }
        return toRegion(loader.getScreen().exists(imagePattern.getPattern()));
    }

    /**
     * {@link Region#exists(String)}
     */
    public RegionImpl exists(String imageName) {
        if (isEmpty(imageName)) {
            return exists();
        }
        RegionImpl baseRegion = this.findBaseRegion();
        if (baseRegion == null) {
            return null;
        }
        return toRegion(baseRegion.exists(loadPattern(imageName)));
    }

    /**
     * Check whether the give imageName is visible in the Region for x Seconds.
     *
     * @return this {@link Region} or null
     */
    public RegionImpl exists(String imageName, int seconds) {
        RegionImpl baseRegion = this.findBaseRegion();
        if (isEmpty(imageName)) {
            return baseRegion;
        }
        if (baseRegion == null) {
            return null;
        }
        return toRegion(baseRegion.exists(loadPattern(imageName), seconds));
    }

    /**************************************
     * CLICKS
     *************************************/

    /**
     * {@link de.consol.sakuli.actions.screenbased.Region#click()}
     */
    public RegionImpl clickMe() {
        RegionImpl baseRegion = findBaseRegion();
        if (baseRegion == null) {
            //exception is already thrown and handled method findBaseRegion()!
            return null;
        }
        int ret;
        try {
            Location center = baseRegion.getCenter();
            ret = baseRegion.click(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't click on region " + baseRegion, baseRegion, resumeOnException);
            return null;
        }
        return baseRegion;
    }


    /***************************
     * DOUBLE CLICK
     ***************************/

    /**
     * {@link Region#doubleClick()} ()}
     */
    public RegionImpl doubleClickMe() {
        RegionImpl baseRegion = findBaseRegion();
        if (baseRegion == null) {
            //exception is already thrown and handled method findBaseRegion()!
            return null;
        }
        int ret;
        try {
            Location center = baseRegion.getCenter();
            ret = baseRegion.doubleClick(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't double click on region " + baseRegion, baseRegion, resumeOnException);
            return null;
        }
        return baseRegion;
    }


    /***************************
     * RIGHT CLICKS
     ***************************/
    /**
     * {@link Region#rightClick()} ()}
     */
    public RegionImpl rightClickMe() {
        RegionImpl baseRegion = findBaseRegion();
        if (baseRegion == null) {
            //exception is already thrown and handled method findBaseRegion()!
            return null;
        }
        int ret;
        try {
            Location center = baseRegion.getCenter();
            ret = baseRegion.rightClick(center);
        } catch (FindFailed findFailed) {
            ret = 0;
        }
        loader.loadSettingDefaults();
        if (ret != 1) {
            loader.getExceptionHandler().handleException("Couldn't right click on region " + baseRegion, baseRegion, resumeOnException);
            return null;
        }
        return baseRegion;
    }


    /***************************
     * WAIT
     ***************************/
    /**
     * {@link Region#waitForImage(String, int)} ()}
     */
    public RegionImpl waitForImage(String imageName, int seconds) {
        RegionImpl baseRegion = findBaseRegion();
        Match match;
        ImageLibObject imageObj = loadImage(imageName);

        try {
            match = baseRegion.wait(imageObj.getPattern(), seconds);
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + imageObj + "' in" + baseRegion + "waitFor function in " + seconds + " sec.", baseRegion, resumeOnException);
        return null;
    }

    /**
     * {@link Region#waitFor(int)}
     */
    public RegionImpl waitFor(int seconds) {
        Match match;
        try {
            if (imagePattern != null) {
                match = this.wait(imagePattern.getPattern(), seconds);
            } else {
                match = this.wait(this, seconds);
            }
        } catch (FindFailed findFailed) {
            match = null;
        }
        if (match != null) {
            return toRegion(match);
        }
        loader.getExceptionHandler().handleException("Can't find \"" + this + "' in " + this + " waitFor function in " + seconds + " sec.", this, resumeOnException);
        return null;
    }


    /********************
     * KEYBOARD FUNCTIONS
     *******************/


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


    /*******************
     * HELPER FUNCTIONS
     ******************/


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
     * {@link de.consol.sakuli.actions.screenbased.Region#extractText()}
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

    /************************
     * INTERNAL FUNCTIONS
     **********************/

    /**
     * loads the picture from the imageLib, if it is available. Else the Exceptionn will be forwarded to the ExceptionHandler.
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

    public static RegionImpl toRegion(org.sikuli.script.Region region, boolean resumeOnException, ScreenActionLoader loader) {
        if (region != null) {
            return new RegionImpl(region, resumeOnException, loader);
        }
        return null;
    }

    @Override
    public String toString() {
        String ret = "";
        if (imagePattern != null && imagePattern.getImageFile() != null) {
            ret = "Pattern [" + imagePattern.getImageFile().getFileName().toString() + "] - ";
        }

        if (getLastMatch() != null) {
            ret += getLastMatch().toString();
        } else {
            ret += super.toStringShort();
        }
        return ret;
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
