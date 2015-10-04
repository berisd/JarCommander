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

public class SessionPanel extends JTabbedPane {
    private final static Logger LOGGER = Logger.getLogger(SessionPanel.class);

    private NavigationPanel navigationPanelLeft;
    private NavigationPanel navigationPanelRight;

    public SessionPanel() {
        super();

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(1, 2);

        panel.setLayout(layout);
        navigationPanelLeft = new NavigationPanel();
        panel.add(navigationPanelLeft);
        navigationPanelRight = new NavigationPanel();
        panel.add(navigationPanelRight);


        addTab("Local", panel);
    }

    public void copy() {
        LOGGER.info("copy");
    }
}
