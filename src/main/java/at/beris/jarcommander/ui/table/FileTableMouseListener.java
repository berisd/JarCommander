/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.ExecuteFileAction;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileTableMouseListener extends MouseAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileTableMouseListener.class);

    private ApplicationContext context;

    public FileTableMouseListener(ApplicationContext context) {
        super();
        this.context = context;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        context.invokeAction(SelectNavigationPanelAction.class, e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.debug("mouseClicked");

        if (e.getClickCount() == 2) {
            JTable table = (JTable) e.getSource();
            int rowIndex = table.getSelectedRow();
            LOGGER.debug("Double Clicked on row with index " + rowIndex);

            if (rowIndex != -1) {
                context.invokeAction(ExecuteFileAction.class, e);
            }
        }
    }
}
