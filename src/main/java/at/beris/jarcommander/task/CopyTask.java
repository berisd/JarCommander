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
import org.apache.log4j.Logger;

import javax.swing.SwingWorker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTask extends SwingWorker<Void, Integer> implements PropertyChangeListener {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);
    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    private List<JFile> sourceList;
    private JPath targetPath;
    private long bytesTotal = 0L;
    private long bytesCopied = 0L;
    private long totalCountFiles = 0;
    private long currentFileNumber = 0;
    private CopyTaskListener listener;

    public CopyTask(List<JFile> sourceList, JPath targetPath, CopyTaskListener listener) {
        this.sourceList = sourceList;
        this.targetPath = targetPath;
        this.listener = listener;

        listener.setCurrentProgressBar(0);
        listener.setAllProgressBar(0);
    }

    @Override
    public Void doInBackground() throws Exception {
        LOGGER.debug("doInBackground");
        try {
            for (JFile sourceFile : sourceList) {
                File file = (File) sourceFile.getBaseObject();
                retrieveFileInfo(file);
            }

            for (JFile sourceFile : sourceList) {
                JFile targetFile = JFileFactory.newInstance(new File(((Path) targetPath.getBaseObject()).toFile(), sourceFile.toPath().toString()));
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

    private void retrieveFileInfo(File sourceFile) {
        File[] files = sourceFile.listFiles();
        if (files == null) {
            files = new File[]{sourceFile};
        }

        for (File file : files) {
            if (file.isDirectory()) retrieveFileInfo(file);
            else {
                bytesTotal += file.length();
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

            FileChannel bis = new FileInputStream((File) sourceFile.getBaseObject()).getChannel();
            FileChannel bos = new FileOutputStream((File) targetFile.getBaseObject()).getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);

            long bytesSizeCurrentFile = bis.size();
            long bytesWrittenCurrentFile = 0L;
            long bytesWritten = 0;

            boolean isCancelled = false;
            try {
                while ((bis.read(buffer) >= 0 || buffer.position() != 0) && !isCancelled()) {
                    buffer.flip();
                    bytesWritten = bos.write(buffer);
                    buffer.compact();

                    bytesWrittenCurrentFile += bytesWritten;
                    bytesCopied += bytesWritten;

                    setProgress((int) (bytesCopied * 100 / bytesTotal));
                    publish((int) (bytesWrittenCurrentFile * 100 / bytesSizeCurrentFile));
                }
            } catch (IOException ex) {
                isCancelled = true;
            } finally {
                bis.close();
                bos.close();
                isCancelled = isCancelled || isCancelled();
            }

            if (isCancelled && bytesSizeCurrentFile != bytesWrittenCurrentFile) {
                targetFile.delete();
            }

            publish(100);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            listener.setAllProgressBar(progress);
        }
    }
}
