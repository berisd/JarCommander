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
import at.beris.virtualfile.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class DeleteAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteAction.class);

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
            int deletion = JOptionPane.showConfirmDialog(context.getApplicationWindow(), "Delete " + countFileList(sourcePanel.getSelection()) + " items?", "Deletion", JOptionPane.YES_NO_OPTION);
            if (deletion == JOptionPane.YES_OPTION) {
                for (VirtualFile file : sourcePanel.getSelection()) {
                    if (file.getName().equals(".."))
                        continue;
                    file.delete();
                }
                sourcePanel.refresh();
            }
        }
    }

    private int countFileList(List<VirtualFile> fileList) {
        int count = 0;

        for (VirtualFile file : fileList) {
            if (file.getName().equals(".."))
                continue;
            count++;
        }

        return count;
    }
}
