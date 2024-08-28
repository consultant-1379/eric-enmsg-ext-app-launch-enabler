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
package com.ericsson.oss.services.eale.ejb.service;
import java.io.File;
import java.io.IOException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class FileHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    public void copyAppConfigtoLaunchDirectory(final File sourceDirectory, final String[] appNames, final File applicationLaunchDir){
        // Getting the supplied source path so the application name can be added to the path
        final String sourceDirName = sourceDirectory.getPath();
        for (String appName : appNames) {
            try {
                final File externalApplicationDirectory = new File(String.format("%s/%s", sourceDirName, appName));
                final File applicationLaunchDirectory = new File(String.format("%s/%s", applicationLaunchDir, appName));
                FileUtils.copyDirectory(externalApplicationDirectory, applicationLaunchDirectory, false);
            } catch (IOException e) {
                LOGGER.error("Error copying {} application launch config to the launch directory: {}", appName, e);
            }
        }

    }
}