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
package com.ericsson.oss.services.eale.integration.tests.configuration;

public final class Constants {

    public static final int TEST_CONTAINER_JBOSS_INTERNAL_PORT = 8080;
    public static final String SFWK_CONTAINER_PATH = "/ericsson/3pp/jboss/modules/system/layers/base/com/ericsson";
    public static final String SFWK_DOWNLOAD_LOCATION = "./target/sfwk/com/ericsson";
    public static final String MODEL_SERVICE_API_DOWNLOAD_LOCATION = "./target/model-service-api/com/ericsson";
    public static final String MODEL_SERVICE_MODULE_DOWNLOAD_LOCATION = "./target/model-service-module/com/ericsson";
    public static final String EALE_EAR_NAME_FRAGMENT_TEST = "external-application-launch-enabler-service-ear-";
    public static final String EALE_EAR_RESOURCE_PATH_TEST = "src/test/resources/";
    public static final String DEPLOYMENTS_DIRECTORY = "/ericsson/3pp/jboss/standalone/deployments/";
    public static final String DOCKER_IMAGE = "armdocker.rnd.ericsson.se/proj_oss_releases/enm/eric-enmsg-ext-app-launch-enabler:latest";
    public static final String JBOSS_CONFIG_ENV_KEY = "JBOSSEAP7_CONFIG";
    public static final String JBOSS_CONFIG_ENV_VALUE = "standalone-eap7-enm_custom.xml";
    public static final String SFWK_DB_DEPLOYMENT_TYPE_ENV_KEY = "sfwk.db.deployment.type";
    public static final String SFWK_DB_DEPLOYMENT_TYPE_ENV_VALUE = "NOT_SET";
    public static final String GLOBAL_PROPERTIES_FILE = "global.properties";
    public static final String GLOBAL_PROPERTIES_PATH = "/gp/global.properties";
    public static final String JBOSS_CLI_PATH = "/ericsson/3pp/jboss/bin/cli";
    public static final String SFWK_CONTAINER_4X_PATH = "/oss/itpf/sdk/service-framework/4.x";
    public static final String SFWK_CONTAINER_MODEL_SERVICE_PATH = "/oss/itpf/modeling/modelservice";
    public static final String STANDALONE_EAP7_FILE = "standalone-eap7-enm.xml";
    public static final String STANDALONE_EAP7_PATH = "/ericsson/3pp/jboss/standalone/configuration/standalone-eap7-enm_custom.xml";
    public static final String JBOSS_AS_CONF_FILE = "jboss-as.conf";
    public static final String JBOSS_AS_CONF_PATH = "/ericsson/3pp/jboss/jboss-as.conf";
    public static final String COMMAND_FIND = "find";
    public static final String COMMAND_GREP = "grep";
    public static final String COMMAND_RM = "rm";
    public static final String COMMAND_XARGS = "xargs";
    public static final String FILE_TYPE = "f";
    public static final String PROPERTIES_FILE_NAME = "project.properties";
    public static final String JSON_HEADER = "application/json";
    public static final String REST_ASSURED_BASE_PATH = "/eale/v1";
    public static final int HTTP_OK_STATUS_CODE = 200;
    public static final int HTTP_CREATED_STATUS_CODE = 201;
    public static final int HTTP_BAD_REQUEST_STATUS_CODE = 400;
    public static final int HTTP_NOT_FOUND_STATUS_CODE = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR_STATUS_CODE = 500;
    public static final String TEST_CONTAINER_NETWORK_TYPE = "bridge";
    public static final int TEST_CONTAINER_CONSUL_INTERNAL_PORT = 8500;
    public static final int IMAGE_DEPLOY_TIMEOUT = 60;
    public static final int KVSTORE_STARTUP_TIMEOUT = 30;
    public static final int JBOSS_STARTUP_TIMEOUT = 180;
    public static final String TEST_CONTAINER_CONSUL_KVSTORE_HOST_NAME = "kvstore-net";
    public static final String TEST_CONTAINER_CONSUL_KVSTORE_ENV_VAR_KEY = "consul.client.agent.host";
    public static final String CONSUL_DOCKER_IMAGE_NAME = "hashicorp/consul:1.15";
    public static final String TEST_CONTAINER_JBOSS_HOST_NAME = "jboss-container-net";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PROTOCOL = "protocol";
    public static final String INVALID_BODY = "invalid_body";
    public static final String SLASH = "/";
    public static final String SYSTEMS_PATH = "/systems/";
    public static final String APPLICATIONS_PATH = "/applications/";
    public static final String ACCEPT_HEADER_JSON = "application/json";
    public static final String APP_URL = "/app-url";
    public static final String COLON_DOUBLE_SLASH = "://";
    public static final String COLON = ":";
    public static final String HTTP_BASE = "http://";

    private Constants() {}
}
