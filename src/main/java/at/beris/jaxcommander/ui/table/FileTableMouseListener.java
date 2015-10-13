/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.action.ActionCommand;
import at.beris.jaxcommander.ui.NavigationPanel;
import org.apache.log4j.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static at.beris.jaxcommander.action.ActionCommand.*;

public class FileTableMouseListener extends MouseAdapter {

    private final static Logger LOGGER = Logger.getLogger(FileTableMouseListener.class);

    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.debug("mouseClicked");

        FileTablePane pane = (FileTablePane) ((Component) e.getSource()).getParent().getParent();
        NavigationPanel navigationPanel = (NavigationPanel) pane.getParent();

        if (e.getClickCount() == 2) {
            JTable table = (JTable) e.getSource();
            int rowIndex = table.convertRowIndexToModel(table.getSelectedRow());
            LOGGER.debug("Double Clicked on row with index " + rowIndex);

            File file = (File) table.getModel().getValueAt(rowIndex, 0);

            if (file.isDirectory()) {
                navigationPanel.changeDirectory(file.toPath());
            }
        }

        pane.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
    }
}
