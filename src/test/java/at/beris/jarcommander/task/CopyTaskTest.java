/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

public class CopyTaskTest {
    private static List<VirtualFile> sourceFiles;

    @Test
    public void copyFiles() throws Exception {
        CopyTaskListener copyTaskListener = Mockito.mock(CopyTaskListener.class);
        VirtualFile targetFile = createFileMock();
        List<VirtualFile> sourceFiles = createSourceFileMockList();

        CopyTask copyTask = new CopyTask(sourceFiles, targetFile, copyTaskListener);
        copyTask.execute();
        copyTask.get();

        for (VirtualFile sourceFile : sourceFiles)
            Mockito.verify(sourceFile, times(1)).copy(any(VirtualFile.class), any(FileOperationListener.class));
    }

    private List<VirtualFile> createSourceFileMockList() throws IOException {
        sourceFiles = new ArrayList<>();
        sourceFiles.add(createFileMock());
        sourceFiles.add(createFileMock());
        return sourceFiles;
    }

    private VirtualFile createFileMock() throws IOException {
        VirtualFile file = Mockito.mock(VirtualFile.class);
        Mockito.when(file.getName()).thenReturn("filename");
        Mockito.when(file.getSize()).thenReturn(1L);
        try {
            Mockito.when(file.getUrl()).thenReturn(new java.io.File("filename").toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}