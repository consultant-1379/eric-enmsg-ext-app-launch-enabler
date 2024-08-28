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
package com.ericsson.oss.services.eale.rest.resources;

import static com.ericsson.oss.services.eale.rest.Constants.BAD_REQUEST_ERROR_MESSAGE;
import static com.ericsson.oss.services.eale.rest.Constants.BAD_REQUEST_ERROR_MESSAGE_FOR_UPDATE;
import static com.ericsson.oss.services.eale.rest.Constants.COLON;
import static com.ericsson.oss.services.eale.rest.Constants.COLON_DOUBLE_SLASH;
import static com.ericsson.oss.services.eale.rest.Constants.MESSAGE_JSON_FORMAT;
import static com.ericsson.oss.services.eale.rest.Constants.NOT_FOUND_MESSAGE;
import static com.ericsson.oss.services.eale.rest.Constants.SUCCESS_MESSAGE;
import static com.ericsson.oss.services.eale.rest.Constants.BO_APP_ID;
import static com.ericsson.oss.services.eale.rest.Constants.NETAN_APP_ID;
import static com.ericsson.oss.services.eale.rest.Constants.NETANWEBPLAYER_APP_ID;
import static com.ericsson.oss.services.eale.rest.Constants.BILAUNCHPAD_APP_ID;
import static com.ericsson.oss.services.eale.rest.Constants.CENTERAL_MANAGEMENT_CONSOLE_APP_ID;
import static com.ericsson.oss.services.eale.rest.Constants.ENIQ_APPS_DIRECTORY;
import static com.ericsson.oss.services.eale.rest.Constants.APP_LAUNCH_DIRECTORY;
import static com.ericsson.oss.services.eale.rest.Constants.EXT_APP_LAUNCH_CONFIG;

import java.util.HashMap;
import java.io.File;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.eale.datastore.consul.ServerErrorException;
import com.ericsson.oss.services.eale.datastore.handlers.DatastoreHandler;
import com.ericsson.oss.services.eale.ejb.service.FileHandler;
import com.ericsson.oss.services.eale.rest.pojo.ExternalAppLaunchConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;

/*
 * EALE Rest service.
 *
 */

@RequestScoped
@Path("/systems/{systemId}")
public class EALERestResource {

    private static final String SYSTEM_ID = "systemId";
    private static final String APPLICATION_ID = "applicationId";
    private static final String ACCEPT_HEADER = "Accept";
    private static final Logger LOGGER = LoggerFactory.getLogger(EALERestResource.class);
    // APPLICATION_DIRECTORY_MAP is used to store the directory path that external application configuration is stored in
    protected static final Map<String, String> APPLICATION_DIRECTORY_MAP = new HashMap<>();
    // APPLICATION_ID_MAP is used to store the names of supported applicationIDs, along with the names of the external applications that are associated with the given applicationId
    protected static final Map<String, String[]> APPLICATION_ID_MAP = new HashMap<>();

    static {

        APPLICATION_DIRECTORY_MAP.put(BO_APP_ID, ENIQ_APPS_DIRECTORY);
        APPLICATION_DIRECTORY_MAP.put(NETAN_APP_ID, ENIQ_APPS_DIRECTORY);

        APPLICATION_ID_MAP.put(BO_APP_ID, new String[]{BILAUNCHPAD_APP_ID, CENTERAL_MANAGEMENT_CONSOLE_APP_ID});
        APPLICATION_ID_MAP.put(NETAN_APP_ID, new String[]{NETANWEBPLAYER_APP_ID});
    }

    @Inject
    private DatastoreHandler dataStoreHandler;

    @Inject
    private FileHandler fileHandler;

