/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class NavigationPanel extends JPanel {
    JComboBox<Path> driveComboBox;
    JTextField currentPathTextField;
    Path currentPath;

    FileTableMouseListener fileTableMouseListener;

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

        Path currentRootDirectory = (Path) driveComboBox.getSelectedItem();
        currentPath = currentRootDirectory;

        c.gridy++;
        currentPathTextField = createCurrentPathTextField(currentPath);
        add(currentPathTextField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;

        fileTableMouseListener = new FileTableMouseListener();
        add(new JScrollPane(createFileTable(currentPath)), c);
    }

    private JComboBox createDriveComboBox() {
        JComboBox<Path> comboBox = new JComboBox();

        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            comboBox.addItem(path);
        }

        return comboBox;
    }

    private JTextField createCurrentPathTextField(Path currentPath) {
        JTextField textField = new JTextField();
        textField.setText(currentPath.toString());
        return textField;
    }

    private JTable createFileTable(Path currentPath) {
        TableModel tableModel = new PathTableModel(currentPath);
        JTable table = new JTable(tableModel);

        table.addMouseListener(fileTableMouseListener);

        return table;
    }

    class FileTableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                System.out.println("Double Clicked on row with index " + row);

                String fileName = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
                currentPath = new File(currentPath.toString(), fileName).toPath();
                currentPathTextField.setText(currentPath.toString());
                ((PathTableModel) table.getModel()).setPath(currentPath);

                repaint();
            }
        }
    }

}
