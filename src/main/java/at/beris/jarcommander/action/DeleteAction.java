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
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class DeleteAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(DeleteAction.class);

    public DeleteAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "F8";
        putValue(Action.NAME, "Delete");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(Action.SHORT_DESCRIPTION, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.debug("Delete");

        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();

        if (sourcePanel.getSelection().size() == 0) {
            JOptionPane.showMessageDialog(context.getApplicationWindow(), "Nothing selected!");
        } else {
            int deletion = JOptionPane.showConfirmDialog(context.getApplicationWindow(), "Delete " + sourcePanel.getSelection().size() + " items?", "Deletion", JOptionPane.YES_NO_OPTION);
            if (deletion == JOptionPane.YES_OPTION) {
                for (IFile iFile : sourcePanel.getSelection()) {
                    File file = (File) iFile.getBaseObject();
                    file.delete();
                }
                sourcePanel.refresh();
            }
        }
    }
}
