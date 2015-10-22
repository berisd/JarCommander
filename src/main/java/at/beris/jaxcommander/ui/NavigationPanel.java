/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.action.ActionCommand;
import at.beris.jaxcommander.action.ParamActionEvent;
import at.beris.jaxcommander.filesystem.LocalDrive;
import at.beris.jaxcommander.filesystem.JFileSystem;
import at.beris.jaxcommander.filesystem.file.VirtualFile;
import at.beris.jaxcommander.filesystem.file.VirtualFileFactory;
import at.beris.jaxcommander.filesystem.path.VirtualPath;
import at.beris.jaxcommander.ui.combobox.DriveComboBox;
import at.beris.jaxcommander.ui.table.FileTablePane;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
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

import static at.beris.jaxcommander.action.ActionCommand.*;

public class NavigationPanel extends JPanel implements ActionListener {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;
    private VirtualPath currentPath;
    private JFileSystem fileSystem;

    private DriveComboBox driveComboBox;
    private JTextField currentPathTextField;
    private FileTablePane fileTablePane;

    public NavigationPanel(JFileSystem fileSystem, FileTablePane fileTablePane, DriveComboBox driveComboBox, JTextField currentPathTextField, FileTableStatusLabel statusLabel) {
        selected = false;
        this.fileTablePane = fileTablePane;
        this.driveComboBox = driveComboBox;
        this.currentPathTextField = currentPathTextField;

        addMouseListener(new MouseListener());

        final LocalDrive currentDrive = (LocalDrive) driveComboBox.getSelectedItem();
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
        borderSelected = BorderFactory.createLineBorder(Color.RED, 2);

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("actionPerformed " + e.getClass().getName());
        if (e.getActionCommand().equals(ActionCommand.SELECT_NAVIGATION_PANEL)) {
            e.setSource(this);
            ((ActionListener) this.getParent().getParent()).actionPerformed(e);
        } else if (e.getActionCommand().equals(CHANGE_DIRECTORY)) {
            VirtualFile file = (VirtualFile) ((ParamActionEvent) e).getParam();
            changeDirectory(file.toPath());
        } else if (e.getActionCommand().equals(EXECUTE_FILE)) {
            VirtualFile virtualFile = (VirtualFile) ((ParamActionEvent) e).getParam();
            VirtualPath path = virtualFile.toPath();
            currentPathTextField.setText(path.toString());
            fileTablePane.listFile((File) virtualFile.getBaseObject());
        } else if (e.getActionCommand().equals(NAVIGATE_PATH_UP)) {
            changeDirectory(VirtualFileFactory.newInstance(new File("..")).toPath());
        } else if (e.getActionCommand().equals(CHANGE_DRIVE)) {
            LocalDrive driveInfo = (LocalDrive) ((ParamActionEvent) e).getParam();
            changeDirectory(driveInfo.getPath());
        }
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

    public void refreshDirectory() {
        fileTablePane.refresh();
    }


    public void changeDirectory(VirtualPath newPath) {
        if (newPath.toString().equals("..") && currentPath.equals(currentPath.getRoot()))
            return;

        if (newPath.toString().equals("..")) {
            currentPath = currentPath.getParent();
        } else {
            currentPath = newPath.normalize();
        }

        currentPathTextField.setText(currentPath.toString());
        fileTablePane.setPath(currentPath);
    }

    public VirtualPath getCurrentPath() {
        return currentPath;
    }

    public JFileSystem getFileSystem() {
        return fileSystem;
    }
}
