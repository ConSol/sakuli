/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package org.sakuli.services;

import org.sakuli.loader.BeanLoader;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Tobias Schneck
 */
public class ResultServiceHelper {
    /**
     * Invokes save results for all active result services
     */
    public static void invokeResultServices() {
        Map<String, ResultService> resultServices = BeanLoader.loadMultipleBeans(ResultService.class);
        SortedSet<ResultService> services = new TreeSet<>(new PrioritizedServiceComparator<ResultService>());
        services.addAll(resultServices.values());
        for (ResultService resultService : services) {
            resultService.refreshStates();
            resultService.saveAllResults();
        }
    }
}
