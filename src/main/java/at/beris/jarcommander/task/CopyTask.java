/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.exception.ApplicationException;
import at.beris.jarcommander.filesystem.IBlockCopy;
import at.beris.jarcommander.filesystem.LocalBlockCopy;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.JFileFactory;
import at.beris.jarcommander.filesystem.path.JPath;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTask extends SwingWorker<Void, Integer> {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);

    private List<JFile> sourceList;
    private JPath targetPath;
    private long bytesTotal = 0L;
    private long bytesCopied = 0L;
    private long totalCountFiles = 0;
    private long currentFileNumber = 0;
    private CopyTaskListener listener;
    private IBlockCopy blockCopy;

    public CopyTask(List<JFile> sourceList, JPath targetPath, CopyTaskListener listener) {
        this.sourceList = sourceList;
        this.targetPath = targetPath;
        this.listener = listener;

        listener.setCurrentProgressBar(0);
        listener.setAllProgressBar(0);

        blockCopy = new LocalBlockCopy();
    }

    @Override
    public Void doInBackground() throws Exception {
        LOGGER.debug("doInBackground");
        try {
            for (JFile sourceFile : sourceList) {
                retrieveFileInfo(sourceFile);
            }

            for (JFile sourceFile : sourceList) {
                JFile targetFile = JFileFactory.newInstance(targetPath.toFile(), sourceFile.getName());
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

    private void retrieveFileInfo(JFile sourceFile) {
        List<JFile> fileList;
        if (sourceFile.isDirectory()) {
            fileList = sourceFile.listFiles();
        } else {
            fileList = Collections.singletonList(sourceFile);
        }

        for (JFile file : fileList) {
            if (file.isDirectory()) {
                retrieveFileInfo(file);
            } else {
                bytesTotal += file.getSize();
                totalCountFiles++;
            }
        }
    }

    private void copyFiles(JFile sourceFile, JFile targetFile) throws IOException {
        LOGGER.debug("CopyFile " + sourceFile);

        if (sourceFile.isDirectory()) {
            if (!targetFile.exists())
                targetFile.mkdirs();
            else
                listener.fileExists(targetFile);

            for (JFile sourceChildFile : (List<JFile>) sourceFile.list()) {
                if (sourceChildFile.toString().equals(".."))
                    continue;
                JFile destFile = JFileFactory.newInstance(new File((File) targetFile.getBaseObject(), sourceChildFile.getName()));
                copyFiles(sourceChildFile, destFile);
            }
        } else {
            currentFileNumber++;
            listener.startCopyFile(sourceFile, currentFileNumber, totalCountFiles);

            if (targetFile.exists()) {
                listener.fileExists(targetFile);
            }

            JFile targetParentFile = targetFile.getParentFile();
            if (targetParentFile != null) {
                targetParentFile.mkdirs();
            }

            blockCopy.init(sourceFile, targetFile);

            boolean isCancelled = false;
            try {
                while ((blockCopy.read() >= 0 || blockCopy.positionBuffer() != 0) && !isCancelled()) {
                    blockCopy.copy();
                    bytesCopied += blockCopy.bytesWritten();
                    listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
                    publish((int) (blockCopy.bytesWrittenTotal() * 100 / blockCopy.size()));
                }
            } catch (ApplicationException ex) {
                ex.show();
                isCancelled = true;
            } finally {
                blockCopy.close();
                isCancelled = isCancelled || isCancelled();
            }

            if (isCancelled && blockCopy.size() != blockCopy.bytesWrittenTotal()) {
                targetFile.delete();
            }

            publish(100);
        }
    }
}
