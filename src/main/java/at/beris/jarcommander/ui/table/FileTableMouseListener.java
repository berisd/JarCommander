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
import at.beris.jarcommander.action.ParamActionEvent;
import at.beris.jarcommander.filesystem.file.JFile;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileTableMouseListener extends MouseAdapter {
    private final static Logger LOGGER = Logger.getLogger(FileTableMouseListener.class);

    @Override
    public void mousePressed(MouseEvent e) {
        ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent().getParent();
        parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.debug("mouseClicked");

        ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent().getParent();

        if (e.getClickCount() == 2) {
            JTable table = (JTable) e.getSource();
            int rowIndex = table.getSelectedRow();
            LOGGER.debug("Double Clicked on row with index " + rowIndex);

            if (rowIndex != -1) {
                JFile file = (JFile) table.getValueAt(rowIndex, 0);
                parent.actionPerformed(new ParamActionEvent(e.getSource(), e.getID(), ActionType.EXECUTE_FILE.toString(), file));
            }
        }
    }
}
