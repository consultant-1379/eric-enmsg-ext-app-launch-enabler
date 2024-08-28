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
package com.ericsson.oss.services.eale.datastore.consul;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

@RunWith(MockitoJUnitRunner.class)
public class ConsulRetriableCommandTest {

    private static final String CONSUL_KEY_DIR = "testDirectory/";
    private static final String CONSUL_KEY = CONSUL_KEY_DIR + "key";
    private static final String CONSUL_KV_URL = "/v1/kv/";
    private static final String CONSUL_KV_URL_FOR_TEST = CONSUL_KV_URL + CONSUL_KEY;
    private static final String CONSUL_KV_URL_FOR_TEST_RECURSE = CONSUL_KV_URL_FOR_TEST + "?recurse";
    private static final String VALUE = "value";
    private static final String ANOTHER_VALUE = "anotherValue";
    private static final String ERROR_NO_KEY_EXISTS = "Error! No key exists";
    private static final String ERROR_NO_CONSUL_LEADER = "rpc error: No cluster leader";
    private static final int ONE_ATTEMPT = 1;
    private static final int NUMBER_OF_ATTEMPTS = 7;

    private ConsulRetriableCommand consulRetriableCommand;

    static {
	System.setProperty("consul.client.agent.host", "localhost");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @ClassRule
    public static final WireMockClassRule wireMockRule = new WireMockClassRule(
	    options().port(8500).bindAddress("localhost"));

    @Rule
    public WireMockClassRule wiremock = wireMockRule;

    @Before
    public void setUp() {
	consulRetriableCommand = ConsulRetriableCommand.builder().attempts(NUMBER_OF_ATTEMPTS).waitInterval(1).build();
    }

    @Test
    public void builderWhenAttemptsLessThan1ThenThrowsIllegalArgumentException() {
	expectedException.expect(IllegalArgumentException.class);
	ConsulRetriableCommand.builder().attempts(0).build();
    }

    @Test
    public void builderWhenWaitIntervalLessThan0ThenThrowsIllegalArgumentException() {
	expectedException.expect(IllegalArgumentException.class);
	ConsulRetriableCommand.builder().waitInterval(-1).build();
    }

    @Test
    public void getKVValueWhenKeyExistsThenReturnsKeyValueAndDoesNotRetry() throws ServerErrorException {
	stubFor(get(urlEqualTo(CONSUL_KV_URL_FOR_TEST))
		.willReturn(ok().withBody("[{\"Value\": \"" + Base64.encodeBase64String(VALUE.getBytes()) + "\"}]")));
	final String value = consulRetriableCommand.getKVValue(CONSUL_KEY);
	assertEquals(VALUE, value);
	verify(exactly(ONE_ATTEMPT), getRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    @Test
    public void getKVValuesWhenKeyExistsThenReturnsKeyValueAndDoesNotRetry() throws ServerErrorException {
	stubFor(get(urlEqualTo(CONSUL_KV_URL_FOR_TEST_RECURSE)).willReturn(ok()
		.withBody("[{\"Key\":\"test/extapp/host\", \"Value\": \"" + Base64.encodeBase64String(VALUE.getBytes())
			+ "\"},{\"Key\":\"test/extapp/port\", \"Value\": \"" + Base64.encodeBase64String(ANOTHER_VALUE.getBytes()) + "\"}]")));
	final Map<String, String> value = consulRetriableCommand.getKVValues(CONSUL_KEY);
	assertEquals(2, value.size());
	assertEquals(true, value.containsValue(VALUE));
	assertEquals(true, value.containsValue(ANOTHER_VALUE));
	verify(exactly(ONE_ATTEMPT), getRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST_RECURSE)));
    }

    @Test
    public void getKVValueWhenKeyDoesNotExistThenReturnsNullAndDoesNotRetry() throws ServerErrorException {
	stubFor(get(urlEqualTo(CONSUL_KV_URL_FOR_TEST))
		.willReturn(aResponse().withStatus(404).withBody(ERROR_NO_KEY_EXISTS)));
	final String value = consulRetriableCommand.getKVValue(CONSUL_KEY);
	assertNull(value);
	verify(exactly(ONE_ATTEMPT), getRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    @Test
    public void getKVValueWhenNoConsulLeaderThenDoesRetryAndThrowsServerErrorException() throws ServerErrorException {
	expectedException.expect(ServerErrorException.class);
	stubFor(get(urlEqualTo(CONSUL_KV_URL_FOR_TEST)).willReturn(noConsulLeader()));
	consulRetriableCommand.getKVValue(CONSUL_KEY);
	verify(exactly(NUMBER_OF_ATTEMPTS), getRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    @Test
    public void setKVValueWhenResponseFromConsulThatTheKeyIsSetIsTrueThenDoesNotRetry() throws ServerErrorException {
	stubFor(put(urlEqualTo(CONSUL_KV_URL_FOR_TEST)).willReturn(ok().withBody("true")));
	consulRetriableCommand.setKVValue(CONSUL_KEY, VALUE);
	verify(exactly(ONE_ATTEMPT), putRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    @Test
    public void setKVValueWhenKeyIsNotSetThenDoesRetryAndThrowsServerErrorException() throws ServerErrorException {
	expectedException.expect(ServerErrorException.class);
	stubFor(put(urlEqualTo(CONSUL_KV_URL_FOR_TEST)).willReturn(ok().withBody("false")));
	consulRetriableCommand.setKVValue(CONSUL_KEY, VALUE);
	verify(exactly(NUMBER_OF_ATTEMPTS), putRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    @Test
    public void setKVValueWhenNoConsulLeaderThenDoesRetryAndThrowsServerErrorException() throws ServerErrorException {
	expectedException.expect(ServerErrorException.class);
	stubFor(put(urlEqualTo(CONSUL_KV_URL_FOR_TEST)).willReturn(noConsulLeader()));
	consulRetriableCommand.setKVValue(CONSUL_KEY, VALUE);
	verify(exactly(NUMBER_OF_ATTEMPTS), putRequestedFor(urlEqualTo(CONSUL_KV_URL_FOR_TEST)));
    }

    private ResponseDefinitionBuilder noConsulLeader() {
	return aResponse().withStatus(500).withBody(ERROR_NO_CONSUL_LEADER);
    }

}
