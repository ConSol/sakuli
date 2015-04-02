/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.2015
 */
package org.sakuli.exceptions;

/**
 * Wrapper for alle sakuli runtime exceptins
 *
 * @author Tobias Schneck
 */
public class SakuliRuntimeException extends RuntimeException {

    public SakuliRuntimeException(String reason, Exception e) {
        super(reason, e);
    }

    public SakuliRuntimeException(String reason) {
        super(reason);
    }
}
