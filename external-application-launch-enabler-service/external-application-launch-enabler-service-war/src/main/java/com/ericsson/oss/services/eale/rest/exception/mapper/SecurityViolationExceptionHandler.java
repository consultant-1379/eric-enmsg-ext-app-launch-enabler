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
package com.ericsson.oss.services.eale.rest.exception.mapper;

import static com.ericsson.oss.services.eale.rest.Constants.ERROR_JSON_FORMAT;
import static com.ericsson.oss.services.eale.rest.Constants.METHOD_NOT_AUTHORIZED_CODE;
import static com.ericsson.oss.services.eale.rest.Constants.METHOD_NOT_AUTHORIZED_MESSAGE;
import static com.ericsson.oss.services.eale.rest.Constants.EXT_APP_LAUNCH_CONFIG;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;

@Provider
public class SecurityViolationExceptionHandler implements ExceptionMapper<SecurityViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityViolationExceptionHandler.class);

	@Inject
	private SystemRecorder systemRecorder;

    @Override
    public Response toResponse(final SecurityViolationException exception) {
	final String responseBody = ERROR_JSON_FORMAT.format(new Object[] { METHOD_NOT_AUTHORIZED_MESSAGE });

	LOGGER.error("Returning Not authorised HTTP Code={} with message={} exception: ", METHOD_NOT_AUTHORIZED_CODE,
		"Method not auhtorized", exception);
	this.systemRecorder.recordSecurityEvent(EXT_APP_LAUNCH_CONFIG, this.getClass().getSimpleName(), String.format("Not authorised HTTP Code={%d} with message={%s} exeption:{%s}", METHOD_NOT_AUTHORIZED_CODE, "Method not auhtorized", exception), "EALE.Security_Violation_Exception_Handler", ErrorSeverity.NOTICE, "FAILURE");
	return Response.status(METHOD_NOT_AUTHORIZED_CODE).entity(responseBody).type(MediaType.APPLICATION_JSON)
		.build();
    }

}
