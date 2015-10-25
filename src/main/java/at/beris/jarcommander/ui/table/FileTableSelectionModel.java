/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.ui.NavigationPanel;

import javax.swing.DefaultListSelectionModel;

public class FileTableSelectionModel extends DefaultListSelectionModel {
    private FileTable fileTable;

    public FileTableSelectionModel(FileTable fileTable) {
        this.fileTable = fileTable;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        NavigationPanel navigationPanel = (NavigationPanel) fileTable.getParent().getParent().getParent();

        if (!navigationPanel.isSelected())
            return;

        super.setSelectionInterval(index0, index1);
    }
}
