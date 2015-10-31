/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.ExecuteFileAction;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileTableMouseListener extends MouseAdapter {
    private final static Logger LOGGER = Logger.getLogger(FileTableMouseListener.class);

    @Override
    public void mousePressed(MouseEvent e) {
        Component parent = ((Component) e.getSource()).getParent().getParent().getParent();
        new SelectNavigationPanelAction().actionPerformed(new ActionEvent(parent, e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.debug("mouseClicked");

        if (e.getClickCount() == 2) {
            JTable table = (JTable) e.getSource();
            int rowIndex = table.getSelectedRow();
            LOGGER.debug("Double Clicked on row with index " + rowIndex);

            if (rowIndex != -1) {
                new ExecuteFileAction().actionPerformed(new ActionEvent(e.getSource(), e.getID(), ActionType.EXECUTE_FILE.toString()));
            }
        }
    }
}
