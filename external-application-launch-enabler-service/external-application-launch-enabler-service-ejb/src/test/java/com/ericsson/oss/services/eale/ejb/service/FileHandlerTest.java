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

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import  org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.rules.TemporaryFolder;


@RunWith(MockitoJUnitRunner.class)
public class FileHandlerTest {

    public static final String ENIQ_APPS_DIRECTORY = "/ericsson/ERICbonetanstandaloneui_CXP9034896/apps";

    @InjectMocks
    private FileHandler fileHandler;

    @InjectMocks
    private FileUtils fileUtils;

    @Rule
    public TemporaryFolder sourceDir = new TemporaryFolder();
    @Rule
    public TemporaryFolder destDir = new TemporaryFolder();

    @Test
    public void testCopyAppConfigtoLaunchDirectorySuccess() throws IOException {

        File eniqDirectory = sourceDir.newFolder("ericsson","ERICbonetanstandaloneui_CXP9034896","apps");
        sourceDir.newFolder("ericsson","ERICbonetanstandaloneui_CXP9034896","apps", "bilaunchpad");
        sourceDir.newFolder("ericsson","ERICbonetanstandaloneui_CXP9034896","apps", "centralmanagementconsole");
        sourceDir.newFile(ENIQ_APPS_DIRECTORY + File.separator + "centralmanagementconsole"+ File.separator + "centralmanagementconsole.json");
        sourceDir.newFile(ENIQ_APPS_DIRECTORY+ File.separator + "bilaunchpad" + File.separator + "bilaunchpad.json");

        File app_launch_directory = destDir.newFolder("ericsson","tor","data","apps");
        // Store the initial file count in the destination directory
        int initialFileCount = Objects.requireNonNull(app_launch_directory.listFiles()).length;

        try {
             fileHandler.copyAppConfigtoLaunchDirectory(eniqDirectory, new String[]{"bilaunchpad", "centralmanagementconsole"}, app_launch_directory);
         }catch (Exception e){
             throw new AssertionError("Expected no exception, but got: " + e);
         }

        int finalFileCount = Objects.requireNonNull(app_launch_directory.listFiles()).length;
        assertEquals(initialFileCount + 2, finalFileCount);
    }

}


