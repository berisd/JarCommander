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

public class ScrollToTopAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(ScrollToTopAction.class);

    public ScrollToTopAction() {
        super();

        keyStrokeString = ActionType.SCROLL_TO_TOP.getKeyStrokeString();
        putValue(Action.NAME, "Scroll to top");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_HOME);
        putValue(Action.ACTION_COMMAND_KEY, ActionType.SCROLL_TO_TOP.toString());
        putValue(Action.SHORT_DESCRIPTION, "Scroll to top");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("ScrollToTopAction");
        SessionPanel sessionPanel = (SessionPanel) ApplicationContext.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();
        FileTable fileTable = navigationPanel.getFileTable();
        FileTablePane fileTablePane = navigationPanel.getFileTablePane();

        if (fileTable.getRowCount() > 0) {
            fileTablePane.scrollToRow(0);
            fileTable.repaint();
        }
    }
}
