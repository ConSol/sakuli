/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.javaDSL.actions;

import org.sakuli.loader.BeanLoader;

/**
 * Wrapper for the Java based {@link org.sakuli.actions.screenbased.Region} functions.
 *
 * @author Tobias Schneck
 */
public class Region extends org.sakuli.actions.screenbased.Region {

    /**
     * Creates a new Region of the complete screen.
     */
    public Region() {
        super(false, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Region of the complete screen.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     */
    public Region(boolean resumeOnException) {
        super(resumeOnException, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Region according to the assigned image name.
     *
     * @param imageName name of the image pattern
     */
    public Region(String imageName) {
        super(imageName, false, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Region according to the assigned image name.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     * @param imageName         name of the image pattern
     */
    public Region(String imageName, boolean resumeOnException) {
        super(imageName, resumeOnException, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Region from the position parameters.
     *
     * @param x x position of a rectangle on the screen.
     * @param y x position of a rectangle on the screen.
     * @param w weight of the rectangle stating by x,y
     * @param h height of the rectangle stating by x,y
     */
    public Region(int x, int y, int w, int h) {
        super(x, y, w, h, false, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Region from the position parameters.
     *
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     * @param x                 x position of a rectangle on the screen.
     * @param y                 x position of a rectangle on the screen.
     * @param w                 weight of the rectangle stating by x,y
     * @param h                 height of the rectangle stating by x,y
     */
    public Region(int x, int y, int w, int h, boolean resumeOnException) {
        super(x, y, w, h, resumeOnException, BeanLoader.loadScreenActionLoader());
    }

    public Region(org.sakuli.actions.screenbased.Region region) {
        super(region.getRegionImpl(), region.getResumeOnException(), BeanLoader.loadScreenActionLoader());
    }
}
