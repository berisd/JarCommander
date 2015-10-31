/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.ParamActionEvent;
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.JFileFactory;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTable;
import at.beris.jarcommander.ui.table.FileTablePane;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class NavigationPanel extends JPanel implements ActionListener {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;
    private JPath currentPath;

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

        final JDrive currentDrive = (JDrive) driveComboBox.getSelectedItem();
        currentPath = currentDrive.getPath();

        currentPathTextField.addKeyListener(new
                                                    KeyAdapter() {
                                                        @Override
                                                        public void keyPressed(KeyEvent e) {
                                                            super.keyPressed(e);
                                                            JTextField textfield = (JTextField) e.getSource();
                                                            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                                                changeDirectory(currentDrive.getPath(textfield.getText()));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("actionPerformed " + e.getClass().getName());
        if (e.getActionCommand().equals(ActionType.SELECT_NAVIGATION_PANEL.toString())) {
            e.setSource(this);
            ((ActionListener) this.getParent()).actionPerformed(e);
        } else if (e.getActionCommand().equals(ActionType.EXECUTE_FILE.toString())) {
            JFile file = (JFile) ((ParamActionEvent) e).getParam();
            if (file.isDirectory()) {
                changeDirectory(file.toPath());
            } else {
                executeFile(file);
            }
        } else if (e.getActionCommand().equals(ActionType.NAVIGATE_PATH_UP.toString())) {
            changeDirectory(JFileFactory.newInstance(new File("..")).toPath());
        } else if (e.getActionCommand().equals(ActionType.CHANGE_DRIVE.toString())) {
            JDrive driveInfo = (JDrive) ((ParamActionEvent) e).getParam();
            changeDirectory(driveInfo.getPath());
        }
    }

    private void executeFile(JFile file) {
        JPath path = file.toPath();

        boolean isArchive = FilenameUtils.getExtension(file.toString()).toUpperCase().equals("ZIP");
        if (isArchive) {
            currentPath = path;
        }

        currentPathTextField.setText(path.toString());
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

    public List<JFile> getSelection() {
        List<JFile> fileList = new ArrayList<>();
        for (int rowIndex : fileTablePane.getTable().getSelectedRows()) {
            int modelIndex = fileTablePane.getTable().getRowSorter().convertRowIndexToModel(rowIndex);
            fileList.add((JFile) fileTablePane.getTable().getModel().getValueAt(modelIndex, 0));
        }
        return fileList;
    }

    public void refresh() {
        //TODO Refresh the whole Panel
        fileTable.refresh();
    }


    public void changeDirectory(JPath newPath) {
        if (newPath.toString().equals("..") && currentPath.equals(currentPath.getRoot()))
            return;

        if (newPath.toString().equals("..")) {
            currentPath = currentPath.getParent();
        } else {
            currentPath = newPath.normalize();
        }

        currentPathTextField.setText(currentPath.toString());
        fileTable.setPath(currentPath);
    }

    public JPath getCurrentPath() {
        return currentPath;
    }

    public JFileSystem getFileSystem() {
        return driveComboBox.getFileSystem();
    }
}
