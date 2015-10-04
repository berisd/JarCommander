/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import at.beris.jaxcommander.ui.SessionPanel;
import at.beris.jaxcommander.ui.table.FileTable;
import at.beris.jaxcommander.ui.table.FileTableListener;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.nio.file.Path;
import java.nio.file.Paths;

public class NavigationPanel extends JPanel {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    private boolean selected;
    private Border borderNormal;
    private Border borderSelected;

    JComboBox<Path> driveComboBox;
    JTextField currentPathTextField;
    FileTable fileTable;
    Path currentPath;

    public NavigationPanel() {
        selected = false;

        addMouseListener(new MouseListener());
        setBackground(Color.ORANGE);

        borderNormal = BorderFactory.createEtchedBorder();
        borderSelected = BorderFactory.createLineBorder(Color.RED, 2);

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        setBorder(borderNormal);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;

        driveComboBox = createDriveComboBox();
        add(driveComboBox, c);

        currentPath = ((DriveInfo) driveComboBox.getSelectedItem()).getPath();

        c.gridy++;
        currentPathTextField = createCurrentPathTextField(currentPath);
        add(currentPathTextField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;
        fileTable = new FileTable(currentPath);
        fileTable.setFileTableListener(new CustomFileTableListener());


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

    private class ComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox comboBox = (JComboBox) e.getSource();
            Path newRootPath = ((DriveInfo) comboBox.getSelectedItem()).getPath();
            fileTable.changeDirectory(newRootPath);
        }
    }

    private class CurrentPathTextFieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);

            JTextField textfield = (JTextField) e.getSource();
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                fileTable.changeDirectory(Paths.get(textfield.getText()));
            }
        }
    }

    private JComboBox createDriveComboBox() {
        JComboBox<DriveInfo> comboBox = new JComboBox();
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                childDispatchMouseEvent(e);
            }
        });

        comboBox.setRenderer(new DriveInfoComboBoxRenderer());

        for (DriveInfo driveInfo : Application.getDriveInfo()) {
            comboBox.addItem(driveInfo);
        }

        comboBox.addActionListener(new ComboBoxActionListener());

        return comboBox;
    }

    private JTextField createCurrentPathTextField(Path currentPath) {
        JTextField textField = new JTextField();
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                childDispatchMouseEvent(e);
            }
        });
        textField.setText(currentPath.toString());
        textField.addKeyListener(new CurrentPathTextFieldKeyListener());
        return textField;
    }

    private void childDispatchMouseEvent(MouseEvent e) {
        LOGGER.info("childDispatchMouseEvent");
        NavigationPanel navigationPanel = (NavigationPanel) ((Component) e.getSource()).getParent();
        navigationPanel.dispatchEvent(new java.awt.event.MouseEvent(navigationPanel, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), false));
    }

    private class CustomFileTableListener implements FileTableListener {
        @Override
        public void changeDirectory(Path path) {
            currentPathTextField.setText(path.toString());
        }
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
        this.selected = selected;
        setBorder(selected ? borderSelected : borderNormal);
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
}
