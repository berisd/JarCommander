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
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ScrollToBottomAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(ScrollToBottomAction.class);

    public ScrollToBottomAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "END";
        putValue(Action.NAME, "Scroll to bottom");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_END);
        putValue(Action.SHORT_DESCRIPTION, "Scroll to bottom");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("ScrollToBottomAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();
        FileTable fileTable = navigationPanel.getFileTable();
        FileTablePane fileTablePane = navigationPanel.getFileTablePane();

        if (fileTable.getRowCount() > 0) {
            fileTablePane.scrollToRow(fileTable.getRowCount());
            fileTable.repaint();
        }
    }
}
