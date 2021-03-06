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
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.virtualfile.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

public class NavigatePathUpAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(NavigatePathUpAction.class);

    public NavigatePathUpAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "BACK_SPACE";
        putValue(Action.NAME, "Navigate path up");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_BACK_SPACE);
        putValue(Action.SHORT_DESCRIPTION, "Navigate path up");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Navigate Path up");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();

        URL backUrl = UrlUtils.newUrl(navigationPanel.getCurrentFile().getUrl(), "/../");
        navigationPanel.changeDirectory(Application.getContext().getFileManager().resolveFile(backUrl));

    }
}
