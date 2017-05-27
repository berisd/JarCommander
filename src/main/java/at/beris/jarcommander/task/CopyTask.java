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
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTask extends SwingWorker<Void, Integer> implements FileOperationListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(CopyTask.class);

    private List<VirtualFile> sourceList;
    private VirtualFile targetFile;
    private long bytesTotal = 0L;
    private long bytesCopied = 0L;
    private long totalCountFiles = 0;
    private CopyTaskListener listener;

    public CopyTask(List<VirtualFile> sourceList, VirtualFile targetFile, CopyTaskListener listener) {
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
            for (VirtualFile sourceFile : sourceList) {
                if (sourceFile.getName().equals(".."))
                    continue;
                retrieveFileInfo(sourceFile);
            }

            for (VirtualFile sourceFile : sourceList) {
                if (isCancelled())
                    break;
                if (sourceFile.getName().equals(".."))
                    continue;

                VirtualFile targetFile = Application.getContext().getFileManager().resolveFile(UrlUtils.newUrl(this.targetFile.getUrl(), FileUtils.getName(sourceFile.getUrl().getPath())));
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

    private void retrieveFileInfo(VirtualFile sourceFile) {
        if (sourceFile.getName().equals(".."))
            return;

        List<VirtualFile> fileList;
        if (sourceFile.isDirectory()) {
            fileList = sourceFile.list();
        } else {
            fileList = Collections.singletonList(sourceFile);
        }

        for (VirtualFile file : fileList) {
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

    private void copyFiles(VirtualFile sourceFile, VirtualFile targetFile) throws IOException {
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
    public void startProcessingFile(VirtualFile virtualFile, long currentFileNumber) {
        listener.startCopyFile(virtualFile.getName(), currentFileNumber, totalCountFiles);
    }

    @Override
    public void finishedProcessingFile(VirtualFile virtualFile) {

    }

    @Override
    public void afterStreamBufferProcessed(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal) {
        bytesCopied += bytesCopiedBlock;

        listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
        publish((int) (bytesCopiedTotal * 100 / fileSize));
    }

    @Override
    public boolean interrupt() {
        return isCancelled();
    }

    @Override
    public boolean fileExists(VirtualFile file) {
        int result = listener.fileExists(file);
        switch (result) {
            case JOptionPane.NO_OPTION:
                bytesCopied += file.getSize();
                listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
                break;
            case JOptionPane.CANCEL_OPTION:
                cancel(true);
                break;
            default:
                file.delete();
                return true;
        }
        return false;
    }
}
