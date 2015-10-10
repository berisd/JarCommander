/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.ui.NavigationPanel;
import org.apache.log4j.Logger;

import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileTableMouseListener extends MouseAdapter {

    private final static Logger LOGGER = Logger.getLogger(FileTableMouseListener.class);

    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.info("mouseClicked");

        JScrollPane scrollPane = (JScrollPane) ((Component) e.getSource()).getParent().getParent();
        NavigationPanel navigationPanel = (NavigationPanel) scrollPane.getParent();

        if (e.getClickCount() == 2) {
            FileTable table = (FileTable) e.getSource();
            int rowIndex = table.convertRowIndexToModel(table.getSelectedRow());
            LOGGER.info("Double Clicked on row with index " + rowIndex);

            File file = (File) table.getModel().getValueAt(rowIndex, 0);

            if (file.isDirectory()) {
                navigationPanel.changeDirectory(file.toPath());
            }
        }

        scrollPane.dispatchEvent(new java.awt.event.MouseEvent(scrollPane, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), false));
    }
}
