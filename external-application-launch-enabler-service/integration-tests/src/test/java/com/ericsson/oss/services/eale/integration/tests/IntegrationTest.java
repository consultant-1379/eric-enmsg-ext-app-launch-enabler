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
package com.ericsson.oss.services.eale.integration.tests;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ericsson.oss.services.eale.integration.tests.helpers.JbossContainer;
import com.ericsson.oss.services.eale.rest.pojo.ExternalAppLaunchConfig;
import io.restassured.RestAssured;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.consul.ConsulContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.org.awaitility.core.ConditionTimeoutException;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ericsson.oss.services.eale.integration.tests.configuration.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntegrationTest {

    private static final String ARTEFACT_ID = getPropertyValueFromFile();
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTest.class);
    @ClassRule
    public static JbossContainer jbossContainer;
    @ClassRule
    public static GenericContainer<?> consulContainer;
    @ClassRule
    public static ConsulClient client;

    @BeforeAll public static void setupContainers() {

        final Network network = Network.builder().driver(TEST_CONTAINER_NETWORK_TYPE).build();

        consulContainer = new ConsulContainer(CONSUL_DOCKER_IMAGE_NAME).withExposedPorts(
                TEST_CONTAINER_CONSUL_INTERNAL_PORT).withNetwork(network).withNetworkAliases(
                TEST_CONTAINER_CONSUL_KVSTORE_HOST_NAME).withLogConsumer(
                new Slf4jLogConsumer(LOGGER)).withStartupTimeout(Duration.ofSeconds(KVSTORE_STARTUP_TIMEOUT));

        consulContainer.start();

        jbossContainer = new JbossContainer(DOCKER_IMAGE, network);

        jbossContainer.start();


        final String earLocalResourceLocation = EALE_EAR_RESOURCE_PATH_TEST + EALE_EAR_NAME_FRAGMENT_TEST + ARTEFACT_ID + ".ear";
        final String earMountedLocation = DEPLOYMENTS_DIRECTORY + EALE_EAR_NAME_FRAGMENT_TEST + ARTEFACT_ID + ".ear";

        JbossContainer.deleteStaleDeployments(ARTEFACT_ID, jbossContainer);
        jbossContainer.copyFileToContainer(MountableFile.forHostPath(earLocalResourceLocation), earMountedLocation);


        LOGGER.info("Deploying image at : {}", earMountedLocation);

        try {
            await().atMost(IMAGE_DEPLOY_TIMEOUT, TimeUnit.SECONDS).until(
                    checkForDeployed(earMountedLocation + ".deployed"));
        } catch (final ConditionTimeoutException conditionTimeoutException) {
            LOGGER.error("Timed out waiting on EALE image to deploy. Expect tests to fail.", conditionTimeoutException);
        }

        testContainersRunning();
    }

    static void testContainersRunning() {
        RestAssured.baseURI = HTTP_BASE + jbossContainer.getHost() + ":" + jbossContainer.getFirstMappedPort();
        RestAssured.basePath = REST_ASSURED_BASE_PATH;

        client = new ConsulClient(consulContainer.getHost(), consulContainer.getFirstMappedPort());

        try {
            assertThat("JBoss testcontainer not running.", jbossContainer.isRunning(), equalTo(true));
            assertThat("Consul testcontainer not running.", consulContainer.isRunning(), equalTo(true));
        } catch (AssertionError assertionError) {
            LOGGER.error("Testcontainer is not running. Expect tests to fail.", assertionError);
        }
    }

    @Test void testUpdateAppConfiguration_01() {
        final String testSystemId = "ENIQ-S-1";
        final String testApplicationId = "bo-1";

        final ExternalAppLaunchConfig dummyAppConfigDataOne = createEaleTestResource(
                "test_host-1", "test_port-1", "test_protocol-1");

        final int statusCodeOne = given().contentType(ACCEPT_HEADER_JSON).when().body(dummyAppConfigDataOne).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).statusCode();

        assertEquals(HTTP_CREATED_STATUS_CODE, statusCodeOne,
                     String.format("Fail on systemId= %s applicationId=%s", testSystemId, testApplicationId)
        );
        assert (validateResponse(testSystemId, testApplicationId, HOST, dummyAppConfigDataOne.getHost()));
        assert (validateResponse(testSystemId, testApplicationId, PORT, dummyAppConfigDataOne.getPort()));
        assert (validateResponse(testSystemId, testApplicationId, PROTOCOL, dummyAppConfigDataOne.getProtocol()));

    }

    @Test void testUpdateAppConfiguration_02() {
        final String testSystemId = "ENIQ-S-2";
        final String testApplicationId = "bo-2";

        final ExternalAppLaunchConfig dummyAppConfigData = createEaleTestResource(
                null, "test_port-2", "test_protocol-2");

        final int statusCode = given().contentType(ACCEPT_HEADER_JSON).when().body(dummyAppConfigData).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).statusCode();

        assertEquals(HTTP_BAD_REQUEST_STATUS_CODE, statusCode,
                     String.format("Fail on systemId= %s applicationId=%s", testSystemId, testApplicationId)
        );
    }

    @Test void testUpdateAppConfiguration_03() {
        final String testSystemId = "ENIQ-S-3";
        final String testApplicationId = "bo-3";
        final String getHostTest = "test_host-3";
        final String getPortTest = "test_port-3";
        final String getProtocolTest = "test_protocol-3";

        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + HOST, getHostTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PORT, getPortTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PROTOCOL, getProtocolTest);

        final ExternalAppLaunchConfig dummyAppConfigData = createEaleTestResource(
                "test_host-3", "test_port-3", "test_protocol-3");

        final int statusCode = given().contentType(ACCEPT_HEADER_JSON).when().body(dummyAppConfigData).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).statusCode();

        assertEquals(HTTP_OK_STATUS_CODE, statusCode,
                     String.format("Fail on systemId= %s applicationId=%s", testSystemId, testApplicationId)
        );

    }

    @Test void testUpdateAppConfiguration_03_hostUpdated() {
        final String testSystemId = "ENIQ-S-3";
        final String testApplicationId = "bo-3";
        final String getHostTest = "test_host-3";
        final String getPortTest = "test_port-3";
        final String getProtocolTest = "test_protocol-3";

        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + HOST, getHostTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PORT, getPortTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PROTOCOL, getProtocolTest);

        final ExternalAppLaunchConfig dummyAppConfigData = createEaleTestResource("test_host_updated-3", "test_port-3", "test_protocol-3");

        final int statusCode = given().contentType(ACCEPT_HEADER_JSON).when().body(dummyAppConfigData).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).statusCode();

        assertEquals(HTTP_OK_STATUS_CODE, statusCode,
                     String.format("Fail on systemId= %s applicationId=%s", testSystemId, testApplicationId)
        );

    }

    @Test void testUpdateAppConfiguration_InvalidPayload() {
        final String testSystemId = "ENIQ-S-4";
        final String testApplicationId = "bo-4";

        given().contentType(JSON_HEADER).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).then().assertThat().statusCode(
                HTTP_INTERNAL_SERVER_ERROR_STATUS_CODE);

        given().contentType(ACCEPT_HEADER_JSON).when().body(INVALID_BODY).put(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).then().assertThat().statusCode(
                HTTP_BAD_REQUEST_STATUS_CODE);
    }

    @Test void testGetAppConfiguration() {
        final String testSystemId = "ENIQ-S-Test";
        final String testApplicationId = "bo_test";
        final String getHostTest = "test_get_host";
        final String getPortTest = "test_get_port";
        final String getProtocolTest = "test_get_protocol";

        final int statusCodeOne = given().contentType(ACCEPT_HEADER_JSON).when().get(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId).statusCode();

        assertEquals(HTTP_NOT_FOUND_STATUS_CODE, statusCodeOne,
                     "fail on " + "systemId=" + testSystemId + " applicationId=" + testApplicationId
        );

        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + HOST, getHostTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PORT, getPortTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PROTOCOL, getProtocolTest);

        final io.restassured.response.Response getResponse = given().contentType(ACCEPT_HEADER_JSON).when().get(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId);

        assertEquals(HTTP_OK_STATUS_CODE, getResponse.statusCode(),
                     String.format("Fail on systemId= %s applicationId=%s", testSystemId, testApplicationId)
        );

        ExternalAppLaunchConfig responseObject = getResponse.as(ExternalAppLaunchConfig.class);

        assertThat(responseObject.getHost(), equalTo(getHostTest));
        assertThat(responseObject.getPort(), equalTo(getPortTest));
        assertThat(responseObject.getProtocol(), equalTo(getProtocolTest));
    }

    @Test void testGetAppUrl() {
        final String testSystemId = "testGetAppUrl_testSystemId";
        final String testApplicationId = "testGetAppUrl_testApplicationId";
        final String getHostTest = "10.x.x.x";
        final String getPortTest = "1234";
        final String getProtocolTest = "https";

        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + HOST, getHostTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PORT, getPortTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PROTOCOL, getProtocolTest);

        io.restassured.response.Response response = given().contentType(JSON_HEADER).get(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationId + APP_URL);
        Assertions.assertEquals(HTTP_OK_STATUS_CODE, response.getStatusCode());
        final String appUrl = response.body().asString();
        Assertions.assertEquals(getProtocolTest + COLON_DOUBLE_SLASH + getHostTest + COLON + getPortTest, appUrl);
    }

    @Test void testGetAppUrl_invalidParams() {
        final String testSystemId = "testGetAppUrl_invalidParams_testSystemId";
        final String testApplicationId = "testGetAppUrl_invalidParams_testApplicationId";
        final String testSystemIdInvalid = "testGetAppUrl_invalidParams_testSystemIdInvalid";
        final String testApplicationIdInvalid = "testGetAppUrl_invalidParams_testApplicationIdInvalid";
        final String getHostTest = "10.x.x.x";
        final String getPortTest = "1234";
        final String getProtocolTest = "https";

        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + HOST, getHostTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PORT, getPortTest);
        client.setKVValue(testSystemId + SLASH + testApplicationId + SLASH + PROTOCOL, getProtocolTest);

        given().contentType(JSON_HEADER).get(
                SYSTEMS_PATH + testSystemIdInvalid + APPLICATIONS_PATH + testApplicationId + APP_URL).then().assertThat().statusCode(
                HTTP_NOT_FOUND_STATUS_CODE);

        given().contentType(JSON_HEADER).get(
                SYSTEMS_PATH + testSystemId + APPLICATIONS_PATH + testApplicationIdInvalid + APP_URL).then().assertThat().statusCode(
                HTTP_NOT_FOUND_STATUS_CODE);

        given().contentType(JSON_HEADER).get(
                SYSTEMS_PATH + testSystemIdInvalid + APPLICATIONS_PATH + testApplicationIdInvalid + APP_URL).then().assertThat().statusCode(
                HTTP_NOT_FOUND_STATUS_CODE);
    }

    public static boolean validateResponse(String systemId,
                                           String applicationId,
                                           String parameterToTest,
                                           String expectedValue) {
        final String key = systemId + "/" + applicationId;
        final Response<List<GetValue>> getKVValues = client.getKVValues(key);

        if (getKVValues != null && getKVValues.getValue() != null) {
            final String fullKey = systemId + SLASH + applicationId + SLASH + parameterToTest;
            for (GetValue getValue : getKVValues.getValue()) {
                if (fullKey.equals(getValue.getKey()) && getValue.getDecodedValue() != null) {
                    return Objects.equals(getValue.getDecodedValue(), expectedValue);
                }
            }
        }

        return false;
    }

    private static ExternalAppLaunchConfig createEaleTestResource(String testHost,
                                                                  String testPort,
                                                                  String testProtocol) {
        final ExternalAppLaunchConfig testConfig = new ExternalAppLaunchConfig();
        testConfig.setHost(testHost);
        testConfig.setPort(testPort);
        testConfig.setProtocol(testProtocol);
        return testConfig;
    }

    private static Callable<Boolean> checkForDeployed(String deploymentPath) {
        return () -> {
            final CompletableFuture<Boolean> delayedFuture = new CompletableFuture<>();

            SCHEDULER.schedule(() -> {
                final String[] command = {"sh",
                        "-c",
                        "[ -f " + deploymentPath + " ] && echo exists || echo not exists"
                };

                boolean fileExists = false;

                try {
                    LOGGER.info("Purging stale EALE deployment.");
                    JbossContainer.deleteStaleDeployments(ARTEFACT_ID, jbossContainer);
                    final String result = jbossContainer.execInContainer(command).getStdout();
                    fileExists = "exists\n".equals(result);
                    LOGGER.info("Eale Deployed in testcontainer: {}", fileExists);

                } catch (Exception e) {
                    LOGGER.error("Failed to deploy EALE.", e);
                }

                delayedFuture.complete(fileExists);
            }, 1, TimeUnit.SECONDS);

            return delayedFuture.get();
        };
    }

    private static String getPropertyValueFromFile() {
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
            return scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
        } catch (IOException e) {
            LOGGER.error("Error reading properties file", e);
            return "error";
        }
    }
}
