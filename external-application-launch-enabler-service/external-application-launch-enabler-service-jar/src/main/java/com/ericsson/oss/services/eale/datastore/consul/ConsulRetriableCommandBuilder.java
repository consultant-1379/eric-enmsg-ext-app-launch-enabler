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
 * A class that will allow a {@link ConsulRetriableCommand} instance to be built.
 */
public class ConsulRetriableCommandBuilder {

    private int waitInterval = 2000;
    private int attempts = 2;

    /**
     * Package local constructor to ensure {@link ConsulRetriableCommand#builder()} is method of enforcing builder usage.
     */
    ConsulRetriableCommandBuilder() {
    }

    private ConsulRetriableCommandBuilder self() {
        return this;
    }

    /**
     * Build the required {@code {@link ConsulRetriableCommand}} object.
     *
     * @return the required object instance.
     */
    public ConsulRetriableCommand build() {
        validate();
        final ConsulRetriableCommand retriableCommand = new ConsulRetriableCommand();
        retriableCommand.setWaitInterval(waitInterval);
        retriableCommand.setAttempts(attempts);
        return retriableCommand;
    }

    /**
     * Sets the wait time that will be used between each attempt.
     * This is optional and, if not set, will assume that the wait time between attempts is 2 seconds.
     * If specified, wait time must be greater or equal to 0
     *
     * @param millis
     *            the length of time to wait in milliseconds
     * @return a reference to this builder, to allow method chaining.
     */
    public ConsulRetriableCommandBuilder waitInterval(final int millis) {
        this.waitInterval = millis;
        return self();
    }

    /**
     * Sets how many attempts will be performed to execute the command in case of failure.
     * This is optional and, if not set, will assume that the number of attempts is 1
     *
     * @param attempts
     *            The number of attempts, must be greater or equal to 1
     * @return a reference to this builder, to allow method chaining.
     */
    public ConsulRetriableCommandBuilder attempts(final int attempts) {
        this.attempts = attempts;
        return self();
    }

    private void validate() {
        if (attempts < 1) {
            throw new IllegalArgumentException("The number of attempts must be specified with a number >= 1");
        } else if (waitInterval < 0) {
            throw new IllegalArgumentException("The wait interval must be specified with a number >= 0");
        }
    }
}
