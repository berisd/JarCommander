/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.SshContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class SshFileTest extends AbstractFileTest {

    public static final String SSH_SOURCE_FILE_PATH = SSH_HOME_DIRECTORY + "/" + TEST_SOURCE_FILE_NAME;

    private static SshContext sshContext;
    private static SshFile sourceFile;

    @BeforeClass
    public static void setUp() throws Exception {
        initTest();
        sshContext = createSshContext();
        sourceFile = createSshFile(sshContext, SSH_SOURCE_FILE_PATH);
    }

    @Test
    public void copyFileToLocalHostSuccessfully() {
        copyFileToLocalHostSuccessfully(new SshFile(sshContext, SSH_SOURCE_FILE_PATH, fileFactory), fileFactory.newInstance(new File(TEST_TARGET_FILE_NAME)));
    }

    @Test
    public void copyFileToLocalHostCancelled() {
        super.copyFileToLocalHostCancelled(new SshFile(sshContext, SSH_SOURCE_FILE_PATH, fileFactory), fileFactory.newInstance(new File(TEST_TARGET_FILE_NAME)));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sourceFile.delete();
        sshContext.disconnect();
    }
}
