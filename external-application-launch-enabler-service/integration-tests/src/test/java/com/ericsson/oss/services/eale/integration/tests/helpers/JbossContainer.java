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
package com.ericsson.oss.services.eale.integration.tests.helpers;

import com.ericsson.oss.services.eale.integration.tests.configuration.Constants;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.time.Duration;

public class JbossContainer extends GenericContainer<JbossContainer> {

    public JbossContainer(String dockerImage, Network network) {
        super(dockerImage);
        super.withNetwork(network);
        super.withCreateContainerCmdModifier(createContainerCmd -> {
            createContainerCmd.withUser("root");
        });
        super.withNetworkAliases(Constants.TEST_CONTAINER_JBOSS_HOST_NAME);
        super.withExposedPorts(Constants.TEST_CONTAINER_JBOSS_INTERNAL_PORT);
        super.withEnv(Constants.JBOSS_CONFIG_ENV_KEY, Constants.JBOSS_CONFIG_ENV_VALUE);
        super.withEnv(Constants.SFWK_DB_DEPLOYMENT_TYPE_ENV_KEY, Constants.SFWK_DB_DEPLOYMENT_TYPE_ENV_VALUE);
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource(Constants.GLOBAL_PROPERTIES_FILE),
                Constants.GLOBAL_PROPERTIES_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource("overwrite_files_dummy"),
                Constants.JBOSS_CLI_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource("overwrite_files_dummy"),
                Constants.SFWK_CONTAINER_PATH + Constants.SFWK_CONTAINER_4X_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource("overwrite_files_dummy"),
                Constants.SFWK_CONTAINER_PATH + Constants.SFWK_CONTAINER_MODEL_SERVICE_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource(Constants.STANDALONE_EAP7_FILE),
                Constants.STANDALONE_EAP7_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forClasspathResource(Constants.JBOSS_AS_CONF_FILE),
                Constants.JBOSS_AS_CONF_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forHostPath(Constants.SFWK_DOWNLOAD_LOCATION), Constants.SFWK_CONTAINER_PATH);
        super.withCopyFileToContainer(
                MountableFile.forHostPath((Constants.MODEL_SERVICE_API_DOWNLOAD_LOCATION)),
                Constants.SFWK_CONTAINER_PATH
        );
        super.withCopyFileToContainer(
                MountableFile.forHostPath(Constants.MODEL_SERVICE_MODULE_DOWNLOAD_LOCATION),
                Constants.SFWK_CONTAINER_PATH
        );
        super.withEnv(Constants.TEST_CONTAINER_CONSUL_KVSTORE_ENV_VAR_KEY, Constants.TEST_CONTAINER_CONSUL_KVSTORE_HOST_NAME);
        super.withStartupTimeout(
                Duration.ofSeconds(
                        Constants.JBOSS_STARTUP_TIMEOUT));
    }

    public static void deleteStaleDeployments(final String artefactId, final JbossContainer jbossContainer) {
        final String findCommand = buildFindCommand();
        final String excludeCommand = buildExcludeCommand(artefactId);
        final String deleteCommand = buildDeleteCommand();
        executeInContainer(findCommand, excludeCommand, deleteCommand, jbossContainer);
    }

    private static String buildFindCommand() {
        return String.format("%s %s -type %s -name \"%s*\"", Constants.COMMAND_FIND, Constants.DEPLOYMENTS_DIRECTORY, Constants.FILE_TYPE,
                             Constants.EALE_EAR_NAME_FRAGMENT_TEST
        );
    }

    private static String buildExcludeCommand(final String artefactId) {
        return String.format("| %s -v \"%s%s.ear\"", Constants.COMMAND_GREP, Constants.EALE_EAR_NAME_FRAGMENT_TEST, artefactId);
    }

    private static String buildDeleteCommand() {
        return String.format("| %s %s", Constants.COMMAND_XARGS, Constants.COMMAND_RM);
    }

    private static void executeInContainer(final String findCommand, final String excludeCommand, final String deleteCommand, final JbossContainer jbossContainer) {
        try {
            final String command = String.format("%s %s %s", findCommand, excludeCommand, deleteCommand);
            jbossContainer.execInContainer("bash", "-c", command);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
