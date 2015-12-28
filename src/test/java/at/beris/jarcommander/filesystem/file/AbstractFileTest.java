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
import at.beris.jarcommander.helper.SiteManager;
import at.beris.jarcommander.model.SiteModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

import static at.beris.jarcommander.ApplicationContext.HOME_DIRECTORY;
import static at.beris.jarcommander.filesystem.IBlockCopy.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public abstract class AbstractFileTest {
    public static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static int TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;
    public static Instant TEST_SOURCE_FILE_LAST_MODIFIED = Instant.now();

    public static String TEST_SOURCE_DIRECTORY_NAME = "testdirectory";
    public static String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory";

    public static final String TEST_SITES_FILENAME = HOME_DIRECTORY + File.separator + "test" + File.separator + "sites.xml";

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest";

    protected static FileFactory fileFactory;

    public static void initTest() throws Exception {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TEST_SITES_FILENAME).toPath()));
        fileFactory = new FileFactory();
    }

    public static LocalFile createLocalSourceFile() throws IOException {
        File file = new File(TEST_SOURCE_FILE_NAME);

        StringBuilder dataString = new StringBuilder("t");

        while (dataString.length() < TEST_SOURCE_FILE_SIZE)
            dataString.append("t");

        Files.write(file.toPath(), dataString.toString().getBytes());
        Files.setLastModifiedTime(file.toPath(), FileTime.from(TEST_SOURCE_FILE_LAST_MODIFIED));

        return (LocalFile) fileFactory.newInstance(file);
    }

    public static SshFile createSshFile(SshContext sshContext, String path) throws IOException {
        LocalFile localFile = createLocalSourceFile();
        SshFile sshFile = new SshFile(sshContext, path, fileFactory);
        CopyListener copyListener = Mockito.mock(CopyListener.class);

        localFile.copy(sshFile, copyListener);
        localFile.delete();

        return new SshFile(sshContext, path, fileFactory);
    }

    public void assertCopyListener(CopyListener copyListener) {
        ArgumentCaptor<Long> bytesCopiedBlockArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> bytesCopiedTotalArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> fileSizeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(copyListener, times(2)).afterBlockCopied(fileSizeArgumentCaptor.capture(), bytesCopiedBlockArgumentCaptor.capture(), bytesCopiedTotalArgumentCaptor.capture());

        List<Long> bytesCopiedBlockList = bytesCopiedBlockArgumentCaptor.getAllValues();
        List<Long> bytesCopiedTotalList = bytesCopiedTotalArgumentCaptor.getAllValues();

        assertEquals(2, bytesCopiedBlockList.size());
        assertEquals(COPY_BUFFER_SIZE, bytesCopiedBlockList.get(0).intValue());
        assertEquals(COPY_BUFFER_SIZE, bytesCopiedTotalList.get(0).intValue());

        assertEquals(TEST_SOURCE_FILE_SIZE % COPY_BUFFER_SIZE, bytesCopiedBlockList.get(1).intValue());
        assertEquals(TEST_SOURCE_FILE_SIZE, bytesCopiedTotalList.get(1).intValue());
    }

    public void copyFileToLocalHostSuccessfully(IFile sourceFile, IFile targetFile) {
        CopyListener copyListener = Mockito.mock(CopyListener.class);
        Mockito.when(copyListener.interrupt()).thenReturn(false);

        try {
            sourceFile.copy(targetFile, copyListener);
            assertCopyListener(copyListener);
            assertEquals(sourceFile.getSize(), targetFile.getSize());
        } catch (IOException e) {
            fail();
        } finally {
            targetFile.delete();
        }
    }

    public void copyFileToLocalHostCancelled(IFile sourceFile, IFile targetFile) {
        CopyListener copyListener = Mockito.mock(CopyListener.class);
        Mockito.when(copyListener.interrupt()).thenReturn(true);

        try {
            sourceFile.copy(targetFile, copyListener);
            assertTrue(targetFile.getSize() > 0);
            assertTrue(sourceFile.getSize() != targetFile.getSize());
        } catch (IOException e) {
            fail();
        } finally {
            targetFile.delete();
        }
    }

    private static SiteModel getSshSiteModel() {
        SiteManager siteManager = new SiteManager(TEST_SITES_FILENAME);
        siteManager.load();

        return siteManager.getSiteListModel().getElementAt(0);
    }

    public static SshContext createSshContext() {
        SiteModel sshSite = getSshSiteModel();
        SshContext sshContext = new SshContext();
        sshContext.setHost(sshSite.getHostname());
        sshContext.setPort(sshSite.getPortNumber());
        sshContext.setUsername(sshSite.getUsername());
        sshContext.setPassword(String.valueOf(sshSite.getPassword()));

        sshContext.init();
        sshContext.connect();

        return sshContext;
    }
}
