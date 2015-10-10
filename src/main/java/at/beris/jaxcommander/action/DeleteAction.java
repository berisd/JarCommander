/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.action;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.ui.NavigationPanel;
import at.beris.jaxcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class DeleteAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(DeleteAction.class);
    public static final ActionType KEY = ActionType.DELETE;

    public DeleteAction() {
        super();

        keyStrokeString = "F8";
        putValue(Action.NAME, "Delete");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(Action.ACTION_COMMAND_KEY, "delete");
        putValue(Action.SHORT_DESCRIPTION, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.info("Delete");

        Application application = (Application) SwingUtilities.getRoot((Component) event.getSource());
        SessionPanel sessionPanel = application.getSessionPanel();
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();

        if (sourcePanel.getSelection().size() == 0) {
            JOptionPane.showMessageDialog(application, "Nothing selected!");
        } else {
            int deletion = JOptionPane.showConfirmDialog(application, "Delete current selection?", "Deletion", JOptionPane.YES_NO_OPTION);
            if (deletion == JOptionPane.YES_OPTION) {
                for (File file : sourcePanel.getSelection()) {
                    file.delete();
                }
                sourcePanel.refreshDirectory();
            }
        }
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
