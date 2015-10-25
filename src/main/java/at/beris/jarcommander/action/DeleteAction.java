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
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
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
        LOGGER.debug("Delete");

        Application application = (Application) SwingUtilities.getRoot((Component) event.getSource());
        SessionPanel sessionPanel = (SessionPanel) ApplicationContext.getSessionsPanel().getTabComponentAt(0);
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();

        if (sourcePanel.getSelection().size() == 0) {
            JOptionPane.showMessageDialog(application, "Nothing selected!");
        } else {
            int deletion = JOptionPane.showConfirmDialog(application, "Delete " + sourcePanel.getSelection().size() + " items?", "Deletion", JOptionPane.YES_NO_OPTION);
            if (deletion == JOptionPane.YES_OPTION) {
                for (JFile jFile : sourcePanel.getSelection()) {
                    File file = (File) jFile.getBaseObject();
                    file.delete();
                }
                sourcePanel.refresh();
            }
        }
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
