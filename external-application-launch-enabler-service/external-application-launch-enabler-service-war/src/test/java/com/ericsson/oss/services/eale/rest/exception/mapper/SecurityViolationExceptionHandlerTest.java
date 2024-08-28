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

import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static com.ericsson.oss.services.eale.rest.Constants.METHOD_NOT_AUTHORIZED_CODE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SecurityViolationExceptionHandlerTest {

    @Mock
    private SystemRecorder systemRecorder;

    @InjectMocks
    private SecurityViolationExceptionHandler handler;

    @Test
    public void testToResponse() {
        SecurityViolationException exception = new SecurityViolationException("Test Exception");
        Response response = handler.toResponse(exception);
        assert (response.getStatus() == METHOD_NOT_AUTHORIZED_CODE);
        verify(systemRecorder, times(1)).recordSecurityEvent(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                Mockito.anyString()
        );
    }
}