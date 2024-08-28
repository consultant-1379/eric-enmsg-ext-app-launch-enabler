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
package com.ericsson.oss.services.eale.rest.pojo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExternalAppLaunchConfigTest {

    @Test
    public void testEquals() {
	final ExternalAppLaunchConfig firstObject = buildExternalAppLaunchConfig();
	final ExternalAppLaunchConfig secondObject = buildExternalAppLaunchConfig();
	assertEquals(true, firstObject.equals(secondObject));
    }

    @Test
    public void testNotEquals() {
	final ExternalAppLaunchConfig firstObject = buildExternalAppLaunchConfig();
	final ExternalAppLaunchConfig secondObject = buildExternalAppLaunchConfig();
	secondObject.setHost("10.10.10.20");
	assertEquals(false, firstObject.equals(secondObject));
    }

    private ExternalAppLaunchConfig buildExternalAppLaunchConfig() {
	final ExternalAppLaunchConfig externalAppLaunchConfig = new ExternalAppLaunchConfig();
	externalAppLaunchConfig.setHost("10.10.10.10");
	externalAppLaunchConfig.setPort("443");
	externalAppLaunchConfig.setProtocol("https");
	return externalAppLaunchConfig;
    }
}
