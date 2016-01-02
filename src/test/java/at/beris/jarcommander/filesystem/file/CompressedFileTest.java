/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.path.IPath;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompressedFileTest extends AbstractFileTest {

    public static final String ZIP_FILENAME = "test.zip";

    private static ArchiveStreamFactory archiveStreamFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        initTest();
        FileOutputStream fileOutputStream = new FileOutputStream(ZIP_FILENAME);
        archiveStreamFactory = new ArchiveStreamFactory();
        ArchiveOutputStream archiveOutputStream = archiveStreamFactory.createArchiveOutputStream(ArchiveStreamFactory.ZIP, fileOutputStream);
        addArchiveEntries(archiveOutputStream);
        archiveOutputStream.close();
    }

    @AfterClass
    public static void tearDown() {
        try {
            Files.delete(new File(ZIP_FILENAME).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readFilesInRootDirectory() {
//        List<IFile> fileList = fileManager.createFileTreeFromArchive(new File(ZIP_FILENAME));
//        assertEquals(3, fileList.size());
    }

    @Test
    public void navigatePathDown() {
        List<IFile> subDirFileList = getFilesInSubDirectory();
        assertEquals(2, subDirFileList.size());
    }

    @Test
    public void navigatePathUp() {
        List<IFile> subDirFileList = getFilesInSubDirectory();
        IPath parentDir = subDirFileList.get(0).toPath().getParent();
//        assertEquals(3, parentDir.getEntries().size());
    }

    private List<IFile> getFilesInSubDirectory() {
        return new ArrayList<>();
//        List<IFile> fileList = fileManager.createFileTreeFromArchive(new File(ZIP_FILENAME));
//        return fileList.get(0).toPath().getEntries();
    }

    private static void addArchiveEntries(ArchiveOutputStream archiveOutputStream) throws IOException {
        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry("testdir/"));
        archiveOutputStream.closeArchiveEntry();

        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry("testdir/test.xml"));
        archiveOutputStream.write("teststring".getBytes(), 0, "teststring".length());
        archiveOutputStream.closeArchiveEntry();

        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry("test2.xml"));
        archiveOutputStream.write("teststring".getBytes(), 0, "teststring".length());
        archiveOutputStream.closeArchiveEntry();

        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry("readme.txt"));
        archiveOutputStream.write("readmereadme".getBytes(), 0, "readmereadme".length());
        archiveOutputStream.closeArchiveEntry();
    }
}