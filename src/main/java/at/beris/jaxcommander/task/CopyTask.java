/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.task;

import at.beris.jaxcommander.ui.NavigationPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static at.beris.jaxcommander.Application.logException;

public class CopyTask extends JDialog implements ActionListener, PropertyChangeListener {
    private final static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTask.class);

    private JProgressBar progressbarCurrent;
    private JProgressBar progressbarAll;
    private JButton buttonAction;

    private JPanel panelProgress;
    private JPanel panelInput;
    private CopyWorker copyWorker;
    private JLabel labelInfo;

    private NavigationPanel targetPanel;
    private List<File> fileList;

    public CopyTask(List<File> fileList, NavigationPanel targetPanel) {
        this.fileList = fileList;
        this.targetPanel = targetPanel;

        setTitle("Copy");
        setLayout(new GridLayout(3, 1));

        progressbarAll = new JProgressBar(0, 100);
        progressbarAll.setStringPainted(true);
        progressbarAll.setPreferredSize(new Dimension(100, 25));

        progressbarCurrent = new JProgressBar(0, 100);
        progressbarCurrent.setStringPainted(true);
        progressbarCurrent.setPreferredSize(new Dimension(100, 25));


        panelInput = new JPanel(new GridLayout(1, 1));
        panelInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Input"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        labelInfo = new JLabel("Calculating size...");

        panelInput.add(labelInfo);

        panelProgress = new JPanel(new GridLayout(2, 2));
        panelProgress.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Progress"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        panelProgress.add(new JLabel("Current:"));
        panelProgress.add(progressbarCurrent);
        panelProgress.add(new JLabel("All:"));
        panelProgress.add(progressbarAll);

        buttonAction = new JButton("Cancel");
        buttonAction.addActionListener(this);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(panelInput);
        add(panelProgress);
        add(buttonAction);

        pack();
        setLocationRelativeTo(null);
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
        if ("Close".equals(buttonAction.getText())) {
            this.dispose();
        } else if ("Cancel".equals(buttonAction.getText())) {
            copyWorker.cancel(true);
            buttonAction.setText("Close");
        }
    }

    public void startWorker() {
        copyWorker = new CopyWorker(fileList, targetPanel);
        copyWorker.addPropertyChangeListener(this);
        copyWorker.execute();
    }

    class CopyWorker extends SwingWorker<Void, Integer> {
        private List<File> sourceList;
        private NavigationPanel targetPanel;
        private long totalBytes = 0L;
        private long copiedBytes = 0L;

        public CopyWorker(List<File> sourceList, NavigationPanel targetPanel) {
            this.sourceList = sourceList;
            this.targetPanel = targetPanel;
            progressbarCurrent.setValue(0);
            progressbarAll.setValue(0);
        }

        @Override
        public Void doInBackground() throws Exception {
            LOGGER.debug("doInBackground");
            try {
                labelInfo.setText("Calculating size...");

                for (File file : sourceList) {
                    retrieveTotalBytes(file);
                }

                for (File file : sourceList) {
                    copyFiles(file, new File(targetPanel.getCurrentPath().toFile(), file.getName()));
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
                targetPanel.refreshDirectory();
                buttonAction.setText("Close");
            } catch (Exception ex) {
                LOGGER.debug(ex.getMessage());
            }
        }

        private void retrieveTotalBytes(File sourceFile) {

            File[] files = sourceFile.listFiles();
            if (files == null) {
                files = new File[]{sourceFile};
            }

            for (File file : files) {
                if (file.isDirectory()) retrieveTotalBytes(file);
                else totalBytes += file.length();
            }
        }

        private void copyFiles(File sourceFile, File targetFile) throws IOException {
            LOGGER.debug("CopyFile " + sourceFile);
            if (sourceFile.isDirectory()) {
                if (!targetFile.exists()) targetFile.mkdirs();

                String[] filePaths = sourceFile.list();

                for (String filePath : filePaths) {
                    File srcFile = new File(sourceFile, filePath);
                    File destFile = new File(targetFile, filePath);

                    copyFiles(srcFile, destFile);
                }
            } else {
                labelInfo.setText("Copying " + sourceFile.getAbsolutePath() + " ... ");
                labelInfo.setToolTipText("Copying " + sourceFile.getAbsolutePath() + " ... ");

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));

                long fileBytes = sourceFile.length();
                long soFar = 0L;

                int theByte;

                while ((theByte = bis.read()) != -1) {
                    bos.write(theByte);

                    setProgress((int) (copiedBytes++ * 100 / totalBytes));
                    publish((int) (soFar++ * 100 / fileBytes));
                }

                bis.close();
                bos.close();

                publish(100);

                labelInfo.setText("Done!\n");
                labelInfo.setToolTipText("");
            }
        }
    }
}
