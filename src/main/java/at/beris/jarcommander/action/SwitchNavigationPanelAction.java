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
import at.beris.jarcommander.ui.SessionPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SwitchNavigationPanelAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(SwitchNavigationPanelAction.class);

    public SwitchNavigationPanelAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "TAB";
        putValue(Action.NAME, "SwitchNavigationPanel");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_TAB);
        putValue(Action.SHORT_DESCRIPTION, "Switch Navigation Panel");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Switch Navigation Panel");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        sessionPanel.switchSelectedNavigationPanel();
    }
}
