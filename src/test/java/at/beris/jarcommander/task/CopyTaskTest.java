/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.filesystem.file.CopyListener;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.path.IPath;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

public class CopyTaskTest {
    private static List<IFile> sourceFiles;

    @Test
    public void copyFiles() throws Exception {
        CopyTaskListener copyTaskListener = Mockito.mock(CopyTaskListener.class);
        IPath targetPath = createPathMock();
        List<IFile> sourceFiles = createSourceFileMockList();
        FileFactory fileFactory = createFileFactoryMock();

        CopyTask copyTask = new CopyTask(sourceFiles, targetPath, copyTaskListener, fileFactory);
        copyTask.execute();
        copyTask.get();

        for (IFile sourceFile : sourceFiles)
            Mockito.verify(sourceFile, times(1)).copy(any(IFile.class), any(CopyListener.class));
    }

    private FileFactory createFileFactoryMock() {
        FileFactory fileFactory = Mockito.mock(FileFactory.class);
        Mockito.when(fileFactory.newInstance(any(IFile.class), any(String.class))).thenReturn(Mockito.mock(IFile.class));
        return fileFactory;
    }

    private IPath createPathMock() {
        IPath path = Mockito.mock(IPath.class);
        return path;
    }

    private List<IFile> createSourceFileMockList() {
        sourceFiles = new ArrayList<>();
        sourceFiles.add(createFileMock());
        sourceFiles.add(createFileMock());
        return sourceFiles;
    }

    private IFile createFileMock() {
        IFile file = Mockito.mock(IFile.class);
        Mockito.when(file.getName()).thenReturn("filename");
        Mockito.when(file.getSize()).thenReturn(1L);
        return file;
    }
}