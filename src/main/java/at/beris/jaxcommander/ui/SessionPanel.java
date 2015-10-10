/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.NavigationPanel;
import org.apache.log4j.Logger;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SessionPanel extends JTabbedPane {
    private final static Logger LOGGER = Logger.getLogger(SessionPanel.class);

    private NavigationPanel leftNavigationPanel;
    private NavigationPanel rightNavigationPanel;

    public SessionPanel() {
        super();

        addMouseListener(new MouseListener());

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(1, 2);

        panel.setLayout(layout);
        leftNavigationPanel = new NavigationPanel();
        leftNavigationPanel.setSelected(true);
        panel.add(leftNavigationPanel);
        rightNavigationPanel = new NavigationPanel();
        rightNavigationPanel.setSelected(false);
        panel.add(rightNavigationPanel);

        addTab("Local", panel);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            LOGGER.info("mouseClicked");
            super.mouseClicked(e);

            if (e.getSource() instanceof NavigationPanel) {
                NavigationPanel navigationPanel = (NavigationPanel) e.getSource();
                leftNavigationPanel.setSelected(leftNavigationPanel.equals(navigationPanel));
                rightNavigationPanel.setSelected(rightNavigationPanel.equals(navigationPanel));
            }
        }
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
}
