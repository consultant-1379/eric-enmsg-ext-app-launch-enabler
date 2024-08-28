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
package com.ericsson.oss.itpf.sdk.security.accesscontrol.classic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecuritySubject;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityTarget;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Implementation of EAccessControl interface for testing.
 */
public class EAccessControlBypassAllImpl extends EAccessControlImpl implements EAccessControl {
    private final ContextServiceBean contextService = new ContextServiceBean();
    public static Logger logger = LoggerFactory.getLogger(
            EAccessControlBypassAllImpl.class);

    @Override
    public ESecuritySubject getAuthUserSubject() throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: getAuthUserSubject called.");
        logger.warn("************************************************************");

        // get userid from currentAuthUser file in tmpDir
        String toruser = "bypass_usr";
        String useridFile = "bypass_usr_id";
        String tmpDir = "bypass_tmp_dir";
        try {
            tmpDir = System.getProperty("java.io.tmpdir");
            useridFile = String.format("%s/currentAuthUser", tmpDir);
            toruser = new String(Files.readAllBytes(Paths.get(useridFile)));
        } catch (NullPointerException npe) {
            logger.error("Something was null, tmpDir:{}, useridFile:{}, toruser:{}", tmpDir, useridFile, toruser);
        } catch
        (IOException ioe) {
            logger.error("Error reading {}, Details: {}", useridFile, ioe.getMessage());
            toruser = "ioerror";
        }
        logger.info("AccessControlBypassAllImpl: getAuthUserSubject: toruser is <{}>", toruser);
        return new ESecuritySubject(toruser);
    }

    @Override public void setAuthUserSubject(String s) {
        final String userName = "bypass_username";
        this.contextService.setContextValue("X-Tor-UserID", userName);
    }

    @Override public boolean isUserInRole(String s) {
        return false;
    }

    @Override public Set<ESecurityTarget> getTargetsForSubject() {
        return null;
    }

    @Override public Set<ESecurityTarget> getTargetsForSubject(ESecuritySubject eSecuritySubject) {
        return null;
    }

    @Override public boolean checkUserExists(ESecuritySubject eSecuritySubject) {
        return false;
    }

    @Override
    public Map<ESecurityResource, Set<ESecurityAction>> getActionsForResources(ESecuritySubject eSecuritySubject,
                                                                               Set<ESecurityResource> set) {
        return null;
    }

    @Override public Map<ESecurityResource, Set<ESecurityAction>> getActionsForResources(Set<ESecurityResource> set) {
        return null;
    }

    public boolean isAuthorized(final ESecuritySubject secSubject,
                                final ESecurityResource secResource,
                                final ESecurityAction secAction,
                                final EPredefinedRole[] roles) throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 1 called");
        logger.warn("************************************************************");
        return true;
    }

    public boolean isAuthorized(final ESecuritySubject secSubject,
                                final ESecurityResource secResource,
                                final ESecurityAction secAction) throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 2 called");
        logger.warn("************************************************************");
        return true;
    }

    public boolean isAuthorized(final ESecurityResource secResource,
                                final ESecurityAction secAction,
                                final EPredefinedRole[] roles) throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 3 called");
        logger.warn("************************************************************");
        return true;
    }

    public boolean isAuthorized(final ESecurityResource secResource,
                                final ESecurityAction secAction) throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 4 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(ESecuritySubject eSecuritySubject,
                                ESecurityResource eSecurityResource,
                                ESecurityAction eSecurityAction,
                                Set<ESecurityTarget> set) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 5 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(ESecurityResource eSecurityResource,
                                ESecurityAction eSecurityAction,
                                Set<ESecurityTarget> set) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 6 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(ESecuritySubject eSecuritySubject,
                                Set<ESecurityTarget> set) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 7 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override public boolean isAuthorized(Set<ESecurityTarget> set) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 8 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(ESecuritySubject eSecuritySubject,
                                ESecurityTarget eSecurityTarget) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 9 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override public boolean isAuthorized(ESecurityTarget eSecurityTarget) throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 10 called");
        logger.warn("************************************************************");
        return true;
    }

}