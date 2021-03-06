/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.drive.Drive;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTable;
import at.beris.jarcommander.ui.table.FileTablePane;
import at.beris.virtualfile.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class NavigationPanel extends JPanel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;
    private VirtualFile currentFile;

    private DriveComboBox driveComboBox;
    private JTextField currentPathTextField;
    private FileTablePane fileTablePane;
    private FileTable fileTable;

    public NavigationPanel(FileTablePane fileTablePane, DriveComboBox driveComboBox, JTextField currentPathTextField, FileTableStatusLabel statusLabel) {
        selected = false;
        this.fileTablePane = fileTablePane;
        this.fileTable = fileTablePane.getTable();
        this.driveComboBox = driveComboBox;
        this.currentPathTextField = currentPathTextField;

        addMouseListener(new MouseListener());

        final Drive currentDrive = (Drive) driveComboBox.getSelectedItem();
        currentFile = currentDrive.getFile();

        currentPathTextField.addKeyListener(new KeyAdapter() {
                                                @Override
                                                public void keyPressed(KeyEvent e) {
                                                    super.keyPressed(e);
                                                    JTextField textfield = (JTextField) e.getSource();
                                                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                                        changeDirectory(Application.getContext().getFileManager().resolveFile(textfield.getText()));
                                                    }
                                                }
                                            }
        );

        borderNormal = BorderFactory.createEtchedBorder();
        borderSelected = BorderFactory.createLineBorder(SELECTION_FOREGROUND_COLOR, 2);

        GridBagConstraints c = new GridBagConstraints();

        setLayout(new GridBagLayout());

        setBorder(borderNormal);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        add(driveComboBox, c);

        c.gridy++;

        add(currentPathTextField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;

        add(fileTablePane, c);

        c.weightx = 0;
        c.weighty = 0;
        c.gridy++;

        add(statusLabel, c);
    }

    public FileTablePane getFileTablePane() {
        return fileTablePane;
    }

    public FileTable getFileTable() {
        return fileTable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBorder(selected ? borderSelected : borderNormal);
        if (selected) {
            fileTable.requestFocusInWindow();
        }
    }

    public void executeFile(VirtualFile file) {
        currentPathTextField.setText(file.getPath());
        fileTable.listFile(file);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            LOGGER.debug("mouseClicked");
            super.mouseClicked(e);

            SessionPanel sessionPanel = (SessionPanel) ((Component) e.getSource()).getParent().getParent();
            sessionPanel.dispatchEvent(e);
        }
    }

    public List<VirtualFile> getSelection() {
        List<VirtualFile> fileList = new ArrayList<>();
        for (int rowIndex : fileTablePane.getTable().getSelectedRows()) {
            int modelIndex = fileTablePane.getTable().getRowSorter().convertRowIndexToModel(rowIndex);
            fileList.add((VirtualFile) fileTablePane.getTable().getModel().getValueAt(modelIndex, 0));
        }
        return fileList;
    }

    public void refresh() {
        //TODO Refresh the whole Panel
        fileTable.refresh();
        fileTablePane.getTable().clearSelection();
    }


    public void changeDirectory(VirtualFile newFile) {
        String[] pathParts = newFile.getPath().split("/");
        String pathLastPart = pathParts[pathParts.length - 1];

        if (pathLastPart.equals("..") && currentFile.getPath().equals(currentFile.getRoot().getPath()))
            return;
        if (pathLastPart.equals("..")) {
            currentFile = currentFile.getParent();
        } else {
            currentFile = newFile;
        }

        currentPathTextField.setText(currentFile.getPath().toString());
        fileTable.setPath(currentFile);
    }

    public VirtualFile getCurrentFile() {
        return currentFile;
    }
}
