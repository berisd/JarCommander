/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.filesystem.drive.IDrive;
import at.beris.virtualfile.File;
import at.beris.virtualfile.FileManager;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTable;
import at.beris.jarcommander.ui.table.FileTablePane;
import org.apache.log4j.Logger;

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
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;
    private File currentFile;

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

        final IDrive currentDrive = (IDrive) driveComboBox.getSelectedItem();
        currentFile = currentDrive.getFile();

        currentPathTextField.addKeyListener(new
                                                    KeyAdapter() {
                                                        @Override
                                                        public void keyPressed(KeyEvent e) {
                                                            super.keyPressed(e);
                                                            JTextField textfield = (JTextField) e.getSource();
                                                            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                                                changeDirectory(FileManager.newFile(textfield.getText()));
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

    public void executeFile(File file) {
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

    public List<File> getSelection() {
        List<File> fileList = new ArrayList<>();
        for (int rowIndex : fileTablePane.getTable().getSelectedRows()) {
            int modelIndex = fileTablePane.getTable().getRowSorter().convertRowIndexToModel(rowIndex);
            fileList.add((File) fileTablePane.getTable().getModel().getValueAt(modelIndex, 0));
        }
        return fileList;
    }

    public void refresh() {
        //TODO Refresh the whole Panel
        fileTable.refresh();
        fileTablePane.getTable().clearSelection();
    }


    public void changeDirectory(File newPath) {
        String[] pathParts = newPath.toString().split(java.io.File.separator);
        String pathLastPart = pathParts[pathParts.length - 1];

        if (pathLastPart.equals("..") && currentFile.equals(currentFile.getRoot()))
            return;

        if (pathLastPart.equals("..")) {
            currentFile = currentFile.getParent();
        } else {
            currentFile = newPath;
        }

        currentPathTextField.setText(currentFile.getPath().toString());
        fileTable.setPath(currentFile);
    }

    public File getCurrentFile() {
        return currentFile;
    }
}
