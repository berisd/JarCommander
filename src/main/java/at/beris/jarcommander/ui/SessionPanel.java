/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import org.apache.log4j.Logger;

import javax.swing.JPanel;
import java.awt.GridLayout;

public class SessionPanel extends JPanel {
    private final static Logger LOGGER = Logger.getLogger(SessionPanel.class);

    private NavigationPanel leftNavigationPanel;
    private NavigationPanel rightNavigationPanel;

    public SessionPanel(NavigationPanel leftNavigationPanel, NavigationPanel rightNavigationPanel) {
        super();

        this.leftNavigationPanel = leftNavigationPanel;
        this.rightNavigationPanel = rightNavigationPanel;

        setLayout(new GridLayout(1, 2));
        leftNavigationPanel.setSelected(true);
        add(leftNavigationPanel);
        rightNavigationPanel.setSelected(false);
        add(rightNavigationPanel);
    }

    public NavigationPanel getLeftNavigationPanel() {
        return leftNavigationPanel;
    }

    public NavigationPanel getRightNavigationPanel() {
        return rightNavigationPanel;
    }

    public NavigationPanel getSelectedNavigationPanel() {
        NavigationPanel selectedPanel = null;

        if (selectedPanel == null && leftNavigationPanel.isSelected())
            selectedPanel = leftNavigationPanel;
        if (selectedPanel == null && rightNavigationPanel.isSelected())
            selectedPanel = rightNavigationPanel;

        return selectedPanel;
    }

    public void switchSelectedNavigationPanel() {
        leftNavigationPanel.setSelected(!leftNavigationPanel.isSelected());
        rightNavigationPanel.setSelected(!rightNavigationPanel.isSelected());
    }
}
