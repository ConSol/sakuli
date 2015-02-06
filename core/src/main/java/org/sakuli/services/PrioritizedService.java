/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2014
 */
package org.sakuli.services;

/**
 * @author Tobias Schneck
 */
public interface PrioritizedService {

    /**
     * @return the Priority of the current {@link ResultService} implementation. Lower int value means lower priority.
     * E.g. 100 is higher prioritized than 10.
     */
    int getServicePriority();
}
