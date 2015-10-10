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
import at.beris.jaxcommander.NavigationPanel;
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
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;

public class MakeDirAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(MakeDirAction.class);
    public static final ActionType KEY = ActionType.MAKE_DIR;

    public MakeDirAction() {
        super();

        keyStrokeString = "F7";
        putValue(Action.NAME, "MkDir");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
        putValue(Action.ACTION_COMMAND_KEY, "mkdir");
        putValue(Action.SHORT_DESCRIPTION, "Make Directory");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOGGER.info("MkDir");
        Application application = (Application) SwingUtilities.getRoot((Component) event.getSource());
        SessionPanel sessionPanel = application.getSessionPanel();
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();

        String newDirectoryMame = JOptionPane.showInputDialog("New directory name");

        File newDirectory = new File(sourcePanel.getCurrentPath().toString(), newDirectoryMame);

        try {
            Files.createDirectory(newDirectory.toPath());
        } catch (AccessDeniedException ex) {
            JOptionPane.showMessageDialog(application, "Access Denied!" + System.lineSeparator() + "(No permissions?)", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
