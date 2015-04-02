/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.services;

import org.sakuli.exceptions.SakuliInitException;
import org.sakuli.loader.BeanLoader;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
        SortedSet<InitializingService> services = new TreeSet<>(new PrioritizedServiceComparator<>());
        services.addAll(initializingServices.values());
        for (InitializingService initializingService : services) {
            try {
                initializingService.initTestSuite();
            } catch (SakuliInitException e) {
                BeanLoader.loadBaseActionLoader().getExceptionHandler().handleException(e);
            }
        }
    }
}
