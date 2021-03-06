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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;

public class SelectNavigationPanelAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(SelectNavigationPanelAction.class);

    public SelectNavigationPanelAction(ApplicationContext context) {
        super(context);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("SelectNavigationPanelAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel leftNavigationPanel = sessionPanel.getLeftNavigationPanel();
        NavigationPanel rightNavigationPanel = sessionPanel.getRightNavigationPanel();

        NavigationPanel navigationPanel = (NavigationPanel) context.findComponentAncestor(NavigationPanel.class, (Component) e.getSource());

        if (navigationPanel != null) {
            leftNavigationPanel.setSelected(leftNavigationPanel.equals(navigationPanel));
            rightNavigationPanel.setSelected(rightNavigationPanel.equals(navigationPanel));
        }
    }
}
