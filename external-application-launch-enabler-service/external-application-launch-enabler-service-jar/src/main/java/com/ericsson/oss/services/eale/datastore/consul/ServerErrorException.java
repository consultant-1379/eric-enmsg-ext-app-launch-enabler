/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.eale.datastore.consul;

/**
 * This class provides an exception used to describe server error HTTP response codes.
 */
public class ServerErrorException extends Exception {

    private static final long serialVersionUID = 6442695403999762183L;

    /**
     * Constructor for ServerErrorException.
     *
     * @param message
     *            String that describes the error
     */
    public ServerErrorException(final String message) {
        super(message);
    }

    /**
     * Constructor for ServerErrorException with the specified cause
     *
     * @param cause
     *            Cause of the exception
     */
    public ServerErrorException(final Throwable cause) {
        super(cause);
    }
}
