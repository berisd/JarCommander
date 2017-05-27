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
import at.beris.jarcommander.filesystem.drive.Drive;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public class ChangeDriveAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChangeDriveAction.class);

    public ChangeDriveAction(ApplicationContext context) {
        super(context);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("SelectNavigationPanelAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();

        Drive driveInfo = (Drive) ((ParamActionEvent) e).getParam();
        navigationPanel.changeDirectory(driveInfo.getFile());
    }
}
