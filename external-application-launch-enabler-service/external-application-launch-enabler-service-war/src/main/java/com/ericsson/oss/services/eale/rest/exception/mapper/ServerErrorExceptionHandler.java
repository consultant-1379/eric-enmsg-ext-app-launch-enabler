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

import static com.ericsson.oss.services.eale.rest.Constants.ERROR_WITH_EXCEPTION_JSON_FORMAT;
import static com.ericsson.oss.services.eale.rest.Constants.INTERNAL_SERVER_ERROR_CODE;
import static com.ericsson.oss.services.eale.rest.Constants.INTERNAL_SERVER_ERROR_MESSAGE;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.eale.datastore.consul.ServerErrorException;

@Provider
public class ServerErrorExceptionHandler implements ExceptionMapper<ServerErrorException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerErrorExceptionHandler.class);

    @Override
    public Response toResponse(final ServerErrorException exception) {
	final String responseBody = ERROR_WITH_EXCEPTION_JSON_FORMAT.format(new Object[] { INTERNAL_SERVER_ERROR_MESSAGE, exception.getMessage() });

	LOGGER.error("Returning Internal Server Error HTTP Code={} with message={} exception: ",
		INTERNAL_SERVER_ERROR_CODE, "Internal Server Error", exception);
	return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseBody).type(MediaType.APPLICATION_JSON)
		.build();
    }

}
