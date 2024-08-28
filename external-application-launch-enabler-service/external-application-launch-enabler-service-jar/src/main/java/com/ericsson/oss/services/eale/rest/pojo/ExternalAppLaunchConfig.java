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

import java.io.Serializable;
import java.util.Objects;

public class ExternalAppLaunchConfig implements Serializable {

    private static final long serialVersionUID = 4552563679666296526L;

    private String host;
    private String port;
    private String protocol;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public String getPort() {
	return port;
    }

    public void setPort(String port) {
	this.port = port;
    }

    public String getProtocol() {
	return protocol;
    }

    public void setProtocol(String protocol) {
	this.protocol = protocol;
    }

    @Override
    public int hashCode() {
	return Objects.hash(host, port, protocol);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	ExternalAppLaunchConfig other = (ExternalAppLaunchConfig) obj;
	return Objects.equals(host, other.host) && Objects.equals(port, other.port)
		&& Objects.equals(protocol, other.protocol);
    }

    @Override
    public String toString() {
	return "ExternalAppLaunchConfig[" + "host=" + host + ",port=" + port + ",protocol=" + protocol + "]";
    }
}
