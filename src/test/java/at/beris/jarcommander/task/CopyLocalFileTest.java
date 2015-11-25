/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.path.IPath;
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
import java.util.ArrayList;
import java.util.List;

import static at.beris.jarcommander.filesystem.LocalBlockCopy.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class CopyLocalFileTest {
    private static final String TARGET_DIRECTORY = "testtargetdir";

    private static List<IFile> fileList;
    private static List<IFile> sourceFiles;
    private static IPath targetPath;

    @BeforeClass
    public static void setUp() throws Exception {
        sourceFiles = new ArrayList<>();
        sourceFiles.add(FileFactory.newInstance(new File("testfile1.txt")));
        sourceFiles.add(FileFactory.newInstance(new File("testdir")));

        targetPath = new LocalPath(new File(TARGET_DIRECTORY).toPath());

        createFiles();
    }

    @AfterClass
    public static void tearDown() {
        for (IFile file : sourceFiles) {
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

        for (IFile file : fileList) {
            IFile targetFile = FileFactory.newInstance(new File(targetPath.toString(), file.toString()));

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

        ArgumentCaptor<IFile> fileArgumentCaptor = ArgumentCaptor.forClass(IFile.class);
        Mockito.verify(copyTaskListener, times(expectedFilenames.size())).fileExists(fileArgumentCaptor.capture());

        List<IFile> capturedFiles = fileArgumentCaptor.getAllValues();

        List<String> actualFilenames = new ArrayList<>();
        for (IFile file : capturedFiles) {
            actualFilenames.add(file.toString());
        }

        assertEquals(expectedFilenames.size(), actualFilenames.size());
        assertTrue(actualFilenames.containsAll(expectedFilenames));
    }

    private List<String> createExpectedFilenames() {
        List<String> expectedFilenames = new ArrayList<>();
        expectedFilenames.add(TARGET_DIRECTORY + "/testfile1.txt");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir/testfile2.txt");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir/testfile3.txt");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir/subdir");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir/subdir/testfile4.txt");
        expectedFilenames.add(TARGET_DIRECTORY + "/testdir/subdir/testfile5.txt");
        return expectedFilenames;
    }

    private static void createFiles() throws IOException {
        fileList = new ArrayList<>();
        fileList.add(FileFactory.newInstance(new File("testfile1.txt")));
        fileList.add(FileFactory.newInstance(new File("testdir" + File.separator + "testfile2.txt")));
        fileList.add(FileFactory.newInstance(new File("testdir" + File.separator + "testfile3.txt")));
        fileList.add(FileFactory.newInstance(new File("testdir" + File.separator + "subdir" + File.separator + "testfile4.txt")));
        fileList.add(FileFactory.newInstance(new File("testdir" + File.separator + "subdir" + File.separator + "testfile5.txt")));

        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        for (IFile file : fileList) {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            index++;
            while (dataString.length() < COPY_BUFFER_SIZE * index + 10)
                dataString.append(testString);

            IPath path = file.toPath();
            Files.write((Path) path.getBaseObject(), dataString.toString().getBytes());
        }
    }

    private void assertFiles() throws IOException {
        for (IFile sourceFile : fileList) {
            IFile targetFile = FileFactory.newInstance(new File(targetPath.toString(), sourceFile.toString()));
            if (targetFile.exists()) {
                assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
            } else {
                fail("targetFile doesn't exist.");
            }
        }
    }
}