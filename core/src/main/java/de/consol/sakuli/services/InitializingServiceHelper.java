/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.services;

import de.consol.sakuli.loader.BeanLoader;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @author Tobias Schneck
 */
public class InitializingServiceHelper {
    /**
     * Invokes all available InitializingServices to init the test suite data.
     *
     * @throws FileNotFoundException
     */
    public static void invokeInitializingServcies() throws FileNotFoundException {
        Map<String, InitializingService> initializingServices = BeanLoader.loadMultipleBeans(InitializingService.class);
        for (InitializingService initializingService : initializingServices.values()) {
            initializingService.initTestSuite();
        }
    }
}
