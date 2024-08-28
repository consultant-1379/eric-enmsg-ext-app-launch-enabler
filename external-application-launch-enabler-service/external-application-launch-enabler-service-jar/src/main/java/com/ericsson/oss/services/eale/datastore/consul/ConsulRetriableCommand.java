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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecwid.consul.transport.TransportException;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.itpf.sdk.core.retry.classic.RetryManagerBean;

/**
 * A class that will allow for methods to be executed towards the Consul client
 * in a retriable manner where required. Default values will be used where no
 * configuration is supplied for the caller.
 *
 * @see #setAttempts(int)
 * @see #setWaitInterval(int)
 */
public class ConsulRetriableCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulRetriableCommand.class);
    private static final String WAIT_INTERVAL_PROPERTY_KEY = "wait.interval.between.attempts";

    private final ConsulClient consulClient = ConsulClientHolder.getInstance().getClient();

    private int attempts = 2;
    private int waitInterval = 2000;

    /**
     * Get a builder class used to build a valid {@link ConsulRetriableCommand}.
     *
     * @return an instance of the {@link ConsulRetriableCommandBuilder}
     */
    public static ConsulRetriableCommandBuilder builder() {
	return new ConsulRetriableCommandBuilder();
    }

    /**
     * Sets how many attempts will be performed to execute the command in case of
     * failure. This is optional and, if not set, will assume that the number of
     * attempts is 2
     *
     * @param attempts The number of attempts, must be >= 1
     */
    void setAttempts(final int attempts) {
	this.attempts = attempts;
    }

    /**
     * Sets the wait time that will be used between each attempt. This is optional
     * and, if not set, will assume that the wait time between attempts is 2
     * seconds. If specified, wait time must be >= 0
     *
     * @param millis the length of time to wait in milliseconds
     */
    void setWaitInterval(final int millis) {
	this.waitInterval = millis;
    }

    /**
     * Get the value for a Consul key.
     *
     * @param key the path of the key
     * @return the value of the Consul key or null if the Consul key is not set
     * @throws ServerErrorException if an error occurs getting a response from
     *                              Consul
     */
    public String getKVValue(final String key) throws ServerErrorException {
	final String value;
	try {
	    final RetriableCommand<String> retriableCommand = defineRetriableCommandGetKVValue(key);
	    value = executeWithRetry(retriableCommand);
	} catch (final RuntimeException exception) {
	    throw new ServerErrorException(exception);
	}
	return value;
    }

    /**
     * Get the map containing key and value for a Consul key.
     *
     * @param key the path of the key
     * @return the map for a Consul key or null if the Consul key is not set
     * @throws ServerErrorException if an error occurs getting a response from
     *                              Consul
     */
    public Map<String, String> getKVValues(final String key) throws ServerErrorException {
	final Map<String, String> value;
	try {
	    final RetriableCommand<Map<String, String>> retriableCommand = defineRetriableCommandGetKVValues(key);
	    value = executeWithRetry(retriableCommand);
	} catch (final RuntimeException exception) {
	    throw new ServerErrorException(exception);
	}
	return value;
    }

    /**
     * Set the value for a Consul key.
     *
     * @param key   the path of the key
     * @param value the value of the key
     * @throws ServerErrorException if an error occurs getting a response from
     *                              Consul
     */
    public void setKVValue(final String key, final String value) throws ServerErrorException {
	try {
	    final RetriableCommand<Void> retriableCommand = defineRetriableCommandSetKVValue(key, value);
	    executeWithRetry(retriableCommand);
	} catch (final RuntimeException exception) {
	    throw new ServerErrorException(exception);
	}
    }

    private <V> V executeWithRetry(final RetriableCommand<V> retriableCommand) {
	final RetryManager retryManager = new RetryManagerBean();

	final RetryPolicy retryPolicy = RetryPolicy.builder().attempts(attempts)
		.waitInterval(getWaitInterval(), TimeUnit.MILLISECONDS).exponentialBackoff(1.0)
		.retryOn(OperationException.class, TransportException.class).build();

	return retryManager.executeCommand(retryPolicy, retriableCommand);
    }

    private int getWaitInterval() {
	int millis = waitInterval;

	try {
	    millis = Integer.parseInt(System.getProperty(WAIT_INTERVAL_PROPERTY_KEY));
	} catch (NumberFormatException | NullPointerException exception) {
	    LOGGER.trace("Setting wait interval to : " + waitInterval / 1000 + " seconds", exception);
	}

	return millis;
    }

    private RetriableCommand<String> defineRetriableCommandGetKVValue(final String key) {
	return retryContext -> {
	    final Response<GetValue> getKVValue = consulClient.getKVValue(key);
	    String value = null;
	    if (isConsulValueNotNull(getKVValue)) {
		value = getKVValue.getValue().getDecodedValue();
	    }
	    return value;
	};
    }

    private RetriableCommand<Map<String, String>> defineRetriableCommandGetKVValues(final String key) {
	return retryContext -> {
	    final Response<List<GetValue>> getKVValues = consulClient.getKVValues(key);
	    final Map<String, String> values = new HashMap<String, String>();
	    if (getKVValues != null && getKVValues.getValue() != null) {
		for (GetValue getValue : getKVValues.getValue()) {
		    if (getValue.getKey() != null && getValue.getDecodedValue() != null) {
			values.put(getValue.getKey(), getValue.getDecodedValue());
		    }
		}
	    }
	    return values;
	};
    }

    private boolean isConsulValueNotNull(final Response<GetValue> consulValue) {
	return consulValue != null && consulValue.getValue() != null
		&& consulValue.getValue().getDecodedValue() != null;
    }

    private RetriableCommand<Void> defineRetriableCommandSetKVValue(final String key, final String value) {
	return retryContext -> {
	    final Response<Boolean> isValueSet = ConsulClientHolder.getInstance().getClient().setKVValue(key, value);
	    if (!isValueSet.getValue()) {
		throw new OperationException(500, "Consul setKVValue unsuccessful: Key: " + key + " Value: " + value,
			isValueSet.getValue().toString());
	    }
	    return null;
	};
    }

}