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

import java.text.MessageFormat;

public class Constants {
    public static final String ERROR_RESPONSE_WITH_EXCEPTION_TEMPLATE = "'{'\"error\":\"{0} caused by: {1}\"'}'";
    public static final String ERROR_RESPONSE_TEMPLATE = "'{'\"error\":\"{0}\"'}'";
    public static final MessageFormat ERROR_JSON_FORMAT = new MessageFormat(ERROR_RESPONSE_TEMPLATE);
    public static final MessageFormat ERROR_WITH_EXCEPTION_JSON_FORMAT = new MessageFormat(ERROR_RESPONSE_WITH_EXCEPTION_TEMPLATE);

    public static final String MESSAGE_RESPONSE_TEMPLATE = "'{'\"message\":\"{0} for given systemId:{1} and applicationId:{2}\"'}'";
    public static final MessageFormat MESSAGE_JSON_FORMAT = new MessageFormat(MESSAGE_RESPONSE_TEMPLATE);

    public static final int METHOD_NOT_AUTHORIZED_CODE = 401;
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;

    public static final String METHOD_NOT_AUTHORIZED_MESSAGE = "Authorization Failure. Eale_Administrator role or associated capabilities are required.";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
    public static final String NOT_FOUND_MESSAGE = "No data exists in the system";
    public static final String BAD_REQUEST_ERROR_MESSAGE = "All mandatory fields are not provided in the request";
    public static final String BAD_REQUEST_ERROR_MESSAGE_FOR_UPDATE = "Fields provided in the request contain invalid data";
    public static final String SUCCESS_MESSAGE = "Successfully stored configuration";

    public static final String COLON_DOUBLE_SLASH = "://";
    public static final String COLON = ":";

    public static final String BO_APP_ID = "bo";
    public static final String NETAN_APP_ID = "netan";
    public static final String NETANWEBPLAYER_APP_ID = "netanwebplayer";
    public static final String BILAUNCHPAD_APP_ID = "bilaunchpad";
    public static final String CENTERAL_MANAGEMENT_CONSOLE_APP_ID = "centralmanagementconsole";
    public static final String APP_LAUNCH_DIRECTORY = "/ericsson/tor/data/apps";
    public static final String ENIQ_APPS_DIRECTORY = "/ericsson/ERICbonetanstandaloneui_CXP9034896/apps";
    public static final String EXT_APP_LAUNCH_CONFIG = "externalAppLaunchConfig";

}
