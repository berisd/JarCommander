/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.DriveInfo;
import at.beris.jaxcommander.ui.combobox.DriveComboBox;
import at.beris.jaxcommander.ui.table.FileTable;
import at.beris.jaxcommander.ui.table.FileTableMouseListener;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NavigationPanel extends JPanel {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;
    private Path currentPath;

    DriveComboBox driveComboBox;
    JTextField currentPathTextField;
    FileTable fileTable;

    public NavigationPanel(FileTable fileTable, DriveComboBox driveComboBox, JTextField currentPathTextField) {
        selected = false;
        this.fileTable = fileTable;
        this.driveComboBox = driveComboBox;
        this.currentPathTextField = currentPathTextField;

        addMouseListener(new MouseListener());

        currentPath = ((DriveInfo) driveComboBox.getSelectedItem()).getPath();

        fileTable.addMouseListener(new FileTableMouseListener());

        driveComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                LOGGER.info("drivecombo itemStateChanged " + e.getItem().getClass());
                DriveInfo driveInfo = (DriveInfo) e.getItem();
                changeDirectory(driveInfo.getPath());
            }
        });
        driveComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                childDispatchMouseEvent(e);
            }
        });

        currentPathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                JTextField textfield = (JTextField) e.getSource();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    changeDirectory(Paths.get(textfield.getText()));
                }
            }
        });
        currentPathTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                childDispatchMouseEvent(e);
            }
        });


        borderNormal = BorderFactory.createEtchedBorder();
        borderSelected = BorderFactory.createLineBorder(Color.RED, 2);

        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        setBorder(borderNormal);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        add(driveComboBox, c);

        c.gridy++;
        add(currentPathTextField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;
        JScrollPane scrollPane = new JScrollPane(fileTable);
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOGGER.info("scrollPane clicked");
                super.mouseClicked(e);
                childDispatchMouseEvent(e);
            }
        });
        add(scrollPane, c);
    }

    private void childDispatchMouseEvent(MouseEvent e) {
        NavigationPanel navigationPanel = (NavigationPanel) ((Component) e.getSource()).getParent();
        navigationPanel.dispatchEvent(new java.awt.event.MouseEvent(navigationPanel, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), false));
    }


    public void dispose() {
        LOGGER.debug("dispose");
        fileTable.dispose();
    }

    public FileTable getFileTable() {
        return fileTable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (!this.selected && selected)
            getFileTable().getSelectionModel().clearSelection();

        this.selected = selected;
        setBorder(selected ? borderSelected : borderNormal);
        getFileTable().setRowSelectionAllowed(selected);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            LOGGER.info("mouseClicked");
            super.mouseClicked(e);

            SessionPanel sessionPanel = (SessionPanel) ((Component) e.getSource()).getParent().getParent();
            sessionPanel.dispatchEvent(e);
        }
    }

    public List<File> getSelection() {
        List<File> fileList = new ArrayList<>();
        for (int rowIndex : fileTable.getSelectedRows()) {
            int modelIndex = fileTable.getRowSorter().convertRowIndexToModel(rowIndex);
            fileList.add((File) fileTable.getModel().getValueAt(modelIndex, 0));
        }
        return fileList;
    }

    public void refreshDirectory() {
        fileTable.refresh();
    }


    public void changeDirectory(Path newPath) {

        if (newPath.toString().equals("..")) {
            currentPath = currentPath.getParent();
        } else {
            currentPath = newPath.normalize();
        }

        currentPathTextField.setText(currentPath.toString());
        fileTable.setPath(currentPath);
    }


    public Path getCurrentPath() {
        return fileTable.getPath();
    }
}
