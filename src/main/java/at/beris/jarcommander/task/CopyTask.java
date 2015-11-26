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
import at.beris.jarcommander.filesystem.FileUtils;
import at.beris.jarcommander.filesystem.IBlockCopy;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.path.IPath;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTask extends SwingWorker<Void, Integer> {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);

    private List<IFile> sourceList;
    private IPath targetPath;
    private long bytesTotal = 0L;
    private long bytesCopied = 0L;
    private long totalCountFiles = 0;
    private long currentFileNumber = 0;
    private CopyTaskListener listener;
    private IBlockCopy blockCopy;

    public CopyTask(List<IFile> sourceList, IPath targetPath, CopyTaskListener listener) {
        this.sourceList = sourceList;
        this.targetPath = targetPath;
        this.listener = listener;

        listener.setCurrentProgressBar(0);
        listener.setAllProgressBar(0);

        blockCopy = FileUtils.createBlockCopyInstance(sourceList.get(0), targetPath);
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
                IFile targetFile = FileFactory.newInstance(targetPath.toFile(), sourceFile.getName());
                copyFiles(sourceFile, targetFile);
            }
        } catch (Exception ex) {
            logException(ex);
        } finally {
            blockCopy.close();
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
            fileList = sourceFile.listFiles();
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
        if (sourceFile.getName().equals(".."))
            return;

        LOGGER.debug("CopyFile " + sourceFile + " to " + targetFile);

        if (sourceFile.isDirectory()) {
            if (!targetFile.exists())
                targetFile.mkdirs();
            else
                listener.fileExists(targetFile);

            for (IFile sourceChildFile : (List<IFile>) sourceFile.list()) {
                if (sourceChildFile.toString().equals(".."))
                    continue;
                IFile destFile = FileFactory.newInstance(new File((File) targetFile.getBaseObject(), sourceChildFile.getName()));
                copyFiles(sourceChildFile, destFile);
            }
        } else {
            currentFileNumber++;
            listener.startCopyFile(sourceFile, currentFileNumber, totalCountFiles);

            if (targetFile.exists()) {
                int result = listener.fileExists(targetFile);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                        bytesCopied += targetFile.getSize();
                        listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancel(true);
                    default:
                        targetFile.delete();
                }
            }

            IFile targetParentFile = targetFile.getParentFile();
            if (targetParentFile != null) {
                targetParentFile.mkdirs();
            }

            try {
                blockCopy.init(sourceFile, targetFile);

                while ((blockCopy.read() >= 0 || blockCopy.positionBuffer() != 0) && !isCancelled()) {
                    bytesCopied += blockCopy.write();
                    listener.setAllProgressBar((int) (bytesCopied * 100 / bytesTotal));
                    publish((int) (blockCopy.bytesWritten() * 100 / blockCopy.size()));
                }
            } catch (RuntimeException ex) {
                LOGGER.error(ex.getMessage());
                int result = listener.showError(ex.getMessage());
                if (result == JOptionPane.CANCEL_OPTION)
                    cancel(true);
            } catch (Exception ex) {
                Application.logException(ex);
            } finally {
                blockCopy.close();
                if (isCancelled() && blockCopy.size() != blockCopy.bytesWritten()) {
                    targetFile.delete();
                }
            }

            publish(100);
        }
    }
}
