/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.task;

import at.beris.jaxcommander.filesystem.JFileSystem;
import at.beris.jaxcommander.filesystem.file.JFile;
import at.beris.jaxcommander.filesystem.file.JFileFactory;
import at.beris.jaxcommander.ui.NavigationPanel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import static at.beris.jaxcommander.Application.logException;

public class CopyTask extends JDialog implements ActionListener, PropertyChangeListener {
    private final static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);
    private final static int COPY_BUFFER_SIZE = 1024 * 16;


    private JProgressBar progressbarCurrent;
    private JProgressBar progressbarAll;
    private JButton buttonAction;

    private CopyWorker copyWorker;
    private JLabel labelInfo;
    private JLabel labelCopyStatus;

    private NavigationPanel targetPanel;
    private List<JFile> fileList;

    public CopyTask(List<JFile> fileList, NavigationPanel targetPanel) {
        this.fileList = fileList;
        this.targetPanel = targetPanel;

        setTitle("Copy");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        progressbarAll = new JProgressBar(0, 100);
        progressbarAll.setStringPainted(true);

        progressbarCurrent = new JProgressBar(0, 100);
        progressbarCurrent.setStringPainted(true);

        buttonAction = new JButton("Start");
        buttonAction.addActionListener(this);

        add(createPanelInput(), BorderLayout.NORTH);
        add(createPanelProgress(), BorderLayout.CENTER);
        add(buttonAction, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createPanelInput() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Input"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        labelInfo = new JLabel("Copy " + fileList.size() + " items to:");
        panel.add(labelInfo);

        labelCopyStatus = new JLabel(targetPanel.getCurrentPath().toString());
        panel.add(labelCopyStatus);

        return panel;
    }

    private JPanel createPanelProgress() {
        JPanel panelProgress = new JPanel();
        panelProgress.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        panelProgress.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Progress"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        c.gridx = 0;
        c.gridy = 0;

        panelProgress.add(new JLabel("Current:"), c);
        c.gridx++;
        panelProgress.add(progressbarCurrent, c);

        c.gridy++;
        c.gridx = 0;
        panelProgress.add(new JLabel("All:"), c);

        c.gridx++;
        panelProgress.add(progressbarAll, c);

        return panelProgress;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressbarAll.setValue(progress);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Start".equals(buttonAction.getText())) {
            startWorker();
            buttonAction.setText("Cancel");
        } else if ("Cancel".equals(buttonAction.getText())) {
            copyWorker.cancel(true);
            targetPanel.refresh();
            this.dispose();
        }
    }

    public void startWorker() {
        LOGGER.debug("startWorker");
        copyWorker = new CopyWorker(fileList, targetPanel);
        copyWorker.addPropertyChangeListener(this);
        copyWorker.execute();
    }

    class CopyWorker extends SwingWorker<Void, Integer> {
        private List<JFile> sourceList;
        private NavigationPanel targetPanel;
        private long bytesTotal = 0L;
        private long bytesCopied = 0L;
        private long totalCountFiles = 0;
        private long currentFileNumber = 0;

        public CopyWorker(List<JFile> sourceList, NavigationPanel targetPanel) {
            this.sourceList = sourceList;
            this.targetPanel = targetPanel;
            progressbarCurrent.setValue(0);
            progressbarAll.setValue(0);
        }

        @Override
        public Void doInBackground() throws Exception {
            JFileSystem fileSystem = targetPanel.getFileSystem();


            LOGGER.debug("doInBackground");
            try {
                for (JFile jFile : sourceList) {
                    File file = (File) jFile.getBaseObject();
                    retrieveFileInfo(file);
                }

                for (JFile jFile : sourceList) {
                    copyFiles(jFile, JFileFactory.newInstance(new File(((Path) targetPanel.getCurrentPath().getBaseObject()).toFile(), jFile.getName())));
                }
            } catch (Exception ex) {
                logException(ex);
            }
            return null;
        }

        @Override
        public void process(List<Integer> chunks) {
            for (int i : chunks) {
                progressbarCurrent.setValue(i);
            }
        }

        @Override
        public void done() {
            try {
                LOGGER.debug("done");
                setProgress(100);
                targetPanel.refresh();
                CopyTask.this.dispose();
            } catch (Exception ex) {
                LOGGER.debug(ex.getMessage());
            }
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
            JFileSystem targetFileSystem = targetPanel.getFileSystem();

            if (sourceFile.isDirectory()) {
                if (!targetFile.exists()) targetFile.mkdirs();

                for (JFile file : (List<JFile>) sourceFile.list()) {
                    JFile srcFile = JFileFactory.newInstance(new File((File) sourceFile.getBaseObject(), file.toString()));
                    JFile destFile = JFileFactory.newInstance(new File((File) targetFile.getBaseObject(), file.toString()));
                    copyFiles(srcFile, destFile);
                }
            } else {
                String infoText = "Copying " + sourceFile.getAbsolutePath() + " ... ";
                labelInfo.setText(infoText);
                labelInfo.setToolTipText(infoText);

                currentFileNumber++;

                String statusText = "Item " + currentFileNumber + " of  " + totalCountFiles + " processed.";
                labelCopyStatus.setText(statusText);
                labelCopyStatus.setToolTipText(statusText);

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
                    targetFileSystem.delete(targetFile.toPath());
                }

                publish(100);
            }
        }
    }
}
