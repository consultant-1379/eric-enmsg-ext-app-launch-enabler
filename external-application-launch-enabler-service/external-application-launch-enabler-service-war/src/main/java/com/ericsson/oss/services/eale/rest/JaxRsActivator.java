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
package com.ericsson.oss.services.eale.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ericsson.oss.services.eale.rest.exception.mapper.SecurityViolationExceptionHandler;
import com.ericsson.oss.services.eale.rest.exception.mapper.ServerErrorExceptionHandler;
import com.ericsson.oss.services.eale.rest.resources.EALERestResource;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is
 * the Java EE 8 "no XML" approach to activating JAX-RS.
 *
 * <p>
 * Resources are served relative to the servlet path specified in the
 * {@link ApplicationPath} annotation.
 * </p>
 */
@ApplicationPath("/v1")
public class JaxRsActivator extends Application {

    @Override
    public Set<Class<?>> getClasses() {
	final Set<Class<?>> classes = new HashSet<>();
	classes.add(EALERestResource.class);
	classes.add(SecurityViolationExceptionHandler.class);
	classes.add(ServerErrorExceptionHandler.class);
	return classes;
    }

}
