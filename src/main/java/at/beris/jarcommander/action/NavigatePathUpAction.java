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
import at.beris.jarcommander.filesystem.file.JFileFactory;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class NavigatePathUpAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(NavigatePathUpAction.class);

    public NavigatePathUpAction() {
        super();

        keyStrokeString = ActionType.NAVIGATE_PATH_UP.getKeyStrokeString();
        putValue(Action.NAME, "Navigate path up");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_BACK_SPACE);
        putValue(Action.ACTION_COMMAND_KEY, ActionType.NAVIGATE_PATH_UP.toString());
        putValue(Action.SHORT_DESCRIPTION, "Navigate path up");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Navigate Path up");
        SessionPanel sessionPanel = (SessionPanel) ApplicationContext.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();

        navigationPanel.changeDirectory(JFileFactory.newInstance(new File("..")).toPath());
    }
}
