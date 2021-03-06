/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class SessionPanel extends JPanel {
    private final static Logger LOGGER = LoggerFactory.getLogger(SessionPanel.class);

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
        selectNavigationPanel(!leftNavigationPanel.isSelected());
    }

    public void selectRightNavigationPanel() {
        selectNavigationPanel(false);
    }

    private void selectNavigationPanel(boolean selectLeftPanel) {
        leftNavigationPanel.setSelected(selectLeftPanel);
        rightNavigationPanel.setSelected(!selectLeftPanel);

        (selectLeftPanel ? leftNavigationPanel : rightNavigationPanel).getFileTable().requestFocusInWindow();
    }
}
