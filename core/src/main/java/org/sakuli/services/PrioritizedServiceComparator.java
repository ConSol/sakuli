/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2014
 */
package org.sakuli.services;

import java.util.Comparator;

/**
 * @author Tobias Schneck
 */
public class PrioritizedServiceComparator<S extends PrioritizedService> implements Comparator<S> {

    @Override
    public int compare(S o1, S o2) {
        int result = -1 * Integer.compare(o1.getServicePriority(), o2.getServicePriority());
        if (result == 0) {
            result = Integer.compare(o1.hashCode(), o2.hashCode());
        }
        return result;
    }
}
