/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.javaDSL.actions;

import org.sakuli.loader.BeanLoader;

/**
 * @author Tobias Schneck
 */
public class Application extends org.sakuli.actions.environment.Application {
    /**
     * Creates a new Application of the name or path to the executable.
     *
     * @param appName           name of path of the application.
     * @param resumeOnException if true, the test execution won't stop on an occurring error.
     */
    public Application(String appName, boolean resumeOnException) {
        super(appName, resumeOnException, BeanLoader.loadScreenActionLoader());
    }

    /**
     * Creates a new Application of the name or path to the executable.
     *
     * @param appName name of path of the application.
     */
    public Application(String appName) {
        super(appName, false, BeanLoader.loadScreenActionLoader());
    }
}
