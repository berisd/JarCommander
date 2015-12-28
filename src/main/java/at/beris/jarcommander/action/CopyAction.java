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
import at.beris.jarcommander.ui.dialog.CopyTaskDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CopyAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(CopyAction.class);

    public CopyAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "F5";
        putValue(Action.NAME, "Copy");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        putValue(Action.SHORT_DESCRIPTION, "Copy");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Copy");

        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        final NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();
        final NavigationPanel targetPanel;
        if (sessionPanel.getLeftNavigationPanel().equals(sourcePanel))
            targetPanel = sessionPanel.getRightNavigationPanel();
        else
            targetPanel = sessionPanel.getLeftNavigationPanel();

        if (sourcePanel.getSelection().size() == 0) {
            JOptionPane.showMessageDialog(context.getApplicationWindow(), "No items selected!");
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CopyTaskDialog copyDialog = new CopyTaskDialog(sourcePanel.getSelection(), targetPanel, context);
                    copyDialog.setVisible(true);
                }
            });
        }
    }
}
