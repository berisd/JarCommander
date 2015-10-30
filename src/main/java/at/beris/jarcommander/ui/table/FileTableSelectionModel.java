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
import javax.swing.JComponent;

public class FileTableSelectionModel extends DefaultListSelectionModel {
    private FileTable fileTable;

    public FileTableSelectionModel(FileTable fileTable) {
        this.fileTable = fileTable;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        NavigationPanel navigationPanel = null;
        JComponent parent = (JComponent) fileTable.getParent();

        if (parent != null)
            parent = (JComponent) parent.getParent();
        if (parent != null)
            parent = (JComponent) parent.getParent();
        if (parent != null) {
            navigationPanel = (NavigationPanel) parent;

            if (navigationPanel != null && !navigationPanel.isSelected())
                return;
        }

        super.setSelectionInterval(index0, index1);
    }
}
