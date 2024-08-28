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

import com.ecwid.consul.v1.ConsulClient;

/**
 * A class that will hold a single instance of a {@code ConsulClient} object.
 */
public class ConsulClientHolder {

    private static final String CONSUL_CLIENT_AGENT_HOST = System.getProperty("consul.client.agent.host", "kvstore");
    private static final ConsulClient CONSUL_CLIENT = new ConsulClient(CONSUL_CLIENT_AGENT_HOST);
    private static final ConsulClientHolder INSTANCE = new ConsulClientHolder();

    private ConsulClientHolder() {
    }

    /**
     * @return the single instance of the {@link ConsulClientHolder}
     */
    public static ConsulClientHolder getInstance() {
	return INSTANCE;
    }

    /**
     * Get the consul client
     *
     * @return the {@code ConsulClient} object
     */
    public ConsulClient getClient() {
	return CONSUL_CLIENT;
    }
}
