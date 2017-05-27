/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.jarcommander.ui.table.FileTable;
import at.beris.jarcommander.ui.table.FileTablePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ScrollToTopAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScrollToTopAction.class);

    public ScrollToTopAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "HOME";
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("ScrollToTopAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();
        FileTable fileTable = navigationPanel.getFileTable();
        FileTablePane fileTablePane = navigationPanel.getFileTablePane();

        if (fileTable.getRowCount() > 0) {
            fileTablePane.scrollToRow(0);
            fileTable.repaint();
        }
    }
}
