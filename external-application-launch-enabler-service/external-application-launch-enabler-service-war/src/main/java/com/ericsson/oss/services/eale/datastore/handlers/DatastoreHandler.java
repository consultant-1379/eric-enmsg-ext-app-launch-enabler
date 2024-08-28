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
package com.ericsson.oss.services.eale.datastore.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.eale.datastore.consul.ConsulRetriableCommand;
import com.ericsson.oss.services.eale.datastore.consul.ServerErrorException;
import com.ericsson.oss.services.eale.rest.pojo.ExternalAppLaunchConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to perform read, write operations in datastore for Application launch
 * configuration.
 */
public class DatastoreHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreHandler.class);

    @Inject
    private ConsulRetriableCommand consulRetriableCommand;

    private static final String SLASH = "/";

    /**
     * Return entries from consul kv store for a given systemId and applicationId.
     *
     * @param systemId
     * @param applicationId
     * @return
     *       {@link ExternalAppLaunchConfig} if data exists or
     *       null if no data exists with given systemId and applicationId.
     * @throws ServerErrorException
     */
    public ExternalAppLaunchConfig retrieveAppConfigFromDataStore(final String systemId, final String applicationId)
	    throws ServerErrorException {
	ExternalAppLaunchConfig externalAppLaunchConfig = null;
	final Map<String, String> appConfigDataFromDataStore = consulRetriableCommand
		.getKVValues(buildCompleteKey(systemId, applicationId));
	if (!appConfigDataFromDataStore.isEmpty()) {
	    LOGGER.debug("Configuration from datastore is:{}", appConfigDataFromDataStore);
	    // Iterate the map and build ExternalAppLaunchConfig object.
	    externalAppLaunchConfig = getExternalAppLaunchConfigFromMap(appConfigDataFromDataStore);
	}
	return externalAppLaunchConfig;
    }

    /**
     * Store data in consul kv store for a given systemId, applicationId and externalAppLaunchConfig.
     *
     * @param systemId
     * @param applicationId
     * @param externalAppLaunchConfig contains the configuration to be stored in datastore.
     * @throws ServerErrorException
     */
    public void storeAppConfigInDataStore(final String systemId, final String applicationId,
	    final ExternalAppLaunchConfig externalAppLaunchConfig) throws ServerErrorException {
	final Map<String, String> valuesToBeStored = getMapFromExternalAppLaunchConfig(externalAppLaunchConfig);
	for (final Map.Entry<String, String> entry : valuesToBeStored.entrySet()) {
	    final String key = buildCompleteKey(systemId, applicationId, entry.getKey());
	    consulRetriableCommand.setKVValue(key, entry.getValue());
	    LOGGER.debug("Stored key:{}, value:{} in datastore.", key, entry.getValue());
	}
    }

    private String buildCompleteKey(final String... strings) {
	return String.join(SLASH, strings);
    }

    private Map<String, String> getMapFromExternalAppLaunchConfig(
	    final ExternalAppLaunchConfig externalAppLaunchConfig) {
	final ObjectMapper objectMapper = new ObjectMapper();
	return objectMapper.convertValue(externalAppLaunchConfig, new TypeReference<Map<String, String>>() {
	});
    }

    private ExternalAppLaunchConfig getExternalAppLaunchConfigFromMap(final Map<String, String> properties) {
	// Strip the systemId, applicationId to get the actual key that corresponds to a
	// property in ExternalAppLaunchConfig.
	final Map<String, String> filteredMap = trimKeys(properties);
	final ObjectMapper objectMapper = new ObjectMapper();
	return objectMapper.convertValue(filteredMap, ExternalAppLaunchConfig.class);
    }

    /**
     * Strips the key value from each entry of the map and returns a new map with
     * modified keys. E.g., key - /ENIQ-S/netan/host is modified to key - host.
     *
     * @param properties
     * @return Map<String, String>
     */
    private Map<String, String> trimKeys(final Map<String, String> properties) {
	final Map<String, String> filteredMap = new HashMap<String, String>();
	for (final Map.Entry<String, String> entry : properties.entrySet()) {
	    final String key = entry.getKey();
	    filteredMap.put(key.substring(key.lastIndexOf(SLASH) + 1), entry.getValue());
	}
	return filteredMap;
    }

}