    @Inject
    private SystemRecorder systemRecorder;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authorize(resource = "externalAppLaunchConfig", action = "update")
    @Path("/applications/{applicationId}")
    public Response updateAppConfiguration(@HeaderParam(ACCEPT_HEADER) final String acceptHeader,
	    @PathParam(SYSTEM_ID) final String systemId, @PathParam(APPLICATION_ID) final String applicationId,
	    final ExternalAppLaunchConfig externalAppLaunchConfig)
	    throws ServerErrorException, IllegalArgumentException {
	LOGGER.info("updateAppConfiguration request received for systemId:{}, applicationId:{}, is:{}", systemId,
		applicationId, externalAppLaunchConfig);
	// Audit Logging
	this.systemRecorder.recordSecurityEvent(EXT_APP_LAUNCH_CONFIG, this.getClass().getSimpleName(), String.format("request for systemId{%s}, applicationId{%s}, is:{%s}", systemId, applicationId, externalAppLaunchConfig), "EALE.Update_App_Configuration", ErrorSeverity.NOTICE, "SUCCESS");
	// Check if the configuration exists in datastore.
	final ExternalAppLaunchConfig configFromDataStore = dataStoreHandler.retrieveAppConfigFromDataStore(systemId,
		applicationId);
	if (null == configFromDataStore) {
	    // Create case
	    copyLaunchConfigToLauncher(applicationId);

        return handleCreateCase(systemId, applicationId, externalAppLaunchConfig);
	} else {
	    // Update case
	    copyLaunchConfigToLauncher(applicationId);

        return handleUpdateCase(systemId, applicationId, externalAppLaunchConfig, configFromDataStore);
	}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authorize(resource = "externalAppLaunchConfig", action = "read")
    @Path("/applications/{applicationId}")
    public Response getAppConfiguration(@HeaderParam(ACCEPT_HEADER) final String acceptHeader,
	    @PathParam(SYSTEM_ID) final String systemId, @PathParam(APPLICATION_ID) final String applicationId)
	    throws ServerErrorException, IllegalArgumentException, NullPointerException {
	LOGGER.info("getAppConfiguration request received for systemId:{}, applicationId:{}", systemId, applicationId);
    // Audit Logging
    this.systemRecorder.recordSecurityEvent(EXT_APP_LAUNCH_CONFIG, this.getClass().getSimpleName(), String.format("systemId{%s}, applicationId{%s}", systemId, applicationId), "EALE.Get_App_Configuration", ErrorSeverity.NOTICE, "SUCCESS");
	final ExternalAppLaunchConfig configFromDataStore = dataStoreHandler.retrieveAppConfigFromDataStore(systemId,
		applicationId);
	if (null == configFromDataStore) {
	    final String responseBody = MESSAGE_JSON_FORMAT
		    .format(new Object[] { NOT_FOUND_MESSAGE, systemId, applicationId });
	    return Response.status(Response.Status.NOT_FOUND).entity(responseBody).build();
	} else {
	    return Response.status(Response.Status.OK).entity(configFromDataStore).build();
	}
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/applications/{applicationId}/app-url")
    public Response getAppURL(@HeaderParam(ACCEPT_HEADER) final String acceptHeader,
	    @PathParam(SYSTEM_ID) final String systemId, @PathParam(APPLICATION_ID) final String applicationId)
	    throws ServerErrorException {
	LOGGER.info("getAppURL request is received for systemId:{}, applicationId:{}", systemId, applicationId);
	final ExternalAppLaunchConfig configFromDataStore = dataStoreHandler.retrieveAppConfigFromDataStore(systemId,
		applicationId);
	if (null == configFromDataStore) {
	    final String responseBody = MESSAGE_JSON_FORMAT
		    .format(new Object[] { NOT_FOUND_MESSAGE, systemId, applicationId });
	    return Response.status(Response.Status.NOT_FOUND).entity(responseBody).build();
	}
	final StringBuilder result = new StringBuilder();
	result.append(configFromDataStore.getProtocol()).append(COLON_DOUBLE_SLASH)
		.append(configFromDataStore.getHost()).append(COLON).append(configFromDataStore.getPort());
	return Response.status(Response.Status.OK).entity(result.toString()).build();
    }

    private Response handleCreateCase(final String systemId, final String applicationId,
	    final ExternalAppLaunchConfig externalAppLaunchConfig) throws ServerErrorException {
	// Validate the received config to see if it has all the required fields.
	if (isExternalAppLaunchConfigValid(externalAppLaunchConfig)) {
	    dataStoreHandler.storeAppConfigInDataStore(systemId, applicationId, externalAppLaunchConfig);
	    final String responseBody = MESSAGE_JSON_FORMAT
		    .format(new Object[] { SUCCESS_MESSAGE, systemId, applicationId });
	    return Response.status(Response.Status.CREATED).entity(responseBody).build();
	} else {
	    return sendBadRequestResponse(BAD_REQUEST_ERROR_MESSAGE, systemId, applicationId);
	}
    }

    private Response handleUpdateCase(final String systemId, final String applicationId,
	    final ExternalAppLaunchConfig externalAppLaunchConfig, final ExternalAppLaunchConfig configFromDataStore)
	    throws ServerErrorException {
	final Map<String, String> mapWithProperUpdateValues = getEntriesProvidedInTheRequest(externalAppLaunchConfig);
	if (configFromDataStore.equals(externalAppLaunchConfig)) {
	    return sendSuccessRequestResponse(systemId, applicationId);
	} else if (mapWithProperUpdateValues.isEmpty()
		|| StringUtils.isAnyBlank(mapWithProperUpdateValues.values().stream().toArray(String[]::new))) {
	    return sendBadRequestResponse(BAD_REQUEST_ERROR_MESSAGE_FOR_UPDATE, systemId, applicationId);
	} else {
	    final Map<String, String> fromDataStoreConfigMap = new ObjectMapper().convertValue(configFromDataStore,
		    new TypeReference<Map<String, String>>() {
		    });

	    for (final Map.Entry<String, String> entry : mapWithProperUpdateValues.entrySet()) {
		fromDataStoreConfigMap.put(entry.getKey(), entry.getValue());
	    }
	    final ExternalAppLaunchConfig toBeUpdatedExternalAppLaunchConfig = new ObjectMapper()
		    .convertValue(fromDataStoreConfigMap, ExternalAppLaunchConfig.class);
	    dataStoreHandler.storeAppConfigInDataStore(systemId, applicationId, toBeUpdatedExternalAppLaunchConfig);
	    return sendSuccessRequestResponse(systemId, applicationId);
	}
    }

    private Response sendSuccessRequestResponse(final String systemId, final String applicationId) {
	final String responseBody = MESSAGE_JSON_FORMAT
		.format(new Object[] { SUCCESS_MESSAGE, systemId, applicationId });
	return Response.status(Response.Status.OK).entity(responseBody).build();
    }

    private Response sendBadRequestResponse(final String message, final String systemId, final String applicationId) {
	final String responseBody = MESSAGE_JSON_FORMAT.format(new Object[] { message, systemId, applicationId });
	return Response.status(Response.Status.BAD_REQUEST).entity(responseBody).build();
    }

    /**
     * Validates given {@link ExternalAppLaunchConfig} object and returns boolean
     * value.
     *
     * @param externalAppLaunchConfig
     * @return true if each attribute of {@link ExternalAppLaunchConfig} has some
     *         value. false if any of the attribute of
     *         {@link ExternalAppLaunchConfig} is null/empty.
     */
    private boolean isExternalAppLaunchConfigValid(final ExternalAppLaunchConfig externalAppLaunchConfig) {
        final Map<String, String> externalAppLaunchConfigMap = new ObjectMapper().convertValue(externalAppLaunchConfig,
                new TypeReference<Map<String, String>>() {
                });
        for (final Map.Entry<String, String> entry : externalAppLaunchConfigMap.entrySet()) {
            if (StringUtils.isBlank(entry.getValue())) {
                // One of the attribute is empty or not provided.Treat as invalid data.
                return false;
            }
        }
        return true;
    }
    private void copyLaunchConfigToLauncher(String applicationId) {
        if (isApplicationSupported(applicationId)) {
            LOGGER.info("Adding links for {} application(s) to the ENM launcher", applicationId);
            // Copy the launch configuration directories for the applications associated with the given applicationId
            fileHandler.copyAppConfigtoLaunchDirectory(new File(APPLICATION_DIRECTORY_MAP.get(applicationId)), APPLICATION_ID_MAP.get(applicationId), new File(APP_LAUNCH_DIRECTORY));
        } else {
            LOGGER.info("ApplicationId {} is not supported", applicationId);
        }
    }

    private boolean isApplicationSupported(String applicationId) {
        // Check if the applicationId provided is present in the map of supported applications
        return APPLICATION_ID_MAP.containsKey(applicationId);
    }

    /**
     * Method returns the properties that are provided in the request. It filters
     * out properties which are null or empty.
     *
     * @param externalAppLaunchConfig
     * @return
     */
    private Map<String, String> getEntriesProvidedInTheRequest(final ExternalAppLaunchConfig externalAppLaunchConfig) {
	final Map<String, String> externalAppLaunchConfigMap = new ObjectMapper().convertValue(externalAppLaunchConfig,
		new TypeReference<Map<String, String>>() {
		});

	final Map<String, String> propertiesProvidedInTheRequest = new HashMap<>();
	for (final Map.Entry<String, String> entry : externalAppLaunchConfigMap.entrySet()) {
	    if (!StringUtils.isEmpty(entry.getValue())) {
		propertiesProvidedInTheRequest.put(entry.getKey(), entry.getValue());
	    }
	}
	return propertiesProvidedInTheRequest;
    }

}