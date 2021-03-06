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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;

import static at.beris.jarcommander.Application.logException;

public class MakeDirAction extends CustomAction {
    private final static Logger LOGGER = LoggerFactory.getLogger(MakeDirAction.class);

    public MakeDirAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "F7";
        putValue(Action.NAME, "MkDir");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
        putValue(Action.SHORT_DESCRIPTION, "Make Directory");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.debug("MkDir");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();

        String newDirectoryMame = JOptionPane.showInputDialog("New directory name");

        if (newDirectoryMame != null) {
            File newDirectory = new File(sourcePanel.getCurrentFile().toString(), newDirectoryMame);

            try {
                Files.createDirectory(newDirectory.toPath());
                sourcePanel.refresh();
            } catch (AccessDeniedException ex) {
                JOptionPane.showMessageDialog(context.getApplicationWindow(), "Access Denied!" + System.lineSeparator() + "(No permissions?)", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                logException(ex);
            }
        }
    }
}
