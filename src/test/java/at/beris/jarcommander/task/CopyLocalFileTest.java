/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.JFileFactory;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.LocalPath;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jarcommander.filesystem.BlockCopy.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class CopyLocalFileTest {
    private static List<JFile> fileList;
    private static List<JFile> sourceFiles;
    private static JPath targetPath;

    private static MessageDigest messageDigest;

    @BeforeClass
    public static void setUp() throws Exception {
        sourceFiles = new ArrayList<>();
        sourceFiles.add(JFileFactory.newInstance(new File("testfile1.txt")));
        sourceFiles.add(JFileFactory.newInstance(new File("testdir")));

        targetPath = new LocalPath(new File("testtargetdir").toPath());

        messageDigest = MessageDigest.getInstance("SHA");

        createFiles();
    }

    @AfterClass
    public static void tearDown() {
        for (JFile file : sourceFiles) {
            file.delete();
        }
    }

    @After
    public void afterTest() {
        targetPath.toFile().delete();
    }

    @Test
    public void copyFilesRecursive() throws Exception {
        CopyTaskListener copyTaskListener = Mockito.mock(CopyTaskListener.class);

        CopyTask copyTask = new CopyTask(sourceFiles, targetPath, copyTaskListener);
        copyTask.execute();
        copyTask.get();

        assertFiles();
    }

    @Test
    public void copyFilesTargetExists() throws Exception {
        CopyTaskListener copyTaskListener = Mockito.mock(CopyTaskListener.class);

        List<String> expectedFilenames = createExpectedFilenames();

        for (JFile file : fileList) {
            JFile targetFile = JFileFactory.newInstance(new File(targetPath.toString(), file.toString()));

            if (targetFile.isDirectory())
                targetFile.mkdirs();
            else {
                targetFile.getParentFile().mkdirs();
                Files.createFile(((File) targetFile.getBaseObject()).toPath());
            }
        }

        CopyTask copyTask = new CopyTask(sourceFiles, targetPath, copyTaskListener);
        copyTask.execute();
        copyTask.get();

        ArgumentCaptor<JFile> fileArgumentCaptor = ArgumentCaptor.forClass(JFile.class);
        Mockito.verify(copyTaskListener, times(expectedFilenames.size())).fileExists(fileArgumentCaptor.capture());

        List<JFile> capturedFiles = fileArgumentCaptor.getAllValues();

        List<String> actualFilenames = new ArrayList<>();
        for (JFile file : capturedFiles) {
            actualFilenames.add(file.toString());
        }

        assertEquals(expectedFilenames.size(), actualFilenames.size());
        assertTrue(actualFilenames.containsAll(expectedFilenames));
    }

    private List<String> createExpectedFilenames() {
        List<String> expectedFilenames = new ArrayList<>();
        expectedFilenames.add("testtargetdir/testfile1.txt");
        expectedFilenames.add("testtargetdir/testdir");
        expectedFilenames.add("testtargetdir/testdir/testfile2.txt");
        expectedFilenames.add("testtargetdir/testdir/testfile3.txt");
        expectedFilenames.add("testtargetdir/testdir/subdir");
        expectedFilenames.add("testtargetdir/testdir/subdir/testfile4.txt");
        expectedFilenames.add("testtargetdir/testdir/subdir/testfile5.txt");
        return expectedFilenames;
    }

    private static void createFiles() throws IOException {
        fileList = new ArrayList<>();
        fileList.add(JFileFactory.newInstance(new File("testfile1.txt")));
        fileList.add(JFileFactory.newInstance(new File("testdir" + File.separator + "testfile2.txt")));
        fileList.add(JFileFactory.newInstance(new File("testdir" + File.separator + "testfile3.txt")));
        fileList.add(JFileFactory.newInstance(new File("testdir" + File.separator + "subdir" + File.separator + "testfile4.txt")));
        fileList.add(JFileFactory.newInstance(new File("testdir" + File.separator + "subdir" + File.separator + "testfile5.txt")));

        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        for (JFile file : fileList) {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            index++;
            while (dataString.length() < COPY_BUFFER_SIZE * index + 10)
                dataString.append(testString);

            JPath path = file.toPath();
            Files.write((Path) path.getBaseObject(), dataString.toString().getBytes());
        }
    }

    private void assertFiles() throws IOException {
        for (JFile sourceFile : fileList) {
            JFile targetFile = JFileFactory.newInstance(new File(targetPath.toString(), sourceFile.toString()));
            if (targetFile.exists()) {
                messageDigest.reset();
                messageDigest.update(Files.readAllBytes((Path) sourceFile.toPath().getBaseObject()));
                byte[] sourceHashBytes = messageDigest.digest();

                messageDigest.reset();
                messageDigest.update(Files.readAllBytes((Path) targetFile.toPath().getBaseObject()));
                byte[] targetHashBytes = messageDigest.digest();

                assertArrayEquals(sourceHashBytes, targetHashBytes);

            } else {
                fail("targetFile doesn't exist.");
            }
        }
    }
}