/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.consol.sakuli.services;

import de.consol.sakuli.loader.BeanLoader;

import java.util.Map;

/**
 * @author Tobias Schneck
 */
public class ResultServiceHelper {
    /**
     * Invokes save results for all active result services
     */
    public static void invokeResultServices() {
        Map<String, ResultService> resultServices = BeanLoader.loadMultipleBeans(ResultService.class);
        for (ResultService resultService : resultServices.values()) {
            resultService.refreshStates();
            resultService.saveAllResults();
        }
    }
}
