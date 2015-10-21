/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.VirtualFile;
import at.beris.jaxcommander.action.ParamActionEvent;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static at.beris.jaxcommander.action.ActionCommand.*;

public class FileTableMouseListener extends MouseAdapter {
    private final static Logger LOGGER = Logger.getLogger(FileTableMouseListener.class);

    @Override
    public void mousePressed(MouseEvent e) {
        ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent().getParent();
        parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
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
                VirtualFile file = (VirtualFile) table.getValueAt(rowIndex, 0);

                if (file.isDirectory()) {
                    parent.actionPerformed(new ParamActionEvent(e.getSource(), e.getID(), CHANGE_DIRECTORY, file));
                } else {
                    parent.actionPerformed(new ParamActionEvent(e.getSource(), e.getID(), EXECUTE_FILE, file));
                }
            }
        }
    }
}
