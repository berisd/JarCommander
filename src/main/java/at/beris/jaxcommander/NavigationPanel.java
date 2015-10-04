/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import at.beris.jaxcommander.ui.table.FileTable;
import at.beris.jaxcommander.ui.table.FileTableListener;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NavigationPanel extends JPanel {
    private final static Logger LOGGER = Logger.getLogger(NavigationPanel.class.getName());

    JComboBox<Path> driveComboBox;
    JTextField currentPathTextField;
    FileTable fileTable;
    Path currentPath;

    public NavigationPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridBagLayout);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Table Title",
                TitledBorder.CENTER,
                TitledBorder.TOP));

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

        add(new JScrollPane(fileTable), c);
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
        comboBox.setRenderer(new DriveInfoComboBoxRenderer());

        for (DriveInfo driveInfo : Application.getDriveInfo()) {
            comboBox.addItem(driveInfo);
        }

        comboBox.addActionListener(new ComboBoxActionListener());

        return comboBox;
    }

    private JTextField createCurrentPathTextField(Path currentPath) {
        JTextField textField = new JTextField();
        textField.setText(currentPath.toString());
        textField.addKeyListener(new CurrentPathTextFieldKeyListener());
        return textField;
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
}
