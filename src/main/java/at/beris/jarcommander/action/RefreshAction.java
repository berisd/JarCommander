/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RefreshAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(RefreshAction.class);
    public static final ActionType KEY = ActionType.REFRESH;

    public RefreshAction() {
        super();

        keyStrokeString = "ctrl R";
        putValue(Action.NAME, "Refresh");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
        putValue(Action.ACTION_COMMAND_KEY, "refresh");
        putValue(Action.SHORT_DESCRIPTION, "Refresh");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Refresh");
        Application application = (Application) SwingUtilities.getRoot((Component) e.getSource());

        SessionPanel sessionPanel = application.getSessionPanel();
        sessionPanel.getSelectedNavigationPanel().refresh();
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
