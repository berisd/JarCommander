/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.file.CopyListener;
import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.filesystem.file.IFile;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTask extends SwingWorker<Void, Integer> implements CopyListener {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);

    private List<IFile> sourceList;
    private IFile targetFile;
    private long bytesTotal = 0L;
    private long bytesCopied = 0L;
    private long totalCountFiles = 0;
    private CopyTaskListener listener;

    public CopyTask(List<IFile> sourceList, IFile targetFile, CopyTaskListener listener) {
        this.sourceList = sourceList;
        this.targetFile = targetFile;
        this.listener = listener;

        listener.setCurrentProgressBar(0);
        listener.setAllProgressBar(0);
    }

    @Override
    public Void doInBackground() throws Exception {
        LOGGER.debug("doInBackground");
        try {
            for (IFile sourceFile : sourceList) {
                if (sourceFile.getName().equals(".."))
                    continue;
                retrieveFileInfo(sourceFile);
            }

            for (IFile sourceFile : sourceList) {
                if (isCancelled())
                    break;
                if (sourceFile.getName().equals(".."))
                    continue;
                IFile targetFile = FileManager.newFile(this.targetFile, sourceFile.getUrl());
                copyFiles(sourceFile, targetFile);
            }
        } catch (Exception ex) {
            logException(ex);
        }

        return null;
    }

    @Override
    public void process(List<Integer> chunks) {
        for (int i : chunks) {
            listener.setCurrentProgressBar(i);
        }
    }

    @Override
    public void done() {
        LOGGER.info("done");
        setProgress(100);
        listener.done();
    }

    private void retrieveFileInfo(IFile sourceFile) {
        if (sourceFile.getName().equals(".."))
            return;

        List<IFile> fileList;
        if (sourceFile.isDirectory()) {
            try {
                fileList = sourceFile.list();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            fileList = Collections.singletonList(sourceFile);
        }

        for (IFile file : fileList) {
            if (file.getName().equals(".."))
                continue;
            if (file.isDirectory()) {
                retrieveFileInfo(file);
            } else {
                bytesTotal += file.getSize();
                totalCountFiles++;
            }
        }
    }

    private void copyFiles(IFile sourceFile, IFile targetFile) throws IOException {
        try {
            sourceFile.copy(targetFile, this);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            int result = listener.showError(ex.getMessage());
            if (result == JOptionPane.CANCEL_OPTION)
                cancel(true);
        } catch (Exception ex) {
            Application.logException(ex);
        } finally {
            if (isCancelled() && sourceFile.getSize() != targetFile.getSize()) {
                targetFile.delete();
            }
        }

        publish(100);
    }

    @Override
    public void startCopyFile(String fileName, long currentFileNumber) {
        listener.startCopyFile(fileName, currentFileNumber, totalCountFiles);
    }

    @Override
    public void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal) {
        bytesCopied += bytesCopiedBlock;

        listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
        publish((int) (bytesCopiedTotal * 100 / fileSize));
    }

    @Override
    public boolean interrupt() {
        return isCancelled();
    }

    @Override
    public void fileExists(IFile file) {
        int result = listener.fileExists(file);
        switch (result) {
            case JOptionPane.NO_OPTION:
                bytesCopied += file.getSize();
                listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
                return;
            case JOptionPane.CANCEL_OPTION:
                cancel(true);
            default:
                file.delete();
        }
    }
}
