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
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class JaxTable extends JPanel {
    JTable table;

    public JaxTable() {
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

        JComboBox<Path> driveList = new JComboBox();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            driveList.addItem(path);
        }

        add(driveList, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;
//
        Path currentRootDirectory = (Path) driveList.getSelectedItem();
        TableModel fileTableModel = new PathTableModel(currentRootDirectory);

        table = new JTable(fileTableModel);
        add(new JScrollPane(table), c);
    }
}
