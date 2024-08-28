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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.eale.datastore.handlers.DatastoreHandler;
import com.ericsson.oss.services.eale.rest.pojo.ExternalAppLaunchConfig;
import com.ericsson.oss.services.eale.ejb.service.FileHandler;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

import java.io.File;

import static com.ericsson.oss.services.eale.rest.Constants.ENIQ_APPS_DIRECTORY;
import static com.ericsson.oss.services.eale.rest.Constants.APP_LAUNCH_DIRECTORY;



@RunWith(MockitoJUnitRunner.class)
public class EALERestResourceTest {

    @InjectMocks
    private EALERestResource ealeRestResource;

    @Mock
    private FileHandler fileHandler;

    @Mock
    private DatastoreHandler datastoreHandler;

    @Mock
    private ExternalAppLaunchConfig externalAppLaunchConfig;

    @Mock
    private SystemRecorder systemRecorder;


    private static final String SYSTEM_ID = "ENIQ-S";
    private static final String APPLICATION_ID = "netan";
    private static final String BO_APPLICATION_ID = "bo";
    private static final String UNSUPPORTED_APPLICATION_ID = "unsupported";
    private static final String[] BO_APPLICATIONS = new String[]{"bilaunchpad", "centralmanagementconsole"};


    @Test
    public void testUpdateAppConfiguration_InValidAppConfig() throws Exception {
        final ExternalAppLaunchConfig emptyAppLaunchConfig = new ExternalAppLaunchConfig();
        when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
                .thenReturn(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ealeRestResource
                .updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, emptyAppLaunchConfig).getStatus());
    }

    @Test
    public void testUpdateSupportedAppConfiguration_successfulCreate() throws Exception {
        when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
                .thenReturn(null);
        doNothing().when(fileHandler).copyAppConfigtoLaunchDirectory(new File(ENIQ_APPS_DIRECTORY), BO_APPLICATIONS, new File(APP_LAUNCH_DIRECTORY));
        assertEquals(Response.Status.CREATED.getStatusCode(), ealeRestResource
                .updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, buildExternalAppLaunchConfig())
                .getStatus());
    }

    @Test
    public void testUpdateAppConfiguration_updateWithToNoChange() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
	assertEquals(Response.Status.OK.getStatusCode(), ealeRestResource
		.updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, buildExternalAppLaunchConfig())
		.getStatus());
    }

    @Test
    public void testUpdateAppConfiguration_updateBadRequestDueToAllWhiteSpacesAttributes() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
	final ExternalAppLaunchConfig toBeUpdatedExternalAppLaunchConfig = buildExternalAppLaunchConfig();
	toBeUpdatedExternalAppLaunchConfig.setHost(" ");
	toBeUpdatedExternalAppLaunchConfig.setPort("  ");
	toBeUpdatedExternalAppLaunchConfig.setProtocol("   ");
	assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ealeRestResource
		.updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, toBeUpdatedExternalAppLaunchConfig)
		.getStatus());
    }

    @Test
    public void testUpdateAppConfiguration_updateBadRequestDueToWhiteSpaceAttribute() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
	final ExternalAppLaunchConfig toBeUpdatedExternalAppLaunchConfig = buildExternalAppLaunchConfig();
	toBeUpdatedExternalAppLaunchConfig.setHost("   ");
	toBeUpdatedExternalAppLaunchConfig.setPort("443");
	toBeUpdatedExternalAppLaunchConfig.setProtocol("https");
	assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ealeRestResource
		.updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, toBeUpdatedExternalAppLaunchConfig)
		.getStatus());
    }

    @Test
    public void testUpdateAppConfiguration_updateBadRequestDueToEmptyAttributes() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
	final ExternalAppLaunchConfig toBeUpdatedExternalAppLaunchConfig = buildExternalAppLaunchConfig();
	toBeUpdatedExternalAppLaunchConfig.setHost("");
	toBeUpdatedExternalAppLaunchConfig.setPort("");
	toBeUpdatedExternalAppLaunchConfig.setProtocol("");
	assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ealeRestResource
		.updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, toBeUpdatedExternalAppLaunchConfig)
		.getStatus());
    }

    @Test
    public void testUpdateAppConfiguration_successfulUpdate() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, BO_APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
        doNothing().when(fileHandler).copyAppConfigtoLaunchDirectory(new File(ENIQ_APPS_DIRECTORY), BO_APPLICATIONS,new File(APP_LAUNCH_DIRECTORY));
        final ExternalAppLaunchConfig toBeUpdatedExternalAppLaunchConfig = buildExternalAppLaunchConfig();
	toBeUpdatedExternalAppLaunchConfig.setHost("20.20.20.20");
	assertEquals(Response.Status.OK.getStatusCode(), ealeRestResource
		.updateAppConfiguration(BO_APPLICATION_ID, SYSTEM_ID, BO_APPLICATION_ID, toBeUpdatedExternalAppLaunchConfig)
		.getStatus());
    }

    @Test
    public void testUpdateUnsupportedAppConfiguration_UnsuccessfulUpdate() throws Exception {
        when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, UNSUPPORTED_APPLICATION_ID))
                .thenReturn(buildExternalAppLaunchConfig());
        doNothing().when(fileHandler).copyAppConfigtoLaunchDirectory(new File(ENIQ_APPS_DIRECTORY), BO_APPLICATIONS,new File(APP_LAUNCH_DIRECTORY));
        assertEquals(Response.Status.OK.getStatusCode(), ealeRestResource
                .updateAppConfiguration(UNSUPPORTED_APPLICATION_ID, SYSTEM_ID, UNSUPPORTED_APPLICATION_ID, buildExternalAppLaunchConfig())
                .getStatus());
    }

    @Test
    public void testGetAppConfiguration_NotFound() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID)).thenReturn(null);
	assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
		ealeRestResource.getAppConfiguration(APPLICATION_ID, SYSTEM_ID, APPLICATION_ID).getStatus());
    }

    @Test
    public void testGetAppConfiguration_Found() throws Exception {
        when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID))
                .thenReturn(buildExternalAppLaunchConfig());
        assertEquals(Response.Status.OK.getStatusCode(),
                ealeRestResource.getAppConfiguration(APPLICATION_ID, SYSTEM_ID, APPLICATION_ID).getStatus());
    }

    @Test
    public void testGetAppURL_NotFound() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID)).thenReturn(null);
	assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
		ealeRestResource.getAppURL(APPLICATION_ID, SYSTEM_ID, APPLICATION_ID).getStatus());
    }

    @Test
    public void testGetAppURL_Found() throws Exception {
	when(datastoreHandler.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID))
		.thenReturn(buildExternalAppLaunchConfig());
	assertEquals(Response.Status.OK.getStatusCode(),
		ealeRestResource.getAppURL(APPLICATION_ID, SYSTEM_ID, APPLICATION_ID).getStatus());
    }

    private ExternalAppLaunchConfig buildExternalAppLaunchConfig() {
        final ExternalAppLaunchConfig externalAppLaunchConfig = new ExternalAppLaunchConfig();
        externalAppLaunchConfig.setHost("10.10.10.10");
        externalAppLaunchConfig.setPort("443");
        externalAppLaunchConfig.setProtocol("https");
        return externalAppLaunchConfig;
    }

}
