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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.eale.datastore.consul.ConsulRetriableCommand;
import com.ericsson.oss.services.eale.rest.pojo.ExternalAppLaunchConfig;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

@RunWith(MockitoJUnitRunner.class)
public class DatastoreHandlerTest {

    private static final String SYSTEM_ID = "ENIQ-S";
    private static final String APPLICATION_ID = "netan";
    private static final String HOST_DATA = "10.10.10.10";
    private static final String PORT_DATA = "443";
    private static final String PROTOCOL_DATA = "https";

    @InjectMocks
    private DatastoreHandler datastoreHandler;

    @Mock
    private ConsulRetriableCommand consulRetriableCommand;

    @Mock
    private ExternalAppLaunchConfig externalAppLaunchConfig;

    @Mock
    private SystemRecorder systemRecorder;
    @Test
    public void testRetrieveAppConfigFromDataStore_noData() throws Exception {
	final  Map<String, String> mapFromDatastore = new HashMap<String, String>();
	when(consulRetriableCommand.getKVValues(SYSTEM_ID + "/" + APPLICATION_ID)).thenReturn(mapFromDatastore);
	final ExternalAppLaunchConfig externalAppLaunchConfig = datastoreHandler
		.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID);
	assertEquals(null, externalAppLaunchConfig);
    }

    @Test
    public void testRetrieveAppConfigFromDataStore_withData() throws Exception {
	when(consulRetriableCommand.getKVValues(SYSTEM_ID + "/" + APPLICATION_ID)).thenReturn(buildTestData());
	final ExternalAppLaunchConfig externalAppLaunchConfig = datastoreHandler
		.retrieveAppConfigFromDataStore(SYSTEM_ID, APPLICATION_ID);
	assertEquals(HOST_DATA, externalAppLaunchConfig.getHost());
	assertEquals(PORT_DATA, externalAppLaunchConfig.getPort());
	assertEquals(PROTOCOL_DATA, externalAppLaunchConfig.getProtocol());
	assertEquals(true, buildExternalAppLaunchConfig().equals(externalAppLaunchConfig));
    }

    @Test
    public void teststoreAppConfigInDataStore() throws Exception {
	datastoreHandler.storeAppConfigInDataStore(SYSTEM_ID, APPLICATION_ID, buildExternalAppLaunchConfig());
	verify(consulRetriableCommand, times(3)).setKVValue(Mockito.anyString(), Mockito.anyString());
    }

    private ExternalAppLaunchConfig buildExternalAppLaunchConfig() {
	final ExternalAppLaunchConfig externalAppLaunchConfig = new ExternalAppLaunchConfig();
	externalAppLaunchConfig.setHost(HOST_DATA);
	externalAppLaunchConfig.setPort(PORT_DATA);
	externalAppLaunchConfig.setProtocol(PROTOCOL_DATA);
	return externalAppLaunchConfig;
    }

    private Map<String, String> buildTestData() {
	final Map<String, String> testData = new HashMap<String, String>();
	testData.put("ENIQ-S/netan/host", HOST_DATA);
	testData.put("ENIQ-S/netan/port", PORT_DATA);
	testData.put("ENIQ-S/netan/protocol", PROTOCOL_DATA);
	return testData;
    }

}
