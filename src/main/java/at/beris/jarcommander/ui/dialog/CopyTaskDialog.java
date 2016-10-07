/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.dialog;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.task.CopyTask;
import at.beris.jarcommander.task.CopyTaskListener;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.virtualfile.VirtualFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class CopyTaskDialog extends JDialog implements ActionListener, CopyTaskListener {
    private final static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTaskDialog.class);


    private JProgressBar progressbarCurrent;
    private JProgressBar progressbarAll;
    private JButton buttonAction;

    private CopyTask copyTask;
    private JLabel labelInfo;
    private JLabel labelCopyStatus;

    private NavigationPanel targetPanel;
    private List<VirtualFile> fileList;
    private ApplicationContext context;

    public CopyTaskDialog(List<VirtualFile> fileList, NavigationPanel targetPanel, ApplicationContext context) {
        this.fileList = fileList;
        this.targetPanel = targetPanel;
        this.context = context;

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

        labelInfo = new JLabel("Copy " + countFileList() + " items to:");
        panel.add(labelInfo);

        labelCopyStatus = new JLabel(targetPanel.getCurrentFile().toString());
        panel.add(labelCopyStatus);

        return panel;
    }

    private int countFileList() {
        int count = 0;

        for (VirtualFile file : fileList) {
            try {
                if (file.getName().equals(".."))
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }

        return count;
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
    public void actionPerformed(ActionEvent e) {
        if ("Start".equals(buttonAction.getText())) {
            startWorker();
            buttonAction.setText("Cancel");
        } else if ("Cancel".equals(buttonAction.getText())) {
            copyTask.cancel(true);
            targetPanel.refresh();
            this.dispose();
        }
    }

    public void startWorker() {
        LOGGER.debug("startWorker");
        copyTask = new CopyTask(fileList, targetPanel.getCurrentFile(), this);
        copyTask.execute();
    }

    @Override
    public void setCurrentProgressBar(int progress) {
        progressbarCurrent.setValue(progress);
    }

    @Override
    public void setAllProgressBar(int progress) {
        progressbarAll.setValue(progress);
    }

    @Override
    public void done() {
        try {
            LOGGER.debug("done");
            targetPanel.refresh();
            CopyTaskDialog.this.dispose();
        } catch (Exception ex) {
            LOGGER.debug(ex.getMessage());
        }
    }

    @Override
    public void startCopyFile(String fileName, long currentFileNumber, long totalCountFiles) {
        String infoText = "Copying " + fileName + " ... ";
        labelInfo.setText(infoText);
        labelInfo.setToolTipText(infoText);
        String statusText = "Item " + currentFileNumber + " of  " + totalCountFiles + " processed.";
        labelCopyStatus.setText(statusText);
        labelCopyStatus.setToolTipText(statusText);
    }

    @Override
    public int fileExists(VirtualFile file) {
        try {
            return JOptionPane.showConfirmDialog(this, file.getPath() + " already exists! " +
                            System.lineSeparator() + "Would you like to overwrite it?", "File exists",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            logException(e);
        }
        return JOptionPane.NO_OPTION;
    }

    @Override
    public int showError(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    }
}
