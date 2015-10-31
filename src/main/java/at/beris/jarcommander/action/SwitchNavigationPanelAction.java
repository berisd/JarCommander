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
import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SwitchNavigationPanelAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(SwitchNavigationPanelAction.class);
    public static final ActionType KEY = ActionType.SWITCH_NAVIGATION_PANEL;

    public SwitchNavigationPanelAction() {
        super();

        keyStrokeString = "tab";
        putValue(Action.NAME, "SwitchNavigationPanel");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_TAB);
        putValue(Action.ACTION_COMMAND_KEY, "Switch Navigation Panel");
        putValue(Action.SHORT_DESCRIPTION, "Switch Navigation Panel");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Switch Navigation Panel");
        SessionPanel sessionPanel = (SessionPanel) ApplicationContext.getSessionsPanel().getSelectedComponent();
        sessionPanel.switchSelectedNavigationPanel();
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
