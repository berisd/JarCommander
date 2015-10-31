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
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

public class SelectNavigationPanelAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(SelectNavigationPanelAction.class);

    public SelectNavigationPanelAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("SelectNavigationPanelAction");
        SessionPanel sessionPanel = (SessionPanel) ApplicationContext.getSessionsPanel().getSelectedComponent();
        NavigationPanel leftNavigationPanel = sessionPanel.getLeftNavigationPanel();
        NavigationPanel rightNavigationPanel = sessionPanel.getRightNavigationPanel();

        if (e.getSource() instanceof NavigationPanel) {
            NavigationPanel navigationPanel = (NavigationPanel) e.getSource();
            leftNavigationPanel.setSelected(leftNavigationPanel.equals(navigationPanel));
            rightNavigationPanel.setSelected(rightNavigationPanel.equals(navigationPanel));
        }
    }
}
