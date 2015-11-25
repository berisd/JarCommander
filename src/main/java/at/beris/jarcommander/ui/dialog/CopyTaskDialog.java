/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.dialog;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.task.CopyTask;
import at.beris.jarcommander.task.CopyTaskListener;
import at.beris.jarcommander.ui.NavigationPanel;
import org.apache.commons.lang3.NotImplementedException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CopyTaskDialog extends JDialog implements ActionListener, CopyTaskListener {
    private final static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CopyTaskDialog.class);


    private JProgressBar progressbarCurrent;
    private JProgressBar progressbarAll;
    private JButton buttonAction;

    private CopyTask copyTask;
    private JLabel labelInfo;
    private JLabel labelCopyStatus;

    private NavigationPanel targetPanel;
    private List<IFile> fileList;

    public CopyTaskDialog(List<IFile> fileList, NavigationPanel targetPanel) {
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
        copyTask = new CopyTask(fileList, targetPanel.getCurrentPath(), this);
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
    public void startCopyFile(IFile sourceFile, long currentFileNumber, long totalCountFiles) {
        String infoText = "Copying " + sourceFile.getAbsolutePath() + " ... ";
        labelInfo.setText(infoText);
        labelInfo.setToolTipText(infoText);
        String statusText = "Item " + currentFileNumber + " of  " + totalCountFiles + " processed.";
        labelCopyStatus.setText(statusText);
        labelCopyStatus.setToolTipText(statusText);
    }

    @Override
    public void fileExists(IFile file) {
        throw new NotImplementedException("");
    }
}
